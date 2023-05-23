package com.juzi.heart.manager;

import com.juzi.heart.common.StatusCode;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static com.juzi.heart.constant.UserConstants.ADMIN;

/**
 * @author codejuzi
 */
@Service
public class AuthManager {

    @Resource
    private UserManager userManager;

    public void adminOrMe(Long checkedUserId, HttpServletRequest request) {
        UserVO loginUser = userManager.getLoginUser(request);
        boolean isAdmin = ADMIN.equals(loginUser.getUserRole());
        boolean isMe = !Objects.isNull(checkedUserId) && checkedUserId.equals(loginUser.getId());
        // 管理员 || 自己
        ThrowUtils.throwIf(!(isAdmin || isMe), StatusCode.NO_AUTH_ERROR, "无相应权限!");
    }

}
