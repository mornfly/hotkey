package com.jd.platform.hotkey.dashboard.erp;

import com.jd.common.springmvc.interceptor.SpringSSOInterceptor;
import com.jd.common.web.LoginContext;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ErpUimInterceptor extends SpringSSOInterceptor {

    @Autowired
    private UserService userService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginContext loginContext = LoginContext.getLoginContext();
        if (null == loginContext) {
            super.preHandle(request, response, handler);
        }
        loginContext = LoginContext.getLoginContext();
        if (null == loginContext) {
            return true;
        }
        /*Cookie cookie = new Cookie("erp", loginContext.getNick());
        cookie.setMaxAge(3600*24*7);
        cookie.setPath("/");
        response.addCookie(cookie);*/
        String pin = loginContext.getPin();
        String nickName = loginContext.getNick();
        String orgName = loginContext.getOrgName();
        String orgId = loginContext.getOrgId();
        String mobile = loginContext.getMobile();
        String email = loginContext.getEmail();
        User user = new User();
        user.setAppName("test");
        user.setUserName(pin);
        user.setNickName(nickName);
        user.setPhone(mobile);
        user.setRole("APPUSER");
        Cookie cookie = userService.loginErpUser(user);
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie tempCookie : cookies){
                if("token".equals(tempCookie.getName()) && tempCookie.getValue() != null){
                    return true;
                }
            }
        }
        response.addCookie(cookie);
        return true;
    }
}
