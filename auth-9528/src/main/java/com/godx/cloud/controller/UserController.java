package com.godx.cloud.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.godx.cloud.dao.UserDao;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import com.godx.cloud.service.UserService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;


@RestController
@Slf4j
public class UserController implements constant{

    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;

    @Resource
    private Producer producer;

    @Resource
    private  RedisTemplate redisTemplate;

    @Resource
    private TokenEndpoint tokenEndpoint;

    @Resource
    private ConsumerTokenServices consumerTokenServices;

    @Resource
    private PasswordEncoder passwordEncoder;

    @GetMapping("/oauth/user")
    public CommonResult GetUser(@RequestHeader("Authorization")String token){
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user =(User) redisTemplate.opsForValue().get(tokenKey);
        log.info("token: "+tokenKey);
        if(user!=null){
            log.info(user.toString());
            Map<String,Object> map=new HashMap<>();
            map.put("user",user);
            return new CommonResult(200,"ok",map);
        } else{
            log.info("no user in redis");
            return new CommonResult(STATUS_SUC,"no user");
        }

    }


//    @GetMapping("/oauth/token")
//    public CommonResult getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
//        return custom((OAuth2AccessToken) tokenEndpoint.getAccessToken(principal, parameters),"",null);
//    }

    @PostMapping("/oauth/token")
    public CommonResult postAccessToken(Principal principal, @RequestParam Map<String, String> parameters, @CookieValue("kaptchaOwner") String kaptchaOwner) throws HttpRequestMethodNotSupportedException {
        log.info(parameters.toString());
        String username=parameters.get("username");
        String password=parameters.get("password");
        String code=parameters.get("code");
        String rememberme=parameters.get("rememberme");
        String kaptcha=null;
        String msg="";
        if (!StringUtils.isBlank(kaptchaOwner)){
            String redisKey=RedisKeyUtil.getKaptcharKey(kaptchaOwner);
            kaptcha= (String) redisTemplate.opsForValue().get(redisKey);
        }

        //不区分大小写
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
            msg="验证码不正确！";
            return new CommonResult(STATUS_SUC,msg);
        }

        //账号密码
        int expired=0;
        Map<String, String> map = userService.login(username, password, expired);
        log.info(map.toString());
        msg=map.get("msg");
        //通过
        if(msg==null || "".equals(msg)){
            msg="ok";
            return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody(),msg,username,rememberme.equals("true")?true:false);
        } else{//不通过
            return new CommonResult(STATUS_SUC,msg);
        }
    }

    private CommonResult custom(OAuth2AccessToken accessToken,String msg,String username,boolean remember) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Date newDate=null;
        //默认6 小时
        newDate = DateUtil.offsetHour(new Date(), 6);
        if(remember){
            //7天免登录
            newDate = DateUtil.offsetDay(new Date(), 7);
        }
        token.setExpiration(newDate);
        Map<String, Object> data = new LinkedHashMap(token.getAdditionalInformation());
        data.put("accessToken", token.getValue());
        data.put("expire",token.getExpiresIn());
        if (token.getRefreshToken() != null) {
            data.put("refreshToken", token.getRefreshToken().getValue());
        }
        User user = userService.getUserByUsername(username);
        user.setPassword("");
        data.put("user",user);
        String tokenKey = RedisKeyUtil.getTokenKey(token.getValue());
        log.info("tokenkey:"+tokenKey);
        redisTemplate.opsForValue().set(tokenKey, user, token.getExpiresIn(), TimeUnit.SECONDS);
        return new CommonResult(200,msg,data);
    }

    @GetMapping("/oauth/kaptcha")
    public void getKaptcha(HttpServletResponse response) throws Exception {

        //生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        //重构，存到redis
        String kaptcharOwner= IdUtil.simpleUUID();
        Cookie cookie=new Cookie("kaptchaOwner",kaptcharOwner);
        cookie.setMaxAge(120);
//        cookie.setPath(contextPath);
        response.addCookie(cookie);
        System.out.println("in kaptcha "+text);
        //将验证码存入redis
        String redisKey= RedisKeyUtil.getKaptcharKey(kaptcharOwner);
        redisTemplate.opsForValue().set(redisKey,text,120, TimeUnit.SECONDS);//120秒失效

        //图片返回给浏览器
        response.setContentType("image/png");
        ServletOutputStream os = response.getOutputStream();
//        os.write(image);
        ImageIO.write(image,"png",os);
//        return new CommonResult(STATUS_SUC,MESSAGE_OK);
    }

    @PostMapping("/oauth/register")
    public CommonResult register(User user,String code,@CookieValue("kaptchaOwner") String kaptchaOwner){
        log.info(code);
        log.info(user.toString());

        String kaptcha=null;
        if (!StringUtils.isBlank(kaptchaOwner)){
            String redisKey=RedisKeyUtil.getKaptcharKey(kaptchaOwner);
            kaptcha= (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
            return new CommonResult(400,"验证码不正确");
        }

        CommonResult result = userService.register(user);
        if ("ok".equals(result.getMessage())){
            result.setMessage("注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快查看并激活！");
//            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快查看并激活！");
//            model.addAttribute("target",path+"/index");
//            return "/site/operate-result";
        }
        return result;
    }

    // https://localhost:8088/community/activation/id/code
    //restful格式
    @GetMapping("/oauth/activation/{userId}/{code}")
    public CommonResult activation(@PathVariable("userId") int userId,@PathVariable("code") String code){

        int activation = userService.activation(userId, code);
        String res;
        int retCode=0;
        if (activation==ACTIVATION_SUCCESS){
            retCode=200;
            res="激活成功！您的账号已经可以正常使用了";
//            model.addAttribute("msg","激活成功！您的账号已经可以正常使用了");
//            model.addAttribute("target",path+"/login");
        }
        else if (activation==ACTIVATION_REPEAT){
            retCode=401;
            res="无效的操作，该账号已经激活过了！";
//            model.addAttribute("msg","无效的操作，该账号已经激活过了！");
//            model.addAttribute("target",path+"/index");
        }
        else {
            retCode=401;
            res="激活失败！您提供的激活码不正确！";
//            model.addAttribute("msg","激活失败！您提供的激活码不正确！");
//            model.addAttribute("target",path+"/index");
        }
        return new CommonResult(retCode,res);
//        return "/site/operate-result";
    }

//    @PostMapping("/oauth/modifypass")
//    public CommonResult modifypass(User user,String newPass,String checkNewPass,String code,@CookieValue("kaptchaOwner") String kaptchaOwner){
//
//        String kaptcha=null;
//        if (!StringUtils.isBlank(kaptchaOwner)){
//            String redisKey=RedisKeyUtil.getKaptcharKey(kaptchaOwner);
//            kaptcha= (String) redisTemplate.opsForValue().get(redisKey);
//        }
//
//        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
//            return new CommonResult(400,"验证码不正确");
//        }
//
//        CommonResult result = userService.modifypass(user);
//        if ("ok".equals(result.getMessage())){
//            result.setMessage("请求修改密码成功，我们已经向您的邮箱发送了一封确认邮件，请尽快查看并确认修改密码！");
////            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快查看并激活！");
////            model.addAttribute("target",path+"/index");
////            return "/site/operate-result";
//        }
//        return result;
//    }

    // https://localhost:8088/community/activation/id/code
    //restful格式
    @PostMapping("/oauth/modifyPass/{userId}/{code}")
    public CommonResult modifyPassVerify(@PathVariable("userId") int userId,@PathVariable("code") String code){

        int activation = userService.modifyPassVerify(userId, code);
        String res;
        int retCode=0;
        if (activation==ACTIVATION_SUCCESS){
            retCode=200;
            res="激活成功！您的账号已经可以正常使用了";
//            model.addAttribute("msg","激活成功！您的账号已经可以正常使用了");
//            model.addAttribute("target",path+"/login");
        }
        else if (activation==ACTIVATION_REPEAT){
            retCode=401;
            res="无效的操作，该账号已经激活过了！";
//            model.addAttribute("msg","无效的操作，该账号已经激活过了！");
//            model.addAttribute("target",path+"/index");
        }
        else {
            retCode=401;
            res="激活失败！您提供的激活码不正确！";
//            model.addAttribute("msg","激活失败！您提供的激活码不正确！");
//            model.addAttribute("target",path+"/index");
        }
        return new CommonResult(retCode,res);
//        return "/site/operate-result";
    }

    @GetMapping("/oauth/forget")
    public CommonResult forgetPass(String email,String code,@CookieValue("kaptchaOwner") String kaptchaOwner){
        User user = userService.getUserByEmail(email);
        if(user==null){
            return new CommonResult(400,"不存在该邮箱");
        }
        String kaptcha=null;
        if (!StringUtils.isBlank(kaptchaOwner)){
            String redisKey=RedisKeyUtil.getKaptcharKey(kaptchaOwner);
            kaptcha= (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
            return new CommonResult(400,"验证码不正确");
        }

        CommonResult result = userService.forgetPass(user);
        if ("ok".equals(result.getMessage())){
            result.setMessage("重置成功，我们已经向您的邮箱发送了一封邮件，请尽快查看并重置密码！");
        }
        return result;
    }

    @PostMapping("/oauth/reset")
    public CommonResult reset(int userId,String code,String pass,String checkPass){

        String msg = userService.resetPassVerify(userId, code);
        if(!"ok".equals(msg)){
            return new CommonResult(STATUS_BADREQUEST,msg);
        }
        if(StringUtils.isBlank(pass) || !pass.equals(checkPass)){
            return new CommonResult(STATUS_BADREQUEST,"密码验证失败");
        }
        userDao.updatePassById(userId,passwordEncoder.encode(pass));
        return new CommonResult(STATUS_SUC,"重置成功，请重新登录");
    }

    @PostMapping("/oauth/login2")
    @Deprecated
    public CommonResult login(String username, String password, String code,
                        boolean rememberme, HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner){

        CommonResult result=new CommonResult();
        result.setCode(200);
        //从cookie中取key redis中读取验证码
        String kaptcha=null;
        if (!StringUtils.isBlank(kaptchaOwner)){
            String redisKey=RedisKeyUtil.getKaptcharKey(kaptchaOwner);
            kaptcha= (String) redisTemplate.opsForValue().get(redisKey);
        }

        //不区分大小写
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
            result.setMessage("验证码不正确！");
//            model.addAttribute("codeMsg","验证码不正确！");
//            return "/site/login";
        }

        //账号密码
        int expired=rememberme ? REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, String> map = userService.login(username, password, expired);
        if (map.containsKey("ticket")){
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
//            cookie.setPath(contextPath);//整个项目路径通用
            cookie.setMaxAge(expired);//失效时间
            response.addCookie(cookie);
            result.setMessage("ok");
//            return "redirect:/index";
        }
        else {
            result.setMessage(map.get("msg"));
//            model.addAttribute("usernameMsg",map.get("usernameMsg"));
//            model.addAttribute("passwordMsg",map.get("passwordMsg"));
//            return "/site/login";
        }
        return result;
    }

    @GetMapping("/oauth/logout")
    public CommonResult logout(@RequestHeader("Authorization") String accessToken){
        log.info(accessToken.substring(7));
        if (consumerTokenServices.revokeToken(accessToken.substring(7))) {
            String tokenKey = RedisKeyUtil.getTokenKey(accessToken.substring(7));
            redisTemplate.delete(tokenKey);
            return new CommonResult(STATUS_SUC,MESSAGE_OK);
        }
        return new CommonResult(401,"fail to logout");
    }
}
