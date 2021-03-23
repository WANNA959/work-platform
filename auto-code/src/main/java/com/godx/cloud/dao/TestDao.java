package com.godx.cloud.dao;

import com.godx.cloud.entity.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Test)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-23 17:31:05
 */
public interface TestDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Test queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Test> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param test 实例对象
     * @return 对象列表
     */
    List<Test> queryAll(Test test);

    /**
     * 新增数据
     *
     * @param test 实例对象
     * @return 影响行数
     */
    int insert(Test test);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Test> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Test> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Test> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Test> entities);

    /**
     * 修改数据
     *
     * @param test 实例对象
     * @return 影响行数
     */
    int update(Test test);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

