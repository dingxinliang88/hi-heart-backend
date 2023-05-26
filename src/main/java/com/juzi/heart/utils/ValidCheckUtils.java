package com.juzi.heart.utils;

import com.juzi.heart.common.StatusCode;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.juzi.heart.constant.TeamConstants.*;
import static com.juzi.heart.constant.TeamConstants.TEAM_PWD_MAX_LEN;
import static com.juzi.heart.constant.UserConstants.ACC_MAX_LEN;
import static com.juzi.heart.constant.UserConstants.PWD_MIN_LEN;

/**
 * 合法性校验工具类
 *
 * @author codejuzi
 */
public class ValidCheckUtils {

    public static void checkLoginParams(String userAccount, String userPassword) {
        // 1. 非空
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userAccount, userPassword),
                StatusCode.PARAMS_ERROR, "参数不能为空！");
        // 2. 账号长度 <= 8
        ThrowUtils.throwIf(userAccount.length() > ACC_MAX_LEN, StatusCode.PARAMS_ERROR, "账号不能超过8位！");
        // 3. 密码长度 >= 8
        ThrowUtils.throwIf(userPassword.length() < PWD_MIN_LEN, StatusCode.PARAMS_ERROR, "密码长度不能小于8位！");
        // 4. 账号不包含特殊字符
        String accountValidPattern = "^[a-zA-Z0-9]+$";
        Matcher matcher = Pattern.compile(accountValidPattern).matcher(userAccount);
        ThrowUtils.throwIf(!matcher.find(), StatusCode.PARAMS_ERROR, "账号不能包含特殊字符！");
    }

    public static void checkRegisterParams(String userAccount, String userPassword, String checkedPassword) {
        // 复用
        checkLoginParams(userAccount, userPassword);

        // 5.校验密码长度 >= 8
        ThrowUtils.throwIf(checkedPassword.length() < PWD_MIN_LEN, StatusCode.PARAMS_ERROR, "校验密码长度不能小于8位！");
        // 6.密码 == 校验密码
        ThrowUtils.throwIf(!userPassword.equals(checkedPassword), StatusCode.PARAMS_ERROR, "两次输入密码不一致！");

    }

    /**
     * 校验队伍参数是否合法（新增，修改）
     *
     * @param teamName     队伍名称
     * @param description  队伍描述
     * @param maxNum       队伍最大人数
     * @param status       队伍状态
     * @param teamPassword 队伍密码
     */
    public static void checkTeamInfoValid(String teamName, String description,
                                    Integer maxNum, Integer status, String teamPassword) {
        // 队伍标题不能为空 && 队伍标题长度 <= 20
        ThrowUtils.throwIf(StringUtils.isBlank(teamName), StatusCode.PARAMS_ERROR, "队伍名称不能为空！");
        ThrowUtils.throwIf(StringUtils.isNotBlank(teamName) && teamName.length() > TEAM_NAME_MAX_LEN,
                StatusCode.PARAMS_ERROR, "队伍名称长度不能超过20");
        // 队伍描述长度<= 512
        ThrowUtils.throwIf(StringUtils.isNotBlank(description) && description.length() > TEAM_DESC_MAX_LEN,
                StatusCode.PARAMS_ERROR, "队伍描述过长！");
        // 队伍最大人数 1 <  maxNum  <= 20
        ThrowUtils.throwIf(maxNum <= TEAM_MAX_NUM_BEGIN || maxNum >= TEAM_MAX_NUM_END,
                StatusCode.PARAMS_ERROR, "队伍最大人数在2到20之间！");
        // status不能为空，默认为公开（0），如果status为加密状态（2），则一定要有密码，且密码长度 <= 32
        ThrowUtils.throwIf(CONST_ENCRYPTED.equals(status) && StringUtils.isBlank(teamPassword),
                StatusCode.PARAMS_ERROR, "加密队伍密码不能为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(teamPassword) && teamPassword.length() > TEAM_PWD_MAX_LEN,
                StatusCode.PARAMS_ERROR, "密码长度不能大于32");
    }
}
