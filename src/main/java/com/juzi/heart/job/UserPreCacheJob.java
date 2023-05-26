package com.juzi.heart.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.juzi.heart.constant.BusinessConstants.DEFAULT_PAGE_NUM;
import static com.juzi.heart.constant.BusinessConstants.DEFAULT_PAGE_SIZE;
import static com.juzi.heart.constant.UserRedisConstants.*;

/**
 * 缓存预热，提前主页用户信息
 *
 * @author codejuzi
 */
@Slf4j
@Component
public class UserPreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 重点用户id
     */
    private static final List<Long> MAIN_USER_ID_LIST = Arrays.asList(1L, 2L, 4L);

    /**
     * 每天早上八点执行一次，目标用户是VIP用户
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void doCacheIndexPageUsers() {
        RLock rLock = redissonClient.getLock(INDEX_CACHE_LOCK);
        try {
            // 只有一个线程可以获得锁
            if (rLock.tryLock(INDEX_LOCK_WAIT_TIME, INDEX_LOCK_LEASE_TIME, TimeUnit.MILLISECONDS)) {
                log.info("====> get index page user info lock, threadId: {}", Thread.currentThread().getId());
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                for (Long userId : MAIN_USER_ID_LIST) {
                    String recommendUserKey = String.format("%s:%s", CACHE_INDEX_PAGE_USER_KEY_PREFIX, userId);
                    Page<User> userPage = userService.page(new Page<>(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE));
                    List<UserVO> userVOList = userPage.getRecords().stream()
                            .map(userService::getUserVO).collect(Collectors.toList());
                    Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
                    userVOPage.setRecords(userVOList);
                    // 写缓存
                    valueOperations.set(recommendUserKey, userVOPage, INDEX_CACHE_TTL, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException e) {
            log.error("do cache recommend users error, ", e);
        } finally {
            // 释放锁，且只能释放自己的锁
            if (rLock.isHeldByCurrentThread()) {
                log.info("<==== un index page user info lock, threadId: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }

    }
}
