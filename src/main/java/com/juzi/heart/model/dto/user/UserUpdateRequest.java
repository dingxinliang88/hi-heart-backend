package com.juzi.heart.model.dto.user;

import lombok.Data;

import java.util.List;

/**
 * @author codejuzi
 */
@Data
public class UserUpdateRequest {
    /**
     * 主键、自增、非空
     */
    private Long id;

    /**
     * 用户昵称，代码层面设置默认值，可不填
     */
    private String userName;


    /**
     * 用户头像图片地址，代码层面给默认值
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 性别：1-男，0-女
     */
    private Integer gender;

    /**
     * 手机号，允许为空
     */
    private String phone;

    /**
     * 邮箱。允许为空
     */
    private String email;

    /**
     * 标签列表
     */
    private List<String> tagList;
}
