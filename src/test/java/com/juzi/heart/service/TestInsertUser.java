package com.juzi.heart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.heart.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.juzi.heart.constant.UserConstants.*;

/**
 * 测试插入数据
 *
 * @author codejuzi
 */
@Slf4j
@SpringBootTest
public class TestInsertUser {

    @Resource
    private UserService userService;

    private static final String DEFAULT_USER_AVATAR = "https://img.58tg.com/up/allimg/tx29/08151048279757835.jpg";

    /**
     * 88888888
     */
    private static final String DEFAULT_USER_PWD = "d533163ec50751f5b3f733995e6e618a";

    private static final int DEFAULT_USER_ACC_LEN = 7;

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            40, 1000, 10, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入
     * 输出mp的sql日志 => 8.798s
     * 不输出日志      => 7.507s
     */
    @Test
    void testSaveBatch() {
        StopWatch stopWatch = new StopWatch();
        List<User> userList = new ArrayList<>();
        final int MAX_NUM = 100000;
        stopWatch.start();
        for (int i = 0; i < MAX_NUM; i++) {
            User user = new User();
            String defaultUserName = DEFAULT_UNAME_PREFIX + RandomStringUtils.randomAlphabetic(DEFAULT_UNAME_SUFFIX_LEN);
            user.setUserName(defaultUserName);
            user.setUserAccount(RandomStringUtils.randomAlphabetic(DEFAULT_USER_ACC_LEN));
            user.setUserPassword(DEFAULT_USER_PWD);
            user.setUserAvatar(DEFAULT_USER_AVATAR);
            user.setUserProfile("测试用户" + i);
            user.setGender(RandomUtils.nextInt(0, 2));
            user.setPhone("88888888");
            user.setEmail("888888@juzi.com");
            user.setUserRole(USER);
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList, IService.DEFAULT_BATCH_SIZE);
        stopWatch.stop();
        log.info("=======> insert finished, cost: {}ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入
     * 输出mp的sql日志 => 4.497s
     * 不输出日志      => 3.459s
     */
    @Test
    void testSaveBatchConcurrent() {
        StopWatch stopWatch = new StopWatch();
        final int BATCH_SIZE = 5000;
        final int TIMES = 20;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        stopWatch.start();
        for (int i = 0; i < TIMES; i++) {
            List<User> userList = new ArrayList<>();
            do {
                j++;
                User user = new User();
                String defaultUserName = DEFAULT_UNAME_PREFIX + RandomStringUtils.randomAlphabetic(DEFAULT_UNAME_SUFFIX_LEN);
                user.setUserName(defaultUserName);
                user.setUserAccount(RandomStringUtils.randomAlphabetic(DEFAULT_USER_ACC_LEN));
                user.setUserPassword(DEFAULT_USER_PWD);
                user.setUserAvatar(DEFAULT_USER_AVATAR);
                user.setUserProfile("测试用户" + i);
                user.setGender(RandomUtils.nextInt(0, 2));
                user.setPhone("88888888");
                user.setEmail("888888@juzi.com");
                user.setUserRole(USER);
                user.setTags("[]");
                userList.add(user);
            } while (j % BATCH_SIZE != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                log.info("===========>treadName: {}", Thread.currentThread().getName());
                userService.saveBatch(userList, BATCH_SIZE);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        log.info("=======> insert finished, cost: {}ms", stopWatch.getTotalTimeMillis());
    }
}
