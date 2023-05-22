package com.juzi.heart.constant;

/**
 * @author codejuzi
 */
public interface TagRedisConstants {

    /**
     * 标签缓存key
     */
    String TAG_CACHE_KEY = "heart:tag:list";

    /**
     * 标签缓存分布式锁键
     */
    String TAG_CACHE_LOCK = "heart:t:l:lock";


    /**
     * 标签缓存过期时间——60min
     */
    long TAG_CACHE_TTL = 60;

    /**
     * 写标签缓存时的锁的等待时间
     */
    long TAG_LOCK_WAIT_TIME = 0;

    /**
     * 写标签缓存时的锁的持有时间，使用看门狗机制来处理
     */
    long TAG_LOCK_LEASE_TIME = -1;

    /**
     * 父标签id缓存key
     */
    String P_TAG_ID_KEY = "heart:tag:parent";

    /**
     * 父标签id缓存过期时间 —— 24小时
     */
    long P_TAG_ID_CACHE_TTL = 24;

    /**
     * 父标签id缓存分布式锁键
     */
    String P_TAG_CACHE_LOCK = "heart:t:p:lock";
}
