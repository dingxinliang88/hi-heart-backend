package com.juzi.heart.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author codejuzi
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

    private String host;

    private String port;

    private Integer database;

    @Bean
    public RedissonClient redissonClient() {
        // 创建配置
        Config config = new Config();
        String redisAddr = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddr).setDatabase(database);
        return Redisson.create(config);
    }
}
