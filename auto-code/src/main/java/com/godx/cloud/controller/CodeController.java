package com.godx.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.DbInfo;
import com.godx.cloud.model.User;
import com.godx.cloud.service.CodeService;
import com.godx.cloud.service.DownloadInfoService;
import com.godx.cloud.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/code")
@Slf4j
public class CodeController implements constant {

    @Resource
    private CodeService codeService;

    @Resource
    private DownloadInfoService dbInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/mybatiscode")
    public CommonResult getMybatisCode(Integer id,String codeType, @RequestHeader("Authorization")String token) throws IOException, SQLException {
        User user = UserUtil.getUserInfo(redisTemplate, token);
        if(user!=null){
            log.info(user.toString());
        } else{
            log.info("no user in redis");
            return new CommonResult(STATUS_BADREQUEST,"fail to delete");
        }

        DbInfo dbInfo = dbInfoService.queryById(id);
        if (user.getId()!=dbInfo.getUserId()){
            return new CommonResult(STATUS_BADREQUEST,"鉴权不匹配");
        }
        String username=dbInfo.getUsername();
        String password=dbInfo.getPassword();
        String host=dbInfo.getHost();
        String port=dbInfo.getPort();
        String database= dbInfo.getDbName();
        String type=codeType;
        String tables= dbInfo.getTables();

        List<String> tableList = (List<String>) JSON.parse(tables);
        List<String> types = (List<String>) JSON.parse(type);
        for(int idx=0; idx<types.size(); idx++){
            String item=types.get(idx);
            int pos=item.indexOf(".");
            item=item.substring(0,pos);
            types.set(idx,item);
        }
        String msg = codeService.getMybatisCode(username,password,host,port,database,tableList,token,types);
        if(StringUtils.isBlank(msg)){
            return new CommonResult(STATUS_SUC,MESSAGE_OK);
        }
        return new CommonResult(STATUS_BADREQUEST,msg);
    }
}
