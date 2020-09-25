package com.jd.platform.hotkey.dashboard.biz.service;


import com.jd.platform.hotkey.dashboard.model.BizAccessToken;

import java.util.List;

/**
 * 咚咚消息tokenService接口
 * 
 * @author ruoyi
 * @date 2020-05-21
 */
public interface IBizAccessTokenService 
{
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
     * 删除咚咚消息token信息
     * 
     * @param id 咚咚消息tokenID
     * @return 结果
     */
    public int deleteBizAccessTokenById(Long id);
}
