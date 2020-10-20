package com.jd.platform.hotkey.dashboard.biz.service.impl;

import com.jd.platform.hotkey.dashboard.biz.mapper.BizAccessTokenMapper;
import com.jd.platform.hotkey.dashboard.biz.service.IBizAccessTokenService;
import com.jd.platform.hotkey.dashboard.model.BizAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 咚咚消息tokenService业务层处理
 * 
 * @author ruoyi
 * @date 2020-05-21
 */
@Service
public class BizAccessTokenServiceImpl implements IBizAccessTokenService {


    @Resource
    private BizAccessTokenMapper bizAccessTokenMapper;

    /**
     * 查询咚咚消息token
     * 
     * @param id 咚咚消息tokenID
     * @return 咚咚消息token
     */
    @Override
    public BizAccessToken selectBizAccessTokenById(Long id){
        return bizAccessTokenMapper.selectBizAccessTokenById(id);
    }

    /**
     * 查询咚咚消息token列表
     * 
     * @param bizAccessToken 咚咚消息token
     * @return 咚咚消息token
     */
    @Override
    public List<BizAccessToken> selectBizAccessTokenList(BizAccessToken bizAccessToken){
        return bizAccessTokenMapper.selectBizAccessTokenList(bizAccessToken);
    }

    /**
     * 新增咚咚消息token
     * 
     * @param bizAccessToken 咚咚消息token
     * @return 结果
     */
    @Override
    public int insertBizAccessToken(BizAccessToken bizAccessToken){
        return bizAccessTokenMapper.insertBizAccessToken(bizAccessToken);
    }

    /**
     * 修改咚咚消息token
     * 
     * @param bizAccessToken 咚咚消息token
     * @return 结果
     */
    @Override
    public int updateBizAccessToken(BizAccessToken bizAccessToken){
        return bizAccessTokenMapper.updateBizAccessToken(bizAccessToken);
    }

    /**
     * 删除咚咚消息token信息
     * 
     * @param id 咚咚消息tokenID
     * @return 结果
     */
    @Override
    public int deleteBizAccessTokenById(Long id)
    {
        return bizAccessTokenMapper.deleteBizAccessTokenById(id);
    }
}
