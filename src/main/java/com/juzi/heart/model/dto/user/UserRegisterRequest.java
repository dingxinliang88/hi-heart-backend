package com.juzi.heart.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 8002377846697756698L;

    /**
     * 登录账号，非空，最大为8位
     */
    private String userAccount;

    /**
     * 密码，非空，以加密的方式存入数据库，用户填写的密码不得少于8位
     */
    private String userPassword;

    /**
     * 校验密码 （ == 密码）
     */
    private String checkedPassword;
}
