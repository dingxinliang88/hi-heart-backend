package com.juzi.heart.manager;

import com.juzi.heart.common.StatusCode;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.utils.ThrowUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

import static com.juzi.heart.constant.UserConstants.USER_LOGIN_STATUS_KEY;

/**
 * @author codejuzi
 */
@Service
public class UserManager {
    /**
     * 获取当前登录用户
     *
     * @param request http request
     * @return UserVO，登录态中的用户信息
     */
    public UserVO getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserVO loginUserVO = (UserVO) session.getAttribute(USER_LOGIN_STATUS_KEY);
        ThrowUtils.throwIf(Objects.isNull(loginUserVO), StatusCode.NOT_LOGIN_ERROR, "当前状态未登录");
        return loginUserVO;
    }


    /**
     * 获取当前登录用户，允许为空
     *
     * @param request http request
     * @return UserVO，登录态中的用户信息
     */
    public UserVO getLoginUserPermitNull(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (UserVO) session.getAttribute(USER_LOGIN_STATUS_KEY);
    }
}
