package com.mediams.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mediams.dto.PageResult;
import com.mediams.entity.Media;
import com.mediams.exception.BusinessException;
import com.mediams.mapper.MediaMapper;
import com.mediams.service.MediaService;
import com.mediams.util.FileUtil;
import com.mediams.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 素材服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements MediaService {

    private final MediaMapper mediaMapper;
    private final FileUtil fileUtil;

    private static final String HOT_MEDIA_CACHE = "hot_media";
    private static final String CATEGORY_CACHE = "media_category";

    @Override
    public PageResult<Media> pageList(Integer pageNum, Integer pageSize, String title, Long categoryId, String mediaType, Integer status) {
        LambdaQueryWrapper<Media> wrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            wrapper.like(Media::getTitle, title);
        }
        
        if (categoryId != null) {
            wrapper.eq(Media::getCategoryId, categoryId);
        }
        
        if (mediaType != null && !mediaType.isEmpty()) {
            wrapper.eq(Media::getMediaType, mediaType);
        }
        
        if (status != null) {
            wrapper.eq(Media::getStatus, status);
        }

        wrapper.orderByDesc(Media::getCreateTime);

        Page<Media> page = mediaMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional
    @CacheEvict(value = {HOT_MEDIA_CACHE, CATEGORY_CACHE}, allEntries = true)
    public Media uploadMedia(MultipartFile file, String title, String description, Long categoryId, String tags) {
        try {
            // 上传文件
            String relativePath = fileUtil.uploadFile(file, "media");

            // 确定素材类型
            String extension = fileUtil.getFileExtension(file.getOriginalFilename());
            String mediaType = determineMediaType(extension);

            // 创建素材记录
            Media media = Media.builder()
                    .title(title)
                    .description(description)
                    .mediaType(mediaType)
                    .categoryId(categoryId)
                    .filePath(relativePath)
                    .fileName(IdUtil.fastSimpleUUID() + "." + extension)
                    .originalName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .fileExt(extension)
                    .tags(tags)
                    .downloadCount(0)
                    .viewCount(0)
                    .status(0) // 默认下架
                    .isRecommend(0)
                    .build();

            // 设置创建人
            String username = SecurityUtil.getCurrentUsername();
            if (username != null) {
                // TODO: 根据用户名查询用户ID
                media.setCreateBy(1L);
            }

            mediaMapper.insert(media);
            log.info("素材上传成功: id={}, title={}", media.getId(), media.getTitle());
            
            return media;
        } catch (IOException e) {
            log.error("素材上传失败: {}", e.getMessage());
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    private String determineMediaType(String extension) {
        if (fileUtil.isImage(extension)) {
            return "image";
        } else if (fileUtil.isVideo(extension)) {
            return "video";
        } else if (fileUtil.isAudio(extension)) {
            return "audio";
        } else {
            return "document";
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {HOT_MEDIA_CACHE, CATEGORY_CACHE}, allEntries = true)
    public Media updateMedia(Media media) {
        Media existingMedia = mediaMapper.selectById(media.getId());
        if (existingMedia == null) {
            throw new BusinessException("素材不存在");
        }

        mediaMapper.updateById(media);
        return media;
    }

    @Override
    @Transactional
    @CacheEvict(value = {HOT_MEDIA_CACHE, CATEGORY_CACHE}, allEntries = true)
    public boolean publishMedia(Long id) {
        Media media = new Media();
        media.setId(id);
        media.setStatus(1);
        return mediaMapper.updateById(media) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = {HOT_MEDIA_CACHE, CATEGORY_CACHE}, allEntries = true)
    public boolean unpublishMedia(Long id) {
        Media media = new Media();
        media.setId(id);
        media.setStatus(0);
        return mediaMapper.updateById(media) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = {HOT_MEDIA_CACHE, CATEGORY_CACHE}, allEntries = true)
    public boolean deleteMedia(Long id) {
        // 删除文件
        Media media = mediaMapper.selectById(id);
        if (media != null && media.getFilePath() != null) {
            fileUtil.deleteFile(media.getFilePath());
        }
        
        return mediaMapper.deleteById(id) > 0;
    }

    @Override
    @Cacheable(value = HOT_MEDIA_CACHE, key = "'hot:' + #limit")
    public List<Media> getHotMedia(int limit) {
        return mediaMapper.selectHotMedia(limit);
    }

    @Override
    @Cacheable(value = HOT_MEDIA_CACHE, key = "'recommend:' + #limit")
    public List<Media> getRecommendMedia(int limit) {
        return mediaMapper.selectRecommendMedia(limit);
    }

    @Override
    public List<Media> searchByTag(String tag) {
        return mediaMapper.selectByTag(tag);
    }

    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        mediaMapper.selectById(id); // 确保素材存在
        Media media = new Media();
        media.setId(id);
        // 手动更新下载次数
        Media existing = mediaMapper.selectById(id);
        if (existing != null) {
            existing.setDownloadCount(existing.getDownloadCount() + 1);
            mediaMapper.updateById(existing);
        }
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Media existing = mediaMapper.selectById(id);
        if (existing != null) {
            existing.setViewCount(existing.getViewCount() + 1);
            mediaMapper.updateById(existing);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {HOT_MEDIA_CACHE, CATEGORY_CACHE}, allEntries = true)
    public boolean setRecommend(Long id, Integer isRecommend) {
        Media media = new Media();
        media.setId(id);
        media.setIsRecommend(isRecommend);
        return mediaMapper.updateById(media) > 0;
    }
}