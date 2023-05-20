package com.juzi.heart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.mapper.UserMapper;
import com.juzi.heart.model.dto.user.UserLoginRequest;
import com.juzi.heart.model.dto.user.UserRegisterRequest;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.UserService;
import com.juzi.heart.utils.ThrowUtils;
import com.juzi.heart.utils.ValidCheckUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.juzi.heart.constant.UserConstants.*;

/**
 * @author codejuzi
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-05-15 20:18:05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(userRegisterRequest), StatusCode.PARAMS_ERROR, "注册参数不能为空！");
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkedPassword = userRegisterRequest.getCheckedPassword();
        ValidCheckUtils.checkRegisterParams(userAccount, userPassword, checkedPassword);
        synchronized (userAccount.intern()) {
            // 6. 账号不能重复
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserAccount, userAccount);
            User user = this.getOne(userLambdaQueryWrapper);
            ThrowUtils.throwIf(!Objects.isNull(user), StatusCode.PARAMS_ERROR, "该账号已经存在！");

            // 加密密码
            String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

            // 封装数据
            User newUser = new User();
            newUser.setUserName(DEFAULT_UNAME_PREFIX + RandomStringUtils.randomAlphabetic(DEFAULT_UNAME_SUFFIX_LEN));
            newUser.setUserAccount(userAccount);
            newUser.setUserPassword(encryptedPassword);
            newUser.setUserAvatar(DEFAULT_AVATAR_URL);
            // 插入数据
            boolean result = this.save(newUser);
            ThrowUtils.throwIf(!result, StatusCode.SYSTEM_ERROR, "插入数据失败");
            // 返回新用户id
            return newUser.getId();
        }
    }

    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(userLoginRequest), StatusCode.PARAMS_ERROR, "登录信息不能为空");
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        ValidCheckUtils.checkLoginParams(userAccount, userPassword);

        UserVO loginUser = this.getLoginUserPermitNull(request);
        if (loginUser != null) {
            return loginUser;
        }
        synchronized (userAccount.intern()) {
            loginUser = this.getLoginUserPermitNull(request);
            if (loginUser != null) {
                return loginUser;
            }
            // 校验密码是否正确
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            User userFromDb = this.getOne(queryWrapper);
            ThrowUtils.throwIf(Objects.isNull(userFromDb), StatusCode.NOT_FOUND_ERROR, "账号尚未注册！");
            String pwdFromDb = userFromDb.getUserPassword();
            String encryptedPwd = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
            ThrowUtils.throwIf(!pwdFromDb.equals(encryptedPwd), StatusCode.PARAMS_ERROR, "密码不正确！");

            // 用户信息脱敏
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userFromDb, userVO);

            // 保存用户登录态
            HttpSession session = request.getSession();
            session.setAttribute(USER_LOGIN_STATUS_KEY, userVO);

            // 返回脱敏后的用户信息
            return userVO;
        }
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserVO loginUserVO = (UserVO) session.getAttribute(USER_LOGIN_STATUS_KEY);
        ThrowUtils.throwIf(Objects.isNull(loginUserVO), StatusCode.NOT_LOGIN_ERROR, "当前状态未登录");
        return loginUserVO;
    }

    @Override
    public UserVO getLoginUserPermitNull(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (UserVO) session.getAttribute(USER_LOGIN_STATUS_KEY);
    }

    @Override
    public List<User> queryUser(String searchText) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like(User::getUserName, searchText)
                    .or()
                    .like(User::getUserAccount, searchText);
        }
        return this.list(queryWrapper);
    }

    @Override
    public Boolean deleteUserById(Long userId) {
        ThrowUtils.throwIf(userId <= 0, StatusCode.PARAMS_ERROR, "用户id不合法");
        return this.removeById(userId);
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        UserVO loginUser = this.getLoginUser(request);
        ThrowUtils.throwIf(Objects.isNull(loginUser), StatusCode.NOT_LOGIN_ERROR, "当前尚未登录");
        HttpSession session = request.getSession();
        session.removeAttribute(USER_LOGIN_STATUS_KEY);
        return true;
    }

    @Override
    public List<UserVO> listUserVO() {
        List<User> userList = this.list();
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public List<UserVO> queryUserByTagList(List<String> tagList) {
        return queryUserByTagListUseSql(tagList);
    }

    @Override
    public List<UserVO> queryUserByTagListUseSql(List<String> tagList) {
        ThrowUtils.throwIf(CollectionUtils.isEmpty(tagList), StatusCode.PARAMS_ERROR, "查询标签为空");
        // like 连接
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        for (String tag : tagList) {
            queryWrapper.like(User::getTags, tag);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public List<UserVO> queryUserByTagListUseMemory(List<String> tagList) {
        ThrowUtils.throwIf(CollectionUtils.isEmpty(tagList), StatusCode.PARAMS_ERROR, "查询标签为空");
        // 先查出所有的用户
        List<User> userList = this.list();
        // 在内存中过滤出匹配的用户
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            @SuppressWarnings("UnstableApiUsage")
            Type setType = new TypeToken<Set<String>>() {
            }.getType();
            Set<String> tmpTagNameSet = gson.fromJson(tagStr, setType);
            // 判空
            tmpTagNameSet = Optional.ofNullable(tmpTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagList) {
                if (!tmpTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public UserVO getUserVO(User originUser) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(originUser, userVO);
        return userVO;
    }
}




