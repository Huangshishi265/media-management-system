package com.mediams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mediams.entity.Category;
import com.mediams.exception.BusinessException;
import com.mediams.mapper.CategoryMapper;
import com.mediams.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;

    private static final String CATEGORY_CACHE = "media_category";

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "'tree'")
    public List<Category> getCategoryTree() {
        List<Category> allCategories = categoryMapper.selectAllCategories();
        return buildCategoryTree(allCategories, 0L);
    }

    private List<Category> buildCategoryTree(List<Category> categories, Long parentId) {
        return categories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .peek(category -> category.setChildren(buildCategoryTree(categories, category.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public Category createCategory(Category category) {
        category.setStatus(1);
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public Category updateCategory(Category category) {
        Category existingCategory = categoryMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        categoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional
    @CacheEvict(value = CATEGORY_CACHE, allEntries = true)
    public boolean deleteCategory(Long id) {
        // 检查是否有子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }

        return categoryMapper.deleteById(id) > 0;
    }

    @Override
    @Cacheable(value = CATEGORY_CACHE, key = "'with_count'")
    public List<Category> getCategoriesWithMediaCount() {
        return categoryMapper.selectWithMediaCount(0L);
    }
}