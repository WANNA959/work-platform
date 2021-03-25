package com.godx.cloud.controller;

import com.godx.cloud.model.Test;
import com.godx.cloud.service.TestService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Test)表控制层
 *
 * @author makejava
 * @since 2021-03-23 17:31:16
 */
@RestController
@RequestMapping("test")
public class TestController {
    /**
     * 服务对象
     */
    @Resource
    private TestService testService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Test selectOne(Integer id) {
        return this.testService.queryById(id);
    }

}
