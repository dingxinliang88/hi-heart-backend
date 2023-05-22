package com.juzi.heart.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 队伍状态枚举类
 *
 * @author codejuzi
 */
public enum TeamStatusEnums {
    PUBLIC("公开", 0),
    PRIVATE("私密", 1),
    ENCRYPTED("加密", 2),
    ;

    public static final List<Integer> TEAM_STATUS_LIST = Arrays.asList(0, 1, 2);
    private final String description;
    private final Integer status;


    TeamStatusEnums(String description, Integer status) {
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStatus() {
        return status;
    }

    /**
     * 根据队伍状态值获取枚举值
     *
     * @param status 队伍状态值
     * @return 枚举值
     */
    public TeamStatusEnums getEnumsByStatus(Integer status) {
        if (!TEAM_STATUS_LIST.contains(status)) {
            return null;
        }
        for (TeamStatusEnums statusEnums : TeamStatusEnums.values()) {
            if (statusEnums.getStatus().equals(status)) {
                return statusEnums;
            }
        }
        return null;
    }


}
