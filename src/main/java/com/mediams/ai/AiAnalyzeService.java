package com.mediams.ai;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mediams.entity.Category;
import com.mediams.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 智能入库分析服务
 *
 * <p>核心链路：图片 → 压缩（降 token）→ glm-4v-flash 看图 → 解析 JSON → 分类名规则映射到现有分类树。
 *
 * <p>产品决策：
 * <ul>
 *   <li>「看图描述」交给模型；「分类映射」用规则兜底（模糊匹配现有分类，匹配不上归「未分类」由人工选）</li>
 *   <li>模型异常/未配置 key 时返回 fallback=true，不抛异常、不阻塞上传主流程</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalyzeService {

    private final ZhipuAiClient zhipuAiClient;
    private final CategoryService categoryService;

    private static final String SYSTEM_PROMPT = "你是一个专业的素材管理助手，负责为图片生成简洁准确的标题、描述和标签，并给出最贴切的分类。";

    public AiTagResult analyze(MultipartFile file) {
        AiTagResult result = new AiTagResult();
        try {
            if (!zhipuAiClient.isConfigured()) {
                result.setFallback(true);
                result.setFallbackMessage("未配置智谱 API Key，请手动填写");
                return result;
            }

            // 1. 压缩图片（最长边 768），降低 token 成本
            byte[] imageBytes = compress(file.getBytes());
            String base64 = Base64.encode(imageBytes);
            String mimeType = resolveMimeType(file);

            // 2. 受控分类字典（few-shot 中的受控枚举）
            List<Category> categories = categoryService.list();
            String categoryList = categories.stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.joining("、"));

            // 3. 调视觉模型
            String reply = zhipuAiClient.chatVision(SYSTEM_PROMPT, buildPrompt(categoryList), base64, mimeType);

            // 4. 解析 JSON
            JSONObject json = JSONUtil.parseObj(extractJson(reply));
            result.setTitle(trimToEmpty(json.getStr("title")));
            result.setDescription(trimToEmpty(json.getStr("description")));
            result.setTags(parseTags(json.getJSONArray("tags")));

            String categoryName = trimToEmpty(json.getStr("category"));
            result.setCategoryName(categoryName);

            // 5. 规则映射：分类名匹配现有分类树（匹配不上为 null）
            result.setCategoryId(matchCategory(categoryName, categories));
            result.setFallback(false);
            return result;
        } catch (Exception e) {
            // 降级：AI 失败不阻塞主流程，返回 fallback 由前端提示手动填写
            log.warn("AI 识别失败，降级为手动填写: {}", e.getMessage());
            result.setFallback(true);
            result.setFallbackMessage("AI 识别失败，请手动填写");
            return result;
        }
    }

    private String buildPrompt(String categoryList) {
        return "请分析这张图片，为素材库生成元数据，要求：\n"
                + "1. title：简洁中文标题，10 字以内\n"
                + "2. description：一句话描述图片内容，30 字以内\n"
                + "3. tags：5~8 个中文标签，描述主体/场景/风格\n"
                + "4. category：从以下分类中选择最贴切的一个：[" + categoryList + "]，若都不匹配填\"未分类\"\n"
                + "只返回 JSON，不要任何其他文字，格式：{\"title\":\"\",\"description\":\"\",\"tags\":[\"\",\"\"],\"category\":\"\"}";
    }

    /** 提取回复中的 JSON（模型可能用 markdown 代码块包裹） */
    private String extractJson(String s) {
        if (s == null) {
            return "{}";
        }
        String t = s.trim();
        int start = t.indexOf('{');
        int end = t.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return t.substring(start, end + 1);
        }
        return "{}";
    }

    private List<String> parseTags(JSONArray arr) {
        List<String> tags = new ArrayList<>();
        if (arr != null) {
            for (Object o : arr) {
                if (o != null && !String.valueOf(o).isBlank()) {
                    tags.add(String.valueOf(o).trim());
                }
            }
        }
        return tags;
    }

    /** 规则映射：模型分类名 → 现有分类树（模糊匹配），失败返回 null */
    private Long matchCategory(String categoryName, List<Category> categories) {
        if (categoryName == null || categoryName.isBlank() || "未分类".equals(categoryName) || categories == null) {
            return null;
        }
        for (Category c : categories) {
            if (c.getCategoryName() != null
                    && (c.getCategoryName().contains(categoryName) || categoryName.contains(c.getCategoryName()))) {
                return c.getId();
            }
        }
        return null;
    }

    private String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private String resolveMimeType(MultipartFile file) {
        String ct = file.getContentType();
        return (ct != null && !ct.isBlank()) ? ct : "image/jpeg";
    }

    /** 图片压缩（JDK ImageIO），最长边 768；失败则返回原图，不影响主流程 */
    private byte[] compress(byte[] bytes) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            if (img == null) {
                return bytes;
            }
            int maxDim = 768;
            int w = img.getWidth();
            int h = img.getHeight();
            if (Math.max(w, h) <= maxDim) {
                return bytes;
            }
            double scale = (double) maxDim / Math.max(w, h);
            int nw = (int) (w * scale);
            int nh = (int) (h * scale);
            int type = img.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_RGB : img.getType();
            BufferedImage scaled = new BufferedImage(nw, nh, type);
            Graphics2D g = scaled.createGraphics();
            g.drawImage(img, 0, 0, nw, nh, null);
            g.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(scaled, "jpeg", out);
            return out.toByteArray();
        } catch (IOException e) {
            log.debug("图片压缩失败，使用原图: {}", e.getMessage());
            return bytes;
        }
    }
}
