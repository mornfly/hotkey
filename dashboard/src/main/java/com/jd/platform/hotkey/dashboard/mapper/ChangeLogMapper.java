package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChangeLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChangeLog record);

    int insertSelective(ChangeLog record);

    ChangeLog selectByPrimaryKey(Integer id);

    int updateByPk(ChangeLog record);

    List<ChangeLog> listChangeLog(SearchDto param);
}