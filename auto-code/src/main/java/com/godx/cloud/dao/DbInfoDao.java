package com.godx.cloud.dao;

import com.godx.cloud.model.DbInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * (DbInfo)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-29 19:49:00
 */
@Mapper
public interface DbInfoDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DbInfo queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DbInfo> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit,@Param("map") Map<String, Object> map);

    int queryCount(@Param("map") Map<String, Object> map);
    /**
     * 通过实体作为筛选条件查询
     *
     * @param dbInfo 实例对象
     * @return 对象列表
     */
    List<DbInfo> queryAll(DbInfo dbInfo);

    /**
     * 新增数据
     *
     * @param dbInfo 实例对象
     * @return 影响行数
     */
    int insert(DbInfo dbInfo);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<DbInfo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<DbInfo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<DbInfo> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<DbInfo> entities);

    /**
     * 修改数据
     *
     * @param dbInfo 实例对象
     * @return 影响行数
     */
    int update(DbInfo dbInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

