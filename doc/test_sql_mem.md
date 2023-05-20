# 【数据量不大】

第一次
com.juzi.heart.service.UserServiceTest   : ===========> Use Memory, 15ms
com.juzi.heart.service.UserServiceTest   : ===========> Use Sql, 8ms

第二次
com.juzi.heart.service.UserServiceTest   : ===========> Use Memory, 14ms
com.juzi.heart.service.UserServiceTest   : ===========> Use Sql, 8ms

--- 交换位置 ---

第一次
com.juzi.heart.service.UserServiceTest   : ===========> Use Sql, 14ms
com.juzi.heart.service.UserServiceTest   : ===========> Use Memory, 12ms

第二次
com.juzi.heart.service.UserServiceTest   : ===========> Use Sql, 14ms
com.juzi.heart.service.UserServiceTest   : ===========> Use Memory, 12ms

--- 交换位置 ---
第一次
com.juzi.heart.service.UserServiceTest   : ===========> Use Memory, 15ms
com.juzi.heart.service.UserServiceTest   : ===========> Use Sql, 11ms

第二次
com.juzi.heart.service.UserServiceTest   : ===========> Use Memory, 17ms
com.juzi.heart.service.UserServiceTest   : ===========> Use Sql, 10ms

==> 根据实际情况，选择SQL