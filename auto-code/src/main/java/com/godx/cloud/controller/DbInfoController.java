package com.godx.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.DbInfo;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.User;
import com.godx.cloud.service.DbInfoService;
import com.godx.cloud.util.DbUtil;
import com.godx.cloud.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (DbInfo)表控制层
 *
 * @author makejava
 * @since 2021-03-29 19:49:01
 */
@RestController
@RequestMapping("/api/code")
@Slf4j
public class DbInfoController implements constant {
    /**
     * 服务对象
     */
    @Resource
    private DbInfoService dbInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/selectOnex")
    public DbInfo selectOne(Integer id) {
        return this.dbInfoService.queryById(id);
    }

    @GetMapping("/getDbList")
    public CommonResult getDbList(@RequestParam Map<String,Object> request, @RequestHeader("Authorization")String token){

        String tmp=null;
        int pageNum=0,pageSize=0;
        if (request.containsKey("pageNum")){
            tmp =(String)request.get("pageNum");
            pageNum = Integer.parseInt(tmp);
        }
        if (request.containsKey("pageSize")){
            tmp =(String)request.get("pageSize");
            pageSize = Integer.parseInt(tmp);
        }

        Map<String,Object> map=new HashMap<>();
        if (request.containsKey("host")){
            tmp =(String)request.get("host");
            map.put("host",tmp);
        }
        if (request.containsKey("dbName")){
            tmp =(String)request.get("dbName");
            map.put("dbName",tmp);
        }
        if (request.containsKey("dbName")){
            tmp =(String)request.get("dbName");
            map.put("dbName",tmp);
        }
        if (request.containsKey("table")){
            tmp =(String)request.get("table");
            map.put("table",tmp);
        }
        if (request.containsKey("beginTime")){
            tmp=(String)request.get("beginTime");
            map.put("beginTime",tmp);
        }
        if (request.containsKey("endTime")){
            tmp=(String)request.get("endTime");
            map.put("endTime",tmp);
        }
        if (request.containsKey("beginTime2")){
            tmp=(String)request.get("beginTime2");
            map.put("beginTime2",tmp);
        }
        if (request.containsKey("endTime2")){
            tmp=(String)request.get("endTime2");
            map.put("endTime2",tmp);
        }
        log.info("request:"+map.toString());

        List<DbInfo> dbInfos = dbInfoService.queryAllByLimit(pageSize*(pageNum-1), pageSize,map);
        Map<String,Object> resMap=new HashMap<>();
        resMap.put("list",dbInfos);
        int total = dbInfoService.queryCount(map);
        resMap.put("total",total);
        return new CommonResult(STATUS_SUC,MESSAGE_OK,resMap);
    }

    @PostMapping("/deleteDbItem")
    public CommonResult deleteItem(@RequestParam Map<String,Object> request,@RequestHeader("Authorization")String token){
        Integer id=null,userId=null;
        if (request.containsKey("id")){
            id =Integer.parseInt((String)request.get("id"));
        } else{
            return new CommonResult(STATUS_BADREQUEST,"fail to delete");
        }
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user =(User) redisTemplate.opsForValue().get(tokenKey);
        log.info("token: "+tokenKey);
        if(user!=null){
            log.info(user.toString());
            userId=user.getId();
        } else{
            log.info("no user in redis");
            return new CommonResult(STATUS_BADREQUEST,"fail to delete");
        }
        boolean ok = dbInfoService.deleteById(id,userId);
        if(ok){
            return new CommonResult(STATUS_SUC,MESSAGE_OK);
        }
        return new CommonResult(STATUS_BADREQUEST,"fail to delete");
    }

    @PostMapping("/updateDbItem")
    public CommonResult updateDbItem(@RequestParam Map<String,Object> request, @RequestHeader("Authorization")String token){
        DbInfo info=new DbInfo();
        if(request.containsKey("id")){
            info.setId(Integer.parseInt((String) request.get("id")));
        } else{
            return new CommonResult(STATUS_BADREQUEST,"fail to update");
        }
        if(request.containsKey("tables")){
            info.setTables((String) request.get("tables"));
        } else {
            return new CommonResult(STATUS_BADREQUEST,"fail to update");
        }
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user =(User) redisTemplate.opsForValue().get(tokenKey);
        log.info("token: "+tokenKey);
        Integer userId=null;
        if(user!=null){
            log.info(user.toString());
            userId=user.getId();
        } else{
            log.info("no user in redis");
            return new CommonResult(STATUS_BADREQUEST,"fail to update");
        }
        DbInfo dbInfo = dbInfoService.queryById(info.getId());
        if(dbInfo.getUserId()!=userId){
            return new CommonResult(STATUS_BADREQUEST,"fail to update");
        }
        boolean ok = dbInfoService.update(info);
        if(ok){
            return new CommonResult(STATUS_SUC,MESSAGE_OK);
        }
        return new CommonResult(STATUS_BADREQUEST,"fail to update");
    }

    @PostMapping("/insertDbItem")
    public CommonResult insertDbItem(DbInfo info, @RequestHeader("Authorization")String token){
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user =(User) redisTemplate.opsForValue().get(tokenKey);
        log.info("token: "+tokenKey);
        Integer userId=null;
        if(user!=null){
            log.info(user.toString());
            userId=user.getId();
        } else{
            log.info("no user in redis");
            return new CommonResult(STATUS_BADREQUEST,"fail to insert");
        }
        Connection connection = DbUtil.mySQLOpen(info.getUsername(), info.getPassword(), info.getHost(), info.getPort(), info.getDbName());
        if(connection==null){
            return new CommonResult(STATUS_BADREQUEST,"无法连接数据库,请输入正确的连接信息！");
        }
        info.setUserId(userId);
        String tmp=info.getTables();
        String[] split = Strings.split(tmp, ',');
        info.setTables(JSON.toJSON(split).toString());
        DbInfo dbInfo = dbInfoService.insert(info);
        if(dbInfo!=null){
            return new CommonResult(STATUS_SUC,MESSAGE_OK);
        }
        return new CommonResult(STATUS_BADREQUEST,"fail to insert");
    }
}
