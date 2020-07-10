package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.model.Summary;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liyunfeng31
 */
@Mapper
public interface SummaryMapper {

    /**
     * records
     * @param records records
     * @return row
     */
    int saveOrUpdate(Summary records);

}