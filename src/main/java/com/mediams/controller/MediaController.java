package com.mediams.controller;

import com.mediams.ai.AiAnalyzeService;
import com.mediams.ai.AiTagResult;
import com.mediams.dto.PageResult;
import com.mediams.dto.Result;
import com.mediams.entity.AiBadCase;
import com.mediams.entity.Media;
import com.mediams.mapper.AiBadCaseMapper;
import com.mediams.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 素材控制器
 */
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final AiAnalyzeService aiAnalyzeService;
    private final AiBadCaseMapper aiBadCaseMapper;

    /**
     * 分页查询素材列表
     */
    @GetMapping("/page")
    public Result<PageResult<Media>> pageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String mediaType,
            @RequestParam(required = false) Integer status) {
        PageResult<Media> pageResult = mediaService.pageList(pageNum, pageSize, title, categoryId, mediaType, status);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询素材
     */
    @GetMapping("/{id}")
    public Result<Media> getById(@PathVariable Long id) {
        Media media = mediaService.getById(id);
        mediaService.incrementViewCount(id); // 增加浏览次数
        return Result.success(media);
    }

    /**
     * 上传素材
     */
    @PostMapping("/upload")
    public Result<Media> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tags) {
        Media media = mediaService.uploadMedia(file, title, description, categoryId, tags);
        return Result.success("上传成功", media);
    }

    /**
     * AI 智能识别：对图片生成标题/描述/标签/分类建议
     * 失败时返回 fallback=true（不阻塞上传主流程），由前端提示手动填写
     */
    @PostMapping("/ai-analyze")
    public Result<AiTagResult> aiAnalyze(@RequestParam("file") MultipartFile file) {
        AiTagResult result = aiAnalyzeService.analyze(file);
        return Result.success(result);
    }

    /**
     * 记录 AI 打标签 bad case：用户采纳或修改 AI 建议，供产品迭代复盘
     */
    @PostMapping("/ai-badcase")
    public Result<Void> recordAiBadCase(@RequestBody AiBadCase badCase) {
        aiBadCaseMapper.insert(badCase);
        return Result.success("已记录");
    }

    /**
     * 更新素材
     */
    @PutMapping("/{id}")
    public Result<Media> update(@PathVariable Long id, @RequestBody Media media) {
        media.setId(id);
        Media updatedMedia = mediaService.updateMedia(media);
        return Result.success("更新成功", updatedMedia);
    }

    /**
     * 上架素材
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        mediaService.publishMedia(id);
        return Result.success("上架成功");
    }

    /**
     * 下架素材
     */
    @PutMapping("/{id}/unpublish")
    public Result<Void> unpublish(@PathVariable Long id) {
        mediaService.unpublishMedia(id);
        return Result.success("下架成功");
    }

    /**
     * 删除素材
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return Result.success("删除成功");
    }

    /**
     * 查询热门素材
     */
    @GetMapping("/hot")
    public Result<List<Media>> getHotMedia(@RequestParam(defaultValue = "10") Integer limit) {
        List<Media> hotMedia = mediaService.getHotMedia(limit);
        return Result.success(hotMedia);
    }

    /**
     * 查询推荐素材
     */
    @GetMapping("/recommend")
    public Result<List<Media>> getRecommendMedia(@RequestParam(defaultValue = "10") Integer limit) {
        List<Media> recommendMedia = mediaService.getRecommendMedia(limit);
        return Result.success(recommendMedia);
    }

    /**
     * 根据标签搜索
     */
    @GetMapping("/search/tag")
    public Result<List<Media>> searchByTag(@RequestParam String tag) {
        List<Media> mediaList = mediaService.searchByTag(tag);
        return Result.success(mediaList);
    }

    /**
     * 设置推荐状态
     */
    @PutMapping("/{id}/recommend")
    public Result<Void> setRecommend(@PathVariable Long id, @RequestParam Integer isRecommend) {
        mediaService.setRecommend(id, isRecommend);
        return Result.success("设置成功");
    }

    /**
     * 下载素材（增加下载次数）
     */
    @PostMapping("/{id}/download")
    public Result<Void> download(@PathVariable Long id) {
        mediaService.incrementDownloadCount(id);
        return Result.success("下载次数已更新");
    }
}