package com.jd.platform.hotkey.dashboard.biz.mapper;

import com.jd.platform.hotkey.dashboard.model.BizAccessToken;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 咚咚消息tokenMapper接口
 * 
 * @date 2020-05-21
 */
@Mapper
public interface BizAccessTokenMapper {
    /**
     * 查询咚咚消息token
     * 
     * @param id 咚咚消息tokenID
     * @return 咚咚消息token
     */
    public BizAccessToken selectBizAccessTokenById(Long id);

    /**
     * 查询咚咚消息token列表
     * 
     * @param bizAccessToken 咚咚消息token
     * @return 咚咚消息token集合
     */
    public List<BizAccessToken> selectBizAccessTokenList(BizAccessToken bizAccessToken);

    /**
     * 新增咚咚消息token
     * 
     * @param bizAccessToken 咚咚消息token
     * @return 结果
     */
    public int insertBizAccessToken(BizAccessToken bizAccessToken);

    /**
     * 修改咚咚消息token
     * 
     * @param bizAccessToken 咚咚消息token
     * @return 结果
     */
    public int updateBizAccessToken(BizAccessToken bizAccessToken);

    /**
     * 删除咚咚消息token
     * 
     * @param id 咚咚消息tokenID
     * @return 结果
     */
    public int deleteBizAccessTokenById(Long id);

    /**
     * 批量删除咚咚消息token
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizAccessTokenByIds(String[] ids);
}
