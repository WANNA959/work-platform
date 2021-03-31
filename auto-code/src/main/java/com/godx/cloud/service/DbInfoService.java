package com.godx.cloud.service;

import com.godx.cloud.model.DbInfo;

import java.util.List;
import java.util.Map;

/**
 * (DbInfo)表服务接口
 *
 * @author makejava
 * @since 2021-03-29 19:49:00
 */
public interface DbInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DbInfo queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DbInfo> queryAllByLimit(int offset, int limit,Map<String,Object> map);

    int queryCount( Map<String,Object> map);

    /**
     * 新增数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    DbInfo insert(DbInfo dbInfo);

    /**
     * 修改数据
     *
     * @param dbInfo 实例对象
     * @return 实例对象
     */
    boolean update(DbInfo dbInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id,Integer userId);

}
