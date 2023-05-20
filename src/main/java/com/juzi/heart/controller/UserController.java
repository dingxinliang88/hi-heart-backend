package com.juzi.heart.controller;

import com.juzi.heart.annotations.AuthCheck;
import com.juzi.heart.common.BaseResponse;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.model.dto.user.UserLoginRequest;
import com.juzi.heart.model.dto.user.UserRegisterRequest;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.UserService;
import com.juzi.heart.utils.ResultUtils;
import com.juzi.heart.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(Objects.isNull(userRegisterRequest), StatusCode.PARAMS_ERROR, "注册请求为空");
        Long userId = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(userId, "注册成功");
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(userLoginRequest), StatusCode.PARAMS_ERROR, "登录请求为空");
        UserVO userVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(userVO, "登录成功");
    }

    @AuthCheck(mustRole = "admin")
    @GetMapping("/query")
    public BaseResponse<List<User>> queryUser(String searchText) {
        List<User> userList = userService.queryUser(searchText);
        return ResultUtils.success(userList);
    }

    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUserVO() {
        List<UserVO> userVOList = userService.listUserVO();
        return ResultUtils.success(userVOList);
    }

    @AuthCheck(mustRole = "admin")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestParam("userId") Long userId) {
        ThrowUtils.throwIf(userId <= 0, StatusCode.PARAMS_ERROR, "用户id不合法");
        Boolean result = userService.deleteUserById(userId);
        return ResultUtils.success(result);
    }

    @PutMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        Boolean result = userService.userLogout(request);
        return ResultUtils.success(result, "登出成功");
    }

    @GetMapping("/current")
    public BaseResponse<UserVO> getLoginUserVO(HttpServletRequest request) {
        UserVO loginUserVO = userService.getLoginUser(request);
        return ResultUtils.success(loginUserVO);
    }
}
