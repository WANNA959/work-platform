package com.godx.cloud.service.impl;

import com.godx.cloud.model.DbInfo;
import com.godx.cloud.dao.DbInfoDao;
import com.godx.cloud.service.DbInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * (DbInfo)表服务实现类
 *
 * @author makejava
 * @since 2021-03-29 19:49:00
 */
@Service
@Slf4j
public class DbInfoServiceImpl implements DbInfoService {
    @Resource
    private DbInfoDao dbInfoDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public DbInfo queryById(Integer id) {
        return this.dbInfoDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<DbInfo> queryAllByLimit(int offset, int limit,Map<String,Object> map) {
        return this.dbInfoDao.queryAllByLimit(offset, limit,map);
    }

    @Override
    public int queryCount(Map<String,Object> map){
        return this.dbInfoDao.queryCount(map);
    }

    /**
     * 新增数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    @Override
    public DbInfo insert(DbInfo dbInfo) {
        this.dbInfoDao.insert(dbInfo);
        return dbInfo;
    }

    /**
     * 修改数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(DbInfo dbInfo) {
        return this.dbInfoDao.update(dbInfo)>0;
//        return this.queryById(dbInfo.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id,Integer userId) {
        DbInfo info = this.dbInfoDao.queryById(id);

        if(info!=null && info.getUserId()==userId){
            return this.dbInfoDao.deleteById(id) > 0;
        } else{
            return false;
        }
    }
}
