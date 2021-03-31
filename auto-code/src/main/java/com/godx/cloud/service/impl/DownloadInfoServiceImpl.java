package com.godx.cloud.service.impl;

import com.godx.cloud.dao.DownloadInfoDao;
import com.godx.cloud.model.DownloadInfo;
import com.godx.cloud.model.User;
import com.godx.cloud.service.DownloadInfoService;
import com.godx.cloud.service.UserService;
import com.godx.cloud.utils.OSSUtil;
import com.godx.cloud.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * (DownloadInfo)表服务实现类
 *
 * @author makejava
 * @since 2021-03-25 19:37:51
 */
@Service("downloadInfoService")
@Slf4j
public class DownloadInfoServiceImpl implements DownloadInfoService {
    @Resource
    private DownloadInfoDao downloadInfoDao;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;


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
    public List<DownloadInfo> queryAllByLimit(int offset, int limit, Map<String,Object> map) throws ParseException {
        List<DownloadInfo> downloadInfos = this.downloadInfoDao.queryAllByLimit(offset, limit, map);
        return downloadInfos;
    }

    @Override
    public int queryCount(Map<String,Object> map){
        return this.downloadInfoDao.queryCount(map);
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
    public boolean deleteById(Integer id,Integer userId) {
        DownloadInfo info = this.downloadInfoDao.queryById(id);

        if(info!=null && info.getUserId()==userId){
            return this.downloadInfoDao.deleteById(id) > 0;
        } else{
            return false;
        }
    }

    @Override
    public BufferedInputStream downloadItem(Integer id, String token) throws IOException {
        User user = UserUtil.getUserInfo(redisTemplate,token);
        DownloadInfo info = downloadInfoDao.queryById(id);
        if (info == null) {
            return null;
        }
        //鉴权不匹配
        if(info.getUserId()!=user.getId()){
            return null;
        }
        String fileUrl = info.getOssDetails();
        BufferedInputStream reader = OSSUtil.downloadFromOss(fileUrl);
        return reader;
    }
}
