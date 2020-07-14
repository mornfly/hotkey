package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyTimelyMapper {

    int clear();

    int deleteByKeyAndApp(String key,String appName);

    int insertSelective(KeyTimely key);

    KeyTimely selectByPrimaryKey(Long id);

    KeyTimely selectByKey(String key);

    int updateByKey(KeyTimely key);

    List<KeyTimely> listKeyTimely(SearchReq param);

    int batchInsert(List<KeyTimely> list);

    void batchDeleted(List<KeyTimely> deleteList);

    void saveOrUpdate(KeyTimely keyTimely);
}