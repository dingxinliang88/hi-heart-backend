package com.juzi.heart.constant;

/**
 * @author codejuzi
 */
public interface UserRedisConstants {

    /**
     * 推荐用户业务功能缓存key前缀
     */
    String RECOMMEND_USER_KEY_PREFIX = "heart:u:recommend";


    long REC_CACHE_TTL = 30000;
}
