package com.jd.platform.hotkey.dashboard.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.vo.AppCfgVo;
import com.jd.platform.hotkey.dashboard.biz.service.AppCfgService;
import com.jd.platform.hotkey.dashboard.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: AppCfgServiceImpl
 * @Author: liyunfeng31
 * @Date: 2020/9/2 9:57
 */
@Service
public class AppCfgServiceImpl implements AppCfgService {


    @Resource
    private IConfigCenter configCenter;


    @Override
    public Page<AppCfgVo> pageAppCfgVo(PageReq page, String app) {
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.appCfgPath);
        List<AppCfgVo> cfgVos = new ArrayList<>();
        for (KeyValue kv : keyValues) {
            String v = kv.getValue().toStringUtf8();
            String key = kv.getKey().toStringUtf8();
            if(StringUtil.isEmpty(v)){
                configCenter.put(key, JSON.toJSONString(new AppCfgVo(key)));
                continue;
            }
            AppCfgVo vo = JSON.parseObject(v, AppCfgVo.class);
            vo.setVersion(kv.getModRevision());
            String k = key.replace(ConfigConstant.appCfgPath,"");
            if(StringUtils.isEmpty(app)){
                cfgVos.add(vo);
            }else{
                if(k.equals(app)){ cfgVos.add(vo); }
            }
        }
        return PageUtil.pagination(cfgVos,page.getPageSize(),page.getPageNum()-1);
    }

    @Override
    public AppCfgVo selectAppCfgVo(String app) {
        KeyValue kv = configCenter.getKv(ConfigConstant.appCfgPath + app);
        if(kv == null || kv.getValue() == null){
            AppCfgVo ap = new AppCfgVo(app);
            configCenter.put(ConfigConstant.appCfgPath + app, JSON.toJSONString(ap));
            return ap;
        }
        String v = kv.getValue().toStringUtf8();
        AppCfgVo cfg = JSON.parseObject(v, AppCfgVo.class);
        cfg.setVersion(kv.getModRevision());
        return cfg;
    }

    /**
     * todo 多节点问题 待完善
     */
    @Override
    public void saveAppCfgVo(AppCfgVo cfg) {
        configCenter.put(ConfigConstant.appCfgPath + cfg.getApp(), JSON.toJSONString(cfg));
    }
}
