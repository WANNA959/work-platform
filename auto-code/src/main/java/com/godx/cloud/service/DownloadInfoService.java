package com.godx.cloud.service;

import com.godx.cloud.model.DownloadInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * (DownloadInfo)表服务接口
 *
 * @author makejava
 * @since 2021-03-25 19:37:50
 */
public interface DownloadInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DownloadInfo queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DownloadInfo> queryAllByLimit(int offset, int limit, Map<String,Object> map) throws ParseException;

    int queryCount( Map<String,Object> map);

    /**
     * 新增数据
     *
     * @param downloadInfo 实例对象
     * @return 实例对象
     */
    DownloadInfo insert(DownloadInfo downloadInfo);

    /**
     * 修改数据
     *
     * @param downloadInfo 实例对象
     * @return 实例对象
     */
    DownloadInfo update(DownloadInfo downloadInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id,Integer userId);

    BufferedInputStream downloadItem(Integer id, String token) throws IOException;

}
