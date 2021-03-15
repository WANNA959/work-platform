package com.godx.cloud.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.godx.cloud.constant.constant;
import com.godx.cloud.dao.UserDao;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.LoginTicket;
import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService implements constant {

    @Resource
    private UserDao userDao;

    @Resource
    private MailClient mailClient;

    @Resource
    private RedisTemplate redisTemplate;

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
        result.setCode(200);

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
        user.setPassword(DigestUtil.md5Hex(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(IdUtil.simpleUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        int id=userDao.insertUser(user);
        log.info(String.valueOf(id));

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        log.info(user.toString());
//         http://localhost:8080/community/activation/101/code
        String url = "http://localhost:8443/oauth/activation/" + id + "/" + user.getActivationCode();
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
        String word=password + user.getSalt();
        password = DigestUtil.md5Hex(word);
        if (!user.getPassword().equals(password)) {
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

    //todo 基于角色的权限控制
}
