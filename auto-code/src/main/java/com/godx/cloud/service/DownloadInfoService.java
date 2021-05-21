package com.godx.cloud.service;


import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.DbInfo;
import com.godx.cloud.model.DownloadInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("db-service")
@Component
public interface DownloadInfoService {

    @PostMapping("/api/db/insert")
    public CommonResult insertItem(@SpringQueryMap DownloadInfo info);

    @GetMapping("/api/db/selectOnex")
    DbInfo queryById(@RequestParam("id") Integer id);

}