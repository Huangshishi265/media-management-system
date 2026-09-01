package com.mediams.controller;

import com.mediams.dto.Result;
import com.mediams.entity.Category;
import com.mediams.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/media/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询分类树
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        List<Category> categoryTree = categoryService.getCategoryTree();
        return Result.success(categoryTree);
    }

    /**
     * 查询分类及素材数量
     */
    @GetMapping("/with-count")
    public Result<List<Category>> getCategoriesWithMediaCount() {
        List<Category> categories = categoryService.getCategoriesWithMediaCount();
        return Result.success(categories);
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return Result.success("创建成功", createdCategory);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        return Result.success("更新成功", updatedCategory);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功");
    }

    /**
     * 查询所有分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }
}