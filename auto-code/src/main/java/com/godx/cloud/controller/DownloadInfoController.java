package com.godx.cloud.controller;

import com.godx.cloud.entity.DownloadInfo;
import com.godx.cloud.service.DownloadInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (DownloadInfo)表控制层
 *
 * @author makejava
 * @since 2021-03-25 19:37:51
 */
@RestController
@RequestMapping("downloadInfo")
public class DownloadInfoController {
    /**
     * 服务对象
     */
    @Resource
    private DownloadInfoService downloadInfoService;

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

}
