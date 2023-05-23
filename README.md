# Hi-Heart-Backend

# 技术栈

- Java11
- Spring全家桶
- MyBatis
- MyBatisPlus
- Knife4j
- Redis

# 数据库表设计

## 标签表（分类表）

|     字段     |   数据类型   |              说明              |
|:----------:|:--------:|:----------------------------:|
|     id     |  bigint  |           主键，自增，非空           |
|  tagName   | varchar  |       标签名称（非空，唯一，唯一索引）       |
|   userId   |  bigint  |       上传标签的用户（非空，普通索引）       |
|  isParent  | tinyint  | 标识是否为父标签（0 - 不是，1 - 是），默认值为0 |
|  parentId  |  bigint  |   父标签id（分类用，自身是父标签值为-1，非空）   |
| createTime | datetime |         创建时间，默认为当前时间         |
| updateTime | datetime |         修改时间，默认为当前时间         |
|  isDelete  | tinyint  |    逻辑删除（0-未删除，1-删除），默认为0     |

```sql
CREATE TABLE tag
(
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    tagName    VARCHAR(255) NOT NULL UNIQUE COMMENT '标签名称（非空，唯一，唯一索引）',
    userId     BIGINT       NOT NULL COMMENT '上传标签的用户（非空，普通索引）',
    isParent   TINYINT      NOT NULL DEFAULT 0 COMMENT '标识是否为父标签（0 - 不是，1 - 是），默认值为0',
    parentId   BIGINT       NOT NULL DEFAULT -1 COMMENT '父标签id（分类用，自身是父标签值为-1，非空）',
    createTime DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认为当前时间',
    updateTime DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间，默认为当前时间',
    isDelete   TINYINT               DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-删除），默认为0',
    PRIMARY KEY (id),
    INDEX idx_userId (userId)
) COMMENT '标签表';
```

## 用户表设计

| 字段           | 数据类型     | 说明                              |
|--------------|----------|---------------------------------|
| id           | big int  | 主键、自增、非空                        |
| userName     | varchar  | 用户昵称，代码层面设置默认值，可不填              |
| userAccount  | varchar  | 登录账号，非空，最大为8位                   |
| userPassword | varchar  | 密码，非空，以加密的方式存入数据库，用户填写的密码不得少于8位 |
| userAvatar   | varchar  | 用户头像图片地址，代码层面给默认值               |
| gender       | tiny int | 性别：1 - 男，0 - 女                  |
| phone        | varchar  | 手机号，允许为空                        |
| email        | varchar  | 邮箱。允许为空                         |
| userRole     | tiny int | 用户角色，0-普通用户，1-管理员，2-被封号的用户      |
| tags         | varchar  | 用户标签列表（json字符串格式存储）             |
| createTime   | datetime | 创建时间，默认为当前时间                    |
| updateTime   | datetime | 更新时间，变化时修改为当前时间                 |
| isDelete     | tiny int | 是否删除， 0 - 未删除，1 - 删除            |

```sql
CREATE TABLE `user`
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT  NOT NULL COMMENT '主键、自增、非空',
    userName     VARCHAR(255)                       NULL COMMENT '用户昵称，代码层面设置默认值，可不填',
    userAccount  VARCHAR(8)                         NOT NULL COMMENT '登录账号，非空，最大为8位',
    userPassword VARCHAR(255)                       NOT NULL COMMENT '密码，非空，以加密的方式存入数据库，用户填写的密码不得少于8位',
    userAvatar   VARCHAR(255)                       NULL COMMENT '用户头像图片地址，代码层面给默认值',
    gender       TINYINT                            NULL COMMENT '性别：1 - 男，0 - 女',
    phone        VARCHAR(11)                        NULL COMMENT '手机号，允许为空',
    email        VARCHAR(255)                       NULL COMMENT '邮箱。允许为空',
    userRole     TINYINT  DEFAULT 0                 NULL COMMENT '用户角色，0-普通用户，1-管理员，2-被封号的用户',
    tags         VARCHAR(512)                       NULL COMMENT '标签列表',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间，默认为当前时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，变化时修改为当前时间',
    isDelete     TINYINT  DEFAULT 0                 NULL COMMENT '是否删除，0 - 未删除，1 - 删除'
) COMMENT '用户信息表';

```

## 队伍表设计

|      字段      |   数据类型   |               说明                |
|:------------:|:--------:|:-------------------------------:|
|      id      |  bigint  |            主键、自增、非空             |
|   teamName   | varchar  |             队伍名称、非空             |
| description  | varchar  |            队伍描述，可以为空            |
|    maxNum    |   int    |         队伍最大人数，非空，默认为5          |
| createUserId |  bigint  |         创建队伍人id，非空，普通索引         |
|   leaderId   |  bigint  |          队长id，非空，普通索引           |
|    status    | tinyint  | 队伍状态，0 - 公开、1 - 私有， 2 - 加密，默认为0 |
| teamPassword | varchar  |      队伍密码，只有在队伍状态为加密状态下才有       |
|  teamAvatar  | varchar  |       队伍封面，可以为空，代码层面给默认值        |
|  createTime  | datetime |          创建时间，默认为当前时间           |
|  updateTime  | datetime |          修改时间，默认为当前时间           |
|   isDelete   | tinyint  |      逻辑删除标志，0 - 未删除、1 - 删除      |

```sql
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

```

## 用户队伍表设计

|     字段     |   数据类型   |          说明           |
|:----------:|:--------:|:---------------------:|
|     id     |  bigint  |       主键，自增，非空        |
|   userId   |  bigint  |        用户id，非空        |
|   teamId   |  bigint  |        队伍id，非空        |
|  joinTime  | datetime |    加入时间，非空，默认为当前时间    |
| createTime | datetime |     创建时间，默认为当前时间      |
| updateTime | datetime |     修改时间，默认为当前时间      |

```sql
CREATE TABLE `user_team`
(
    `id`         bigint   NOT NULL AUTO_INCREMENT COMMENT '主键，自增，非空',
    `userId`     bigint   NOT NULL COMMENT '用户id，非空',
    `teamId`     bigint   NOT NULL COMMENT '队伍id，非空',
    `joinTime`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间，非空，默认为当前时间',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认为当前时间',
    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间，默认为当前时间',
    PRIMARY KEY (`id`),
    KEY `idx_userId` (`userId`) COMMENT '为userId字段添加索引',
    KEY `idx_teamId` (`teamId`) COMMENT '为teamId字段添加索引'
) COMMENT ='用户队伍表';

```

