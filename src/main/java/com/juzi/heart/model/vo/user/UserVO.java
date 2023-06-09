package com.juzi.heart.model.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 脱敏用户对象，返回给前端
 *
 * @author codejuzi
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -1875100032388238844L;
    /**
     * 主键、自增、非空
     */
    private Long id;

    /**
     * 用户昵称，代码层面设置默认值，可不填
     */
    private String userName;

    /**
     * 登录账号，非空，最大为8位
     */
    private String userAccount;

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
     * 用户角色，0-普通用户，1-管理员，2-被封号的用户
     */
    private Integer userRole;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 创建时间
     */
    private Date createTime;
}
