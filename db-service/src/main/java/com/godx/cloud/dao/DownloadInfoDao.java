package com.godx.cloud.dao;

import com.godx.cloud.model.DownloadInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * (DownloadInfo)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-25 19:37:50
 */
@Mapper
public interface DownloadInfoDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DownloadInfo queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DownloadInfo> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit, @Param("map") Map<String, Object> map);

    int queryCount(@Param("map") Map<String, Object> map);
    /**
     * 通过实体作为筛选条件查询
     *
     * @param downloadInfo 实例对象
     * @return 对象列表
     */
    List<DownloadInfo> queryAll(DownloadInfo downloadInfo);

    /**
     * 新增数据
     *
     * @param downloadInfo 实例对象
     * @return 影响行数
     */
    int insert(DownloadInfo downloadInfo);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<DownloadInfo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<DownloadInfo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<DownloadInfo> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<DownloadInfo> entities);

    /**
     * 修改数据
     *
     * @param downloadInfo 实例对象
     * @return 影响行数
     */
    int update(DownloadInfo downloadInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

