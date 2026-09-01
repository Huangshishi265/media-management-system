package com.mediams.service;

import com.mediams.dto.PageResult;
import com.mediams.entity.Media;
import com.mediams.mapper.MediaMapper;
import com.mediams.service.impl.MediaServiceImpl;
import com.mediams.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * 素材服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaMapper mediaMapper;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private Media testMedia;

    @BeforeEach
    void setUp() {
        testMedia = Media.builder()
                .id(1L)
                .title("测试素材")
                .description("这是一个测试素材")
                .mediaType("image")
                .categoryId(1L)
                .filePath("media/2024/01/test.jpg")
                .fileSize(1048576L)
                .downloadCount(10)
                .viewCount(100)
                .status(1)
                .build();
    }

    @Test
    @DisplayName("根据ID查询素材 - 成功")
    void testGetById_Success() {
        when(mediaMapper.selectById(1L)).thenReturn(testMedia);

        Media result = mediaService.getById(1L);

        assertNotNull(result);
        assertEquals("测试素材", result.getTitle());
        assertEquals("image", result.getMediaType());
    }

    @Test
    @DisplayName("根据ID查询素材 - 素材不存在")
    void testGetById_NotFound() {
        when(mediaMapper.selectById(999L)).thenReturn(null);

        Media result = mediaService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("上架素材 - 成功")
    void testPublishMedia_Success() {
        when(mediaMapper.updateById(any(Media.class))).thenReturn(1);

        boolean result = mediaService.publishMedia(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("下架素材 - 成功")
    void testUnpublishMedia_Success() {
        when(mediaMapper.updateById(any(Media.class))).thenReturn(1);

        boolean result = mediaService.unpublishMedia(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("查询热门素材 - 成功")
    void testGetHotMedia_Success() {
        List<Media> hotMediaList = new ArrayList<>();
        hotMediaList.add(testMedia);
        hotMediaList.add(Media.builder().id(2L).title("热门素材2").downloadCount(50).build());

        when(mediaMapper.selectHotMedia(anyInt())).thenReturn(hotMediaList);

        List<Media> result = mediaService.getHotMedia(10);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("查询推荐素材 - 成功")
    void testGetRecommendMedia_Success() {
        List<Media> recommendList = new ArrayList<>();
        recommendList.add(testMedia);

        when(mediaMapper.selectRecommendMedia(anyInt())).thenReturn(recommendList);

        List<Media> result = mediaService.getRecommendMedia(5);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("增加下载次数 - 成功")
    void testIncrementDownloadCount_Success() {
        when(mediaMapper.selectById(1L)).thenReturn(testMedia);
        when(mediaMapper.updateById(any(Media.class))).thenReturn(1);

        mediaService.incrementDownloadCount(1L);

        verify(mediaMapper, times(1)).updateById(any(Media.class));
    }

    @Test
    @DisplayName("增加浏览次数 - 成功")
    void testIncrementViewCount_Success() {
        when(mediaMapper.selectById(1L)).thenReturn(testMedia);
        when(mediaMapper.updateById(any(Media.class))).thenReturn(1);

        mediaService.incrementViewCount(1L);

        verify(mediaMapper, times(1)).updateById(any(Media.class));
    }

    @Test
    @DisplayName("设置推荐状态 - 成功")
    void testSetRecommend_Success() {
        when(mediaMapper.updateById(any(Media.class))).thenReturn(1);

        boolean result = mediaService.setRecommend(1L, 1);

        assertTrue(result);
    }
}