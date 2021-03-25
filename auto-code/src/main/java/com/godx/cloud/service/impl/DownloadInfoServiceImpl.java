package com.godx.cloud.service.impl;

import com.godx.cloud.entity.DownloadInfo;
import com.godx.cloud.dao.DownloadInfoDao;
import com.godx.cloud.service.DownloadInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (DownloadInfo)表服务实现类
 *
 * @author makejava
 * @since 2021-03-25 19:37:51
 */
@Service("downloadInfoService")
public class DownloadInfoServiceImpl implements DownloadInfoService {
    @Resource
    private DownloadInfoDao downloadInfoDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public DownloadInfo queryById(Integer id) {
        return this.downloadInfoDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<DownloadInfo> queryAllByLimit(int offset, int limit) {
        return this.downloadInfoDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param downloadInfo 实例对象
     * @return 实例对象
     */
    @Override
    public DownloadInfo insert(DownloadInfo downloadInfo) {
        this.downloadInfoDao.insert(downloadInfo);
        return downloadInfo;
    }

    /**
     * 修改数据
     *
     * @param downloadInfo 实例对象
     * @return 实例对象
     */
    @Override
    public DownloadInfo update(DownloadInfo downloadInfo) {
        this.downloadInfoDao.update(downloadInfo);
        return this.queryById(downloadInfo.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.downloadInfoDao.deleteById(id) > 0;
    }
}
