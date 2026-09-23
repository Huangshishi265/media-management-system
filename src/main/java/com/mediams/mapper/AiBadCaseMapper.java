package com.mediams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mediams.entity.AiBadCase;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 打标签 bad case 记录 Mapper
 */
@Mapper
public interface AiBadCaseMapper extends BaseMapper<AiBadCase> {
}
