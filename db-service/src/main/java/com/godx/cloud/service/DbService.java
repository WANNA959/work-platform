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

@FeignClient("auto-code")
@Component
@Deprecated
public interface DbService {

    @GetMapping("/api/code/selectOnex")
    public DbInfo selectOne(@RequestParam("id") Integer id);

    @PostMapping("/api/code/insert")
    public CommonResult insertItem(@SpringQueryMap DownloadInfo info);
}
