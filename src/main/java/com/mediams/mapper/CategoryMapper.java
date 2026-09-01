package com.mediams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mediams.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类（树形结构）
     */
    @Select("SELECT * FROM media_category WHERE deleted = 0 ORDER BY sort ASC, id ASC")
    List<Category> selectAllCategories();

    /**
     * 查询子分类
     */
    @Select("SELECT * FROM media_category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort ASC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询分类及其素材数量
     */
    @Select("SELECT c.*, COUNT(m.id) as media_count FROM media_category c " +
            "LEFT JOIN media m ON c.id = m.category_id AND m.deleted = 0 AND m.status = 1 " +
            "WHERE c.deleted = 0 AND c.parent_id = #{parentId} " +
            "GROUP BY c.id ORDER BY c.sort ASC")
    List<Category> selectWithMediaCount(@Param("parentId") Long parentId);
}