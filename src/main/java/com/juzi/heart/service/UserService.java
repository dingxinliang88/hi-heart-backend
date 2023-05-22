package com.juzi.heart.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.heart.common.PageRequest;
import com.juzi.heart.model.dto.user.UserLoginRequest;
import com.juzi.heart.model.dto.user.UserRegisterRequest;
import com.juzi.heart.model.dto.user.UserUpdateRequest;
import com.juzi.heart.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.heart.model.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author codejuzi
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-05-15 20:18:05
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求参数封装体
     * @return 新注册用户id
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求信息封装体
     * @param request          http request
     * @return UserVO, 脱敏后的用户数据
     */
    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 根据搜索关键词来模糊匹配userName、 userAccount查找用户。
     * 不传默认搜索所有用户，仅管理员可用
     *
     * @param searchText 搜索关键词
     * @return User全数据列表
     */
    List<User> queryUser(String searchText);

    /**
     * 根据userid 删除用户，仅管理员
     *
     * @param userId user id
     * @return true - 删除成功
     */
    Boolean deleteUserById(Long userId);

    /**
     * 用户登出
     *
     * @param request http request
     * @return true - 登出成功
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 查询所有的用户对象（脱敏后的）
     *
     * @return user vo list
     */
    List<UserVO> listUserVO();

    /**
     * 根据标签列表来查询用户信息（具体使用SQL过滤还是内存过滤，看实际情况）
     * 目前数据量不大的情况下，SQL有一点点优势
     *
     * @param tagList 标签列表
     * @return user vo list
     */
    List<UserVO> queryUserByTagList(List<String> tagList);

    /**
     * 根据标签列表来查询用户信息（使用SQL过滤）
     *
     * @param tagList 标签列表
     * @return user vo list
     */
    List<UserVO> queryUserByTagListUseSql(List<String> tagList);


    /**
     * 根据标签列表来查询用户信息（使用内存过滤）
     *
     * @param tagList 标签列表
     * @return user vo list
     */
    List<UserVO> queryUserByTagListUseMemory(List<String> tagList);

    /**
     * 修改用户信息
     *
     * @param userUpdateRequest 用户修改请求封装信息
     * @param request           http request
     * @return true - 修改成功
     */
    Boolean updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 得到user vo对象
     *
     * @param originUser 原始user对象
     * @return 脱敏后的user对象 user vo
     */
    UserVO getUserVO(User originUser);

    /**
     * 分页展示用户
     *
     * @param pageRequest 分页请求信息
     * @return user vo page
     */
    Page<UserVO> listUserVOByPage(PageRequest pageRequest);

    /**
     * 根据用户标签推荐相似用户（分页）
     *
     * @param pageRequest 分页请求信息
     * @param request     http request
     * @return user vo page
     */
    Page<UserVO> recommendUsers(PageRequest pageRequest, HttpServletRequest request);
}
