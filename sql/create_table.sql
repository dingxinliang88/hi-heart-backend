create database hi_heart;
use hi_heart;

CREATE TABLE `user`
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT  NOT NULL COMMENT '主键、自增、非空',
    userName     VARCHAR(255)                       NULL COMMENT '用户昵称，代码层面设置默认值，可不填',
    userAccount  VARCHAR(8)                         NOT NULL COMMENT '登录账号，非空，最大为8位',
    userPassword VARCHAR(255)                       NOT NULL COMMENT '密码，非空，以加密的方式存入数据库，用户填写的密码不得少于8位',
    userAvatar   VARCHAR(255)                       NULL COMMENT '用户头像图片地址，代码层面给默认值',
    userProfile  VARCHAR(1024)                      NULL COMMENT '用户简介',
    gender       TINYINT                            NULL COMMENT '性别：1 - 男，0 - 女',
    phone        VARCHAR(11)                        NULL COMMENT '手机号，允许为空',
    email        VARCHAR(255)                       NULL COMMENT '邮箱。允许为空',
    userRole     TINYINT  DEFAULT 0                 NULL COMMENT '用户角色，0-普通用户，1-管理员，2-被封号的用户',
    tags         VARCHAR(512)                       NULL COMMENT '标签列表',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间，默认为当前时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，变化时修改为当前时间',
    isDelete     TINYINT  DEFAULT 0                 NULL COMMENT '是否删除，0 - 未删除，1 - 删除',
    UNIQUE INDEX user_acc_index (userAccount)
) COMMENT '用户信息表';


CREATE TABLE tag
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    tagName     VARCHAR(255) NOT NULL UNIQUE COMMENT '标签名称（非空，唯一，唯一索引）',
    userId      BIGINT       NOT NULL COMMENT '上传标签的用户（非空，普通索引）',
    hasChildren TINYINT      NOT NULL DEFAULT 0 COMMENT '标识是否为父标签（0 - 不是，1 - 是），默认值为0',
    parentId    BIGINT       NOT NULL COMMENT '父标签id（分类用，自身是父标签值为0，非空）',
    createTime  DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认为当前时间',
    updateTime  DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间，默认为当前时间',
    isDelete    TINYINT               DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-删除），默认为0',
    PRIMARY KEY (id),
    INDEX idx_userId (userId)
) COMMENT '标签表';


INSERT INTO `user` (`id`, `userName`, `userAccount`, `userPassword`, `userAvatar`, `gender`, `phone`, `email`,
                    `userRole`, `tags`, `createTime`, `updateTime`, `isDelete`)
VALUES (1, 'Test1', 'test', '12345678', 'https://photo.16pic.com/00/53/26/16pic_5326745_b.jpg', 0, '12345678',
        '12345678@test.com', 2, '[\"Java\", \"爱闹\", \"大二\"]', '2023-05-15 20:31:15', '2023-05-17 14:53:25', 0);
INSERT INTO `user` (`id`, `userName`, `userAccount`, `userPassword`, `userAvatar`, `gender`, `phone`, `email`,
                    `userRole`, `tags`, `createTime`, `updateTime`, `isDelete`)
VALUES (2, 'User_RPgZHhwwHD', 'Test1', '8b8a9bac6ac20279f6ef4a0b78cc3637',
        'https://photo.16pic.com/00/53/26/16pic_5326745_b.jpg', 1, '12436456', '234524523@123.com', 0,
        '[\"Java\", \"爱闹\", \"大二\"]', '2023-05-15 22:47:34', '2023-05-17 14:52:35', 0);
INSERT INTO `user` (`id`, `userName`, `userAccount`, `userPassword`, `userAvatar`, `gender`, `phone`, `email`,
                    `userRole`, `tags`, `createTime`, `updateTime`, `isDelete`)
VALUES (3, 'User_nhmeIqrEDg', 'codejuzi', '8b8a9bac6ac20279f6ef4a0b78cc3637',
        'https://photo.16pic.com/00/53/26/16pic_5326745_b.jpg', 1, '345352', '234524523@123.com', 1,
        '[\"Java\", \"静坐\", \"大二\"]', '2023-05-15 22:49:27', '2023-05-17 14:52:35', 0);
INSERT INTO `user` (`id`, `userName`, `userAccount`, `userPassword`, `userAvatar`, `gender`, `phone`, `email`,
                    `userRole`, `tags`, `createTime`, `updateTime`, `isDelete`)
VALUES (4, 'User_aCPQBmhWqo', 'dogjuzi', '8b8a9bac6ac20279f6ef4a0b78cc3637',
        'https://photo.16pic.com/00/53/26/16pic_5326745_b.jpg', 0, '2342341', '234524523@123.com', 0,
        '[\"C++\", \"爱闹\", \"大二\"]', '2023-05-17 14:01:09', '2023-05-17 14:52:35', 0);


CREATE TABLE `team`
(
    `id`           bigint        NOT NULL AUTO_INCREMENT COMMENT '主键、自增、非空',
    `teamName`     varchar(255)  NOT NULL COMMENT '队伍名称、非空',
    `description`  varchar(1024) NULL COMMENT '队伍描述，可以为空',
    `maxNum`       int           NOT NULL DEFAULT 5 COMMENT '队伍最大人数，非空，默认为5',
    `createUserId` bigint        NOT NULL COMMENT '创建队伍人id，非空，普通索引',
    `leaderId`     bigint        NOT NULL COMMENT '队长id，非空，普通索引',
    `status`       tinyint       NOT NULL DEFAULT 0 COMMENT '队伍状态，0 - 公开、1 - 私有，2 - 加密，默认为0',
    `teamPassword` varchar(128)  NULL COMMENT '队伍密码，只有在队伍状态为加密状态下才有',
    `teamAvatar`   varchar(255)  NULL COMMENT '队伍封面，可以为空，代码层面给默认值',
    `createTime`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认为当前时间',
    `updateTime`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间，默认为当前时间',
    `isDelete`     tinyint       NOT NULL DEFAULT 0 COMMENT '逻辑删除标志，0 - 未删除、1 - 删除',
    PRIMARY KEY (`id`),
    KEY `idx_create_user_id` (`createUserId`),
    KEY `idx_leader_id` (`leaderId`)
) COMMENT ='队伍表';
