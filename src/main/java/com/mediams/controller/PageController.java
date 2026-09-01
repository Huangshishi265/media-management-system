package com.mediams.controller;

import com.mediams.entity.Media;
import com.mediams.service.CategoryService;
import com.mediams.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 页面控制器 - Thymeleaf 页面渲染
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
public class PageController {

    private final MediaService mediaService;
    private final CategoryService categoryService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model) {
        // 热门素材
        List<Media> hotMedia = mediaService.getHotMedia(8);
        model.addAttribute("hotMedia", hotMedia);
        
        // 推荐素材
        List<Media> recommendMedia = mediaService.getRecommendMedia(8);
        model.addAttribute("recommendMedia", recommendMedia);
        
        // 分类列表
        List<com.mediams.entity.Category> categories = categoryService.getCategoriesWithMediaCount();
        model.addAttribute("categories", categories);
        
        return "index";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 仪表盘页面
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 热门素材
        List<Media> hotMedia = mediaService.getHotMedia(10);
        model.addAttribute("hotMedia", hotMedia);
        
        // 分类列表
        List<com.mediams.entity.Category> categories = categoryService.getCategoriesWithMediaCount();
        model.addAttribute("categories", categories);
        
        return "dashboard";
    }

    /**
     * 素材列表页面
     */
    @GetMapping("/media/list")
    public String mediaList(Model model) {
        List<com.mediams.entity.Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        return "media/list";
    }

    /**
     * 素材详情页面
     */
    @GetMapping("/media/detail/{id}")
    public String mediaDetail(@PathVariable Long id, Model model) {
        Media media = mediaService.getById(id);
        if (media != null) {
            mediaService.incrementViewCount(id);
        }
        model.addAttribute("media", media);
        return "media/detail";
    }

    /**
     * 素材上传页面
     */
    @GetMapping("/media/upload")
    public String mediaUpload(Model model) {
        List<com.mediams.entity.Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        return "media/upload";
    }

    /**
     * 分类管理页面
     */
    @GetMapping("/media/category")
    public String categoryManage() {
        return "media/category";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/system/user")
    public String userManage() {
        return "system/user";
    }

    /**
     * 角色管理页面
     */
    @GetMapping("/system/role")
    public String roleManage() {
        return "system/role";
    }

    /**
     * 菜单管理页面
     */
    @GetMapping("/system/menu")
    public String menuManage() {
        return "system/menu";
    }
}