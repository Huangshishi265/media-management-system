package com.mediams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mediams.dto.PageResult;
import com.mediams.entity.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 素材服务接口
 */
public interface MediaService extends IService<Media> {

    /**
     * 分页查询素材
     */
    PageResult<Media> pageList(Integer pageNum, Integer pageSize, String title, Long categoryId, String mediaType, Integer status);

    /**
     * 上传素材
     */
    Media uploadMedia(MultipartFile file, String title, String description, Long categoryId, String tags);

    /**
     * 更新素材
     */
    Media updateMedia(Media media);

    /**
     * 上架素材
     */
    boolean publishMedia(Long id);

    /**
     * 下架素材
     */
    boolean unpublishMedia(Long id);

    /**
     * 删除素材
     */
    boolean deleteMedia(Long id);

    /**
     * 查询热门素材
     */
    List<Media> getHotMedia(int limit);

    /**
     * 查询推荐素材
     */
    List<Media> getRecommendMedia(int limit);

    /**
     * 根据标签搜索
     */
    List<Media> searchByTag(String tag);

    /**
     * 增加下载次数
     */
    void incrementDownloadCount(Long id);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);

    /**
     * 设置推荐状态
     */
    boolean setRecommend(Long id, Integer isRecommend);
}