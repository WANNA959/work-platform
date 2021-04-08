package com.godx.cloud.service;


import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.DownloadInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("auto-code")
@Component
public interface DownloadInfoService {

    @PostMapping("/api/code/insert")
    public CommonResult insertItem(@SpringQueryMap DownloadInfo info);
}
