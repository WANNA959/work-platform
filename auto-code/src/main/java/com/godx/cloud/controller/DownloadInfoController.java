package com.godx.cloud.controller;

import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.DownloadInfo;
import com.godx.cloud.model.User;
import com.godx.cloud.service.DownloadInfoService;
import com.godx.cloud.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (DownloadInfo)表控制层
 *
 * @author makejava
 * @since 2021-03-25 19:37:51
 */
@RestController
@RequestMapping("/api/code")
@Slf4j
public class DownloadInfoController implements constant {
    /**
     * 服务对象
     */
    @Resource
    private DownloadInfoService downloadInfoService;

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public DownloadInfo selectOne(Integer id) {
        return this.downloadInfoService.queryById(id);
    }

    @GetMapping("/getList")
    public CommonResult getList(@RequestParam Map<String,Object> request) throws ParseException {
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
        if (request.containsKey("username")){
            tmp =(String)request.get("username");
            map.put("username",tmp);
        }
        if (request.containsKey("database")){
            tmp =(String)request.get("database");
            map.put("database",tmp);
        }
        if (request.containsKey("table")){
            tmp =(String)request.get("table");
            map.put("table",tmp);
        }
        if (request.containsKey("type")){
            tmp =(String)request.get("type");
            map.put("type",tmp);
        }
        if (request.containsKey("beginTime")){
            tmp=(String)request.get("beginTime");
            map.put("beginTime",tmp);
        }
        if (request.containsKey("endTime")){
            tmp=(String)request.get("endTime");
            map.put("endTime",tmp);
        }
        log.info("request:"+map.toString());
        List<DownloadInfo> downloadInfos = downloadInfoService.queryAllByLimit((pageNum - 1) * pageSize, pageSize, map);
        Map<String,Object> data=new HashMap<>();
        data.put("list",downloadInfos);
        data.put("total",downloadInfoService.queryCount(map));
        log.info((new CommonResult(200,"ok",data)).toString());
        return new CommonResult(STATUS_SUC,"ok",data);
    }

    @GetMapping("/downloadItem")
    public void downloadItem(@RequestParam("id")int id, @RequestHeader("Authorization")String token, HttpServletResponse response) throws IOException {
        BufferedInputStream reader = downloadInfoService.downloadItem(id, token);
//        if(bufferedReader==null){
//            return new CommonResult(STATUS_BADREQUEST,"fail to download");
//        }
        ServletOutputStream out = null;
        //自动判断下载文件类型
        response.setContentType("application/form-data;charset=UTF-8");
        //设置文件头：最后一个参数是设置下载文件名
        response.setHeader("Content-Disposition", "attachment;fileName="+"test.zip");
        out = response.getOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024 * 10];
        String line=null;
        while ((len = reader.read(buffer, 0, 1024 * 10)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        reader.close();
    }

    @PostMapping("/deleteItem")
    public CommonResult deleteItem(@RequestParam Map<String,Object> request,@RequestHeader("Authorization")String token){
        Integer id=null,userId=null;
        if (request.containsKey("id")){
            id =Integer.parseInt((String)request.get("id"));
        } else{
            return new CommonResult(STATUS_BADREQUEST,"fail to delete");
        }
//        if (request.containsKey("userId")){
//            userId =Integer.parseInt((String)request.get("userId"));
//        } else{
//            return new CommonResult(STATUS_BADREQUEST,"fail to delete");
//        }
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
        boolean ok = downloadInfoService.deleteById(id,userId);
        if(ok){
            return new CommonResult(STATUS_SUC,MESSAGE_OK);
        }
        return new CommonResult(STATUS_BADREQUEST,"fail to delete");
    }

}
