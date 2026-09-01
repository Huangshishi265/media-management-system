package com.mediams.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 文件工具类
 */
@Slf4j
@Component
public class FileUtil {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.max-size}")
    private Long maxSize;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String category) throws IOException {
        // 校验文件
        validateFile(file);

        // 生成文件路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = IdUtil.fastSimpleUUID() + getFileExtension(file.getOriginalFilename());
        String relativePath = category + "/" + datePath + "/" + fileName;
        String fullPath = uploadPath + relativePath;

        // 创建目录
        Path directory = Paths.get(uploadPath, category, datePath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // 保存文件
        File targetFile = new File(fullPath);
        file.transferTo(targetFile);

        log.info("文件上传成功: {}", relativePath);
        return relativePath;
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }

        // 校验文件大小
        if (file.getSize() > maxSize) {
            throw new IOException("文件大小超过限制");
        }

        // 校验文件类型
        String extension = getFileExtension(file.getOriginalFilename());
        List<String> allowedExtensions = Arrays.asList(allowedTypes.split(","));
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IOException("不允许的文件类型: " + extension);
        }
    }

    /**
     * 获取文件扩展名
     */
    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String relativePath) {
        try {
            Path path = Paths.get(uploadPath, relativePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("文件删除失败: {}", relativePath, e);
            return false;
        }
    }

    /**
     * 获取文件大小（格式化）
     */
    public String formatFileSize(Long size) {
        if (size == null || size == 0) {
            return "0 B";
        }
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 判断是否为图片类型
     */
    public boolean isImage(String extension) {
        List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
        return imageExtensions.contains(extension.toLowerCase());
    }

    /**
     * 判断是否为视频类型
     */
    public boolean isVideo(String extension) {
        List<String> videoExtensions = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv");
        return videoExtensions.contains(extension.toLowerCase());
    }

    /**
     * 判断是否为音频类型
     */
    public boolean isAudio(String extension) {
        List<String> audioExtensions = Arrays.asList("mp3", "wav", "flac", "aac", "ogg");
        return audioExtensions.contains(extension.toLowerCase());
    }
}