package com.juzi.heart.constant;

/**
 * @author codejuzi
 */
public interface UserRedisConstants {

    /**
     * 主页用户业务功能缓存key前缀
     */
    String CACHE_INDEX_PAGE_USER_KEY_PREFIX = "heart:u:recommend";

    /**
     * 主页用户业务分布式锁键
     */
    String INDEX_CACHE_LOCK = "heart:u:rec:lock";

    /**
     * 主页用户过期时间 30000ms
     */
    long INDEX_CACHE_TTL = 30000;

    /**
     * 写主页用户缓存时的锁的等待时间
     */
    long INDEX_LOCK_WAIT_TIME = 0;

    /**
     * 写主页用户缓存时的锁的持有时间，使用看门狗机制来处理
     */
    long INDEX_LOCK_LEASE_TIME = -1;
}
