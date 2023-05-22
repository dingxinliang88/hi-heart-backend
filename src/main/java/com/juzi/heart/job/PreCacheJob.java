package com.juzi.heart.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
import static com.juzi.heart.constant.UserRedisConstants.RECOMMEND_USER_KEY_PREFIX;
import static com.juzi.heart.constant.UserRedisConstants.REC_CACHE_TTL;

/**
 * 缓存预热，提前缓存用户信息
 *
 * @author codejuzi
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 重点用户id
     */
    private static final List<Long> MAIN_USER_ID_LIST = Arrays.asList(1L, 2L);

    /**
     * 每天早上八点执行一次，目标用户是VIP用户
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void doCacheRecommendUsers() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        for (Long userId : MAIN_USER_ID_LIST) {
            String recommendUserKey = String.format("%s:%s", RECOMMEND_USER_KEY_PREFIX, userId);
            Page<User> userPage = userService.page(new Page<>(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE));
            List<UserVO> userVOList = userPage.getRecords().stream().map(userService::getUserVO).collect(Collectors.toList());
            Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
            userVOPage.setRecords(userVOList);
            // 写缓存
            valueOperations.set(recommendUserKey, userVOPage, REC_CACHE_TTL, TimeUnit.MILLISECONDS);
        }
    }
}
