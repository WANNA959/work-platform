package com.godx.cloud.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.godx.cloud.constant.constant;
import com.godx.cloud.dao.UserDao;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.LoginTicket;
import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.bouncycastle.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService implements constant, UserDetailsService {

    @Resource
    private UserDao userDao;

    @Resource
    private MailClient mailClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    //优先从缓存查user
    public User getUserById(@Param("id") int id){
//        User user = userDao.getUserById(id);
        User user = getCache(id);
        System.out.println("in finduser");
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public User getUserByUsername(@Param("username") String name){
        User user = userDao.getUserByUsername(name);
        return user;
    }

    public User getUserByEmail(@Param("email") String email){
        User user = userDao.getUserByEmail(email);
        return user;
    }

    public CommonResult register(User user) {
        Map<String, Object> map = new HashMap<>();
        CommonResult result=new CommonResult();
        result.setCode(400);

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            result.setMessage("账号不能为空!");
            return result;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            result.setMessage("密码不能为空!");
            return result;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            result.setMessage("邮箱不能为空!");
            return result;
        }

        // 验证账号
        User u = userDao.getUserByUsername((user.getUsername()));
        if (u != null) {
            result.setMessage("该账号已存在!");
            return result;
        }

        // 验证邮箱
        u = userDao.getUserByEmail(user.getEmail());
        if (u != null) {
            result.setMessage("该邮箱已被注册!");
            return result;
        }

        // 注册用户
        user.setSalt(IdUtil.simpleUUID().substring(0, 5));
//        user.setPassword(DigestUtil.md5Hex(user.getPassword() + user.getSalt()));
        //改进：使用security的加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(IdUtil.simpleUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userDao.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        log.info(user.toString());
//         http://localhost:8080/community/activation/101/code
        String url = "http://localhost:9528/#/activation?" + "userId="+user.getId() + "&code=" + user.getActivationCode();
        context.setVariable("url", url);
//        String content = templateEngine.process("/mail/activation", context);

        String content2=String.format("<div>\n" +
                "\t\t<p>\n" +
                "\t\t\t<b>%s</b>, 您好!\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\t您正在注册银杏社区BBS账号, 这是一封激活邮件, 请点击\n" +
                "\t\t\t<a href=\"%s\">此链接</a>,\n" +
                "\t\t\t激活您的社区账号!\n" +
                "\t\t</p>\n" +
                "\t</div>",user.getEmail(),url);
        log.info(content2);
        mailClient.sendMail(user.getEmail(), "激活账号", content2);

        result.setCode(200);
        result.setMessage("ok");
        return result;
    }

    public int activation(int userId, String code) {
        User user = userDao.getUserById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userDao.updateStatusById(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public CommonResult modifypass(User user) {
        Map<String, Object> map = new HashMap<>();
        CommonResult result=new CommonResult();
        result.setCode(400);

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            result.setMessage("账号不能为空!");
            return result;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            result.setMessage("密码不能为空!");
            return result;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            result.setMessage("邮箱不能为空!");
            return result;
        }

        // 验证账号
        User u = userDao.getUserByUsername((user.getUsername()));
        if (u != null) {
            result.setMessage("该账号已存在!");
            return result;
        }

        // 验证邮箱
        u = userDao.getUserByEmail(user.getEmail());
        if (u != null) {
            result.setMessage("该邮箱已被注册!");
            return result;
        }

        // 注册用户
        user.setSalt(IdUtil.simpleUUID().substring(0, 5));
//        user.setPassword(DigestUtil.md5Hex(user.getPassword() + user.getSalt()));
        //改进：使用security的加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(IdUtil.simpleUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userDao.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        log.info(user.toString());
//         http://localhost:8080/community/activation/101/code
        String url = "http://localhost:9528/#/activation?" + "userId="+user.getId() + "&code=" + user.getActivationCode();
        context.setVariable("url", url);
//        String content = templateEngine.process("/mail/activation", context);

        String content2=String.format("<div>\n" +
                "\t\t<p>\n" +
                "\t\t\t<b>%s</b>, 您好!\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\t您正在注册work-platform账号, 这是一封激活邮件, 请点击\n" +
                "\t\t\t<a href=\"%s\">此链接</a>,\n" +
                "\t\t\t激活您的账号!\n" +
                "\t\t</p>\n" +
                "\t</div>",user.getEmail(),url);
        log.info(content2);
        mailClient.sendMail(user.getEmail(), "work-platform激活账号", content2);

        result.setCode(200);
        result.setMessage("ok");
        return result;
    }

    public CommonResult forgetPass(User user) {
        Map<String, Object> map = new HashMap<>();
        CommonResult result=new CommonResult();
        result.setCode(400);

        // 验证账号
        if (user.getStatus() == 0) {
            result.setMessage("该账号尚未被激活！");
            return result;
        }

        String code=IdUtil.simpleUUID()+'|'+new Date().getTime();
        userDao.updateCodeById(user.getId(),code);
        log.info(code);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        log.info(user.toString());
//         http://localhost:8080/community/activation/101/code
        String url = "http://localhost:9528/#/resetpass?" + "userId="+user.getId() + "&username="+user.getUsername() + "&code=" + code;
        context.setVariable("url", url);
        String content=String.format("<div>\n" +
                "\t\t<p>\n" +
                "\t\t\t<b>%s</b>, 您好!\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\t您收到这封电子邮件是因为您 (也可能是某人冒充您的名义) 申请了一个找回密码的请求。\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\t假如这不是您本人所申请, 请忽略。\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\t您可以点击<a href=\"%s\">此链接</a>，重新设置您的密码。\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\t请在10min内访问链接，并重新设置新的密码。\n" +
                "\t\t</p>\n" +
                "\t</div>",user.getEmail(),url);
        log.info(content);
        mailClient.sendMail(user.getEmail(), "work-platform找回密码", content);

        result.setCode(200);
        result.setMessage("ok");
        return result;
    }


    public String resetPassVerify(int userId, String code) {
        User user = userDao.getUserById(userId);
        if(user==null){
            return "该用户不存在";
        }
        if(StringUtils.isBlank(user.getResetCode()) || !user.getResetCode().equals(code)){
            return "鉴权不匹配";
        }
        String[] data = Strings.split(code,'|');
        long beginTime=Long.valueOf(data[1]);
        long currentTime=new Date().getTime();
        //十分钟内
        if(beginTime+1000*10*60<currentTime){
            return "重置超时，请重新申请重置密码";
        }
        return "ok";
    }

    public int modifyPassVerify(int userId, String code) {
        User user = userDao.getUserById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userDao.updateStatusById(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }


    public Map<String, String> login(String username, String password, long expiredSeconds) {
        Map<String, String> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("msg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            map.put("msg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("msg", "该账号未激活!");
            return map;
        }

        // 验证密码
//        String word=DigestUtil.md5Hex(password + user.getSalt());
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            map.put("msg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(IdUtil.simpleUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userDao.getUserById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getUserByUsername(username);
        log.info(user.toString());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("admin"));
//        String password = passwordEncoder.encode(user.getPassword());
        String password = user.getPassword();
        return new org.springframework.security.core.userdetails.User(username,password,authorities);
    }

    //todo 基于角色的权限控制
}
