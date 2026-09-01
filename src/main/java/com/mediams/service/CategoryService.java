package com.mediams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mediams.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询分类树
     */
    List<Category> getCategoryTree();

    /**
     * 创建分类
     */
    Category createCategory(Category category);

    /**
     * 更新分类
     */
    Category updateCategory(Category category);

    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);

    /**
     * 查询分类及素材数量
     */
    List<Category> getCategoriesWithMediaCount();
}