package com.juzi.heart.constant;

/**
 * @author codejuzi
 */
public interface TeamConstants {

    /**
     * 队伍名称最大长度
     */
    int TEAM_NAME_MAX_LEN = 20;

    /**
     * 队伍描述最大长度
     */
    int TEAM_DESC_MAX_LEN = 512;

    /**
     * 队伍最大人数（最小边界，不包含）
     */
    int TEAM_MAX_NUM_BEGIN = 1;

    /**
     * 队伍最大人数（最大边界，不包含）
     */
    int TEAM_MAX_NUM_END = 21;

    /**
     * 队伍密码最大长度
     */
    int TEAM_PWD_MAX_LEN = 32;

    /**
     * 用户最多能创建的队伍数量
     */
    int USER_CREATE_TEAM_MAX_NUM = 5;

    /**
     * 用户最多能加入的队伍数量
     */
    int USER_JOIN_TEAM_MAX_NUM = 20;

    /**
     * 默认队伍简介（简介不填的前提下）
     */
    String DEFAULT_TEAM_DESC = "创建人很懒，还没有写简介--，";

    /**
     * 默认队伍封面
     */
    String DEFAULT_TEAM_AVATAR = "https://regengbaike.com/uploads/20230222/1bff61de34bdc7bf40c6278b2848fbcf.jpg";

    /**
     * 加密队伍状态
     */
    Integer CONST_ENCRYPTED = 2;


    /**
     * 私有队伍状态
     */
    Integer CONST_PRIVATE = 1;
}
