package com.godx.cloud.controller;

import cn.hutool.core.util.IdUtil;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import com.godx.cloud.service.UserService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;


@RestController
@Slf4j
public class UserController implements constant{

    @Resource
    private UserService userService;

    @Resource
    private Producer producer;

    @Resource
    private  RedisTemplate redisTemplate;

    @GetMapping("/oauth/user/{id}")
    public CommonResult GetUser(@PathVariable("id") int id){
        User user = userService.getUserById(id);
        return new CommonResult(200,"ok",user);
    }

    @GetMapping("/oauth/kaptcha")
    public String getKaptcha(HttpServletResponse response) throws Exception {

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
        return "/site/login";
    }

    @PostMapping("/oauth/register")
    public CommonResult register(Model model, User user){
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
        if (activation==ACTIVATION_SUCCESS){
            res="激活成功！您的账号已经可以正常使用了";
//            model.addAttribute("msg","激活成功！您的账号已经可以正常使用了");
//            model.addAttribute("target",path+"/login");
        }
        else if (activation==ACTIVATION_REPEAT){
            res="无效的操作，该账号已经激活过了！";
//            model.addAttribute("msg","无效的操作，该账号已经激活过了！");
//            model.addAttribute("target",path+"/index");
        }
        else {
            res="激活失败！您提供的激活码不正确！";
//            model.addAttribute("msg","激活失败！您提供的激活码不正确！");
//            model.addAttribute("target",path+"/index");
        }
        return new CommonResult(200,res);
//        return "/site/operate-result";
    }

    @PostMapping("/login")
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

}
