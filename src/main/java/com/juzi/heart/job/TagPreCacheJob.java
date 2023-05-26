package com.juzi.heart.job;

import com.juzi.heart.manager.TagManager;
import com.juzi.heart.model.vo.tag.TagVO;
import com.juzi.heart.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.juzi.heart.constant.TagRedisConstants.*;

/**
 * 标签的缓存任务
 *
 * @author codejuzi
 */
@Slf4j
@Component
public class TagPreCacheJob {

    @Resource
    private TagService tagService;

    @Resource
    private TagManager tagManager;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 每天早上八点执行，缓存标签信息
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void doCacheTagInfo() {
        RLock rLock = redissonClient.getLock(TAG_CACHE_LOCK);
        try {
            if (rLock.tryLock(TAG_LOCK_WAIT_TIME, TAG_LOCK_LEASE_TIME, TimeUnit.MILLISECONDS)) {
                log.info("====> get tag cache lock, threadId: {}", Thread.currentThread().getId());
                List<TagVO> tagVOList = tagManager.getTagVOList(tagService.list());
                // 写缓存
                tagManager.cacheTagVOList(tagVOList);
            }
        } catch (InterruptedException e) {
            log.error("cache tag info error, ", e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("<=== un tag cache lock, threadId: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }


    /**
     * 每天早上八点缓存父标签id
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void doCacheParentTagId() {
        RLock rLock = redissonClient.getLock(P_TAG_CACHE_LOCK);
        try {
            if (rLock.tryLock(TAG_LOCK_WAIT_TIME, TAG_LOCK_LEASE_TIME, TimeUnit.MILLISECONDS)) {
                log.info("====> get cache parent tag id lock, threadId: {}", Thread.currentThread().getId());
                List<Long> parentTagIdList = tagService.getParentTagIdList();
                tagManager.cacheParentTagId(parentTagIdList);
            }
        } catch (InterruptedException e) {
            log.error("cache parent tag id error, ", e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("<=== un parent tag id cache lock, threadId: {}", Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }


}
