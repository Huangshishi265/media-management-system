package com.mediams.controller;

import com.mediams.dto.PageResult;
import com.mediams.entity.Media;
import com.mediams.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 素材控制器单元测试
 */
@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    private MockMvc mockMvc;

    private Media testMedia;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();

        testMedia = Media.builder()
                .id(1L)
                .title("测试素材")
                .description("这是一个测试素材")
                .mediaType("image")
                .categoryId(1L)
                .downloadCount(10)
                .viewCount(100)
                .status(1)
                .build();
    }

    @Test
    @DisplayName("分页查询素材接口测试")
    void testPageListEndpoint() throws Exception {
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(testMedia);

        PageResult<Media> pageResult = PageResult.of(mediaList, 1L, 1L, 10L);

        when(mediaService.pageList(anyInt(), anyInt(), null, null, null, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/media/page?pageNum=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("根据ID查询素材接口测试")
    void testGetByIdEndpoint() throws Exception {
        when(mediaService.getById(anyLong())).thenReturn(testMedia);

        mockMvc.perform(get("/api/media/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试素材"));
    }

    @Test
    @DisplayName("查询热门素材接口测试")
    void testGetHotMediaEndpoint() throws Exception {
        List<Media> hotMediaList = new ArrayList<>();
        hotMediaList.add(testMedia);

        when(mediaService.getHotMedia(anyInt())).thenReturn(hotMediaList);

        mockMvc.perform(get("/api/media/hot?limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("查询推荐素材接口测试")
    void testGetRecommendMediaEndpoint() throws Exception {
        List<Media> recommendList = new ArrayList<>();
        recommendList.add(testMedia);

        when(mediaService.getRecommendMedia(anyInt())).thenReturn(recommendList);

        mockMvc.perform(get("/api/media/recommend?limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}