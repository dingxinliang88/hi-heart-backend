package com.juzi.heart.constant;

/**
 * @author codejuzi
 */
public interface TeamRedisConstants {

    /**
     * 加入队伍锁前缀
     */
    String JOIN_TEAM_LOCK_PREFIX = "heart:join_team:";

    /**
     * 加入队伍缓存时的锁的等待时间
     */
    long JOIN_TEAM_LOCK_WAIT_TIME = 0;

    /**
     * 加入队伍的锁的持有时间，使用看门狗机制来处理
     */
    long JOIN_TEAM_LOCK_LEASE_TIME = -1;

    /**
     * 删除队伍锁前缀
     */
    String DELETE_TEAM_LOCK_PREFIX = "heart:delete_team:";

    /**
     * 删除队伍缓存时的锁的等待时间
     */
    long DELETE_TEAM_LOCK_WAIT_TIME = 0;

    /**
     * 删除队伍的锁的持有时间，使用看门狗机制来处理
     */
    long DELETE_TEAM_LOCK_LEASE_TIME = -1;

}
