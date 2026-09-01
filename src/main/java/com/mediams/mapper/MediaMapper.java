package com.mediams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mediams.entity.Media;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 素材 Mapper
 */
@Mapper
public interface MediaMapper extends BaseMapper<Media> {

    /**
     * 查询热门素材（按下载量排序）
     */
    @Select("SELECT m.*, c.category_name, u.nickname as create_by_name FROM media m " +
            "LEFT JOIN media_category c ON m.category_id = c.id " +
            "LEFT JOIN sys_user u ON m.create_by = u.id " +
            "WHERE m.deleted = 0 AND m.status = 1 " +
            "ORDER BY m.download_count DESC, m.view_count DESC " +
            "LIMIT #{limit}")
    List<Media> selectHotMedia(@Param("limit") int limit);

    /**
     * 查询推荐素材
     */
    @Select("SELECT m.*, c.category_name, u.nickname as create_by_name FROM media m " +
            "LEFT JOIN media_category c ON m.category_id = c.id " +
            "LEFT JOIN sys_user u ON m.create_by = u.id " +
            "WHERE m.deleted = 0 AND m.status = 1 AND m.is_recommend = 1 " +
            "ORDER BY m.create_time DESC " +
            "LIMIT #{limit}")
    List<Media> selectRecommendMedia(@Param("limit") int limit);

    /**
     * 统计各类型素材数量
     */
    @Select("SELECT media_type, COUNT(*) as count FROM media WHERE deleted = 0 AND status = 1 GROUP BY media_type")
    List<MediaCountByType> countByMediaType();

    /**
     * 统计各分类素材数量
     */
    @Select("SELECT c.id, c.category_name, COUNT(m.id) as media_count FROM media_category c " +
            "LEFT JOIN media m ON c.id = m.category_id AND m.deleted = 0 AND m.status = 1 " +
            "WHERE c.deleted = 0 " +
            "GROUP BY c.id, c.category_name")
    List<CategoryMediaCount> countByCategory();

    /**
     * 根据标签搜索素材
     */
    @Select("SELECT m.*, c.category_name, u.nickname as create_by_name FROM media m " +
            "LEFT JOIN media_category c ON m.category_id = c.id " +
            "LEFT JOIN sys_user u ON m.create_by = u.id " +
            "WHERE m.deleted = 0 AND m.status = 1 AND m.tags LIKE CONCAT('%', #{tag}, '%') " +
            "ORDER BY m.create_time DESC")
    List<Media> selectByTag(@Param("tag") String tag);

    /**
     * 内部接口：素材数量统计结果
     */
    interface MediaCountByType {
        String getMediaType();
        Integer getCount();
    }

    /**
     * 内部接口：分类素材数量统计结果
     */
    interface CategoryMediaCount {
        Long getId();
        String getCategoryName();
        Integer getMediaCount();
    }
}