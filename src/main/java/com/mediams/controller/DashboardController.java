package com.mediams.controller;

import com.mediams.dto.Result;
import com.mediams.mapper.MediaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MediaMapper mediaMapper;

    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 素材类型统计
        List<MediaMapper.MediaCountByType> mediaTypeStats = mediaMapper.countByMediaType();
        stats.put("mediaTypeStats", mediaTypeStats);
        
        // 分类素材数量统计
        List<MediaMapper.CategoryMediaCount> categoryStats = mediaMapper.countByCategory();
        stats.put("categoryStats", categoryStats);
        
        return Result.success(stats);
    }

    /**
     * 获取素材类型统计
     */
    @GetMapping("/media-type-stats")
    public Result<List<MediaMapper.MediaCountByType>> getMediaTypeStats() {
        List<MediaMapper.MediaCountByType> stats = mediaMapper.countByMediaType();
        return Result.success(stats);
    }

    /**
     * 获取分类素材数量统计
     */
    @GetMapping("/category-stats")
    public Result<List<MediaMapper.CategoryMediaCount>> getCategoryStats() {
        List<MediaMapper.CategoryMediaCount> stats = mediaMapper.countByCategory();
        return Result.success(stats);
    }
}