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

|     字段     |   数据类型   |       说明        |
|:----------:|:--------:|:---------------:|
|     id     |  bigint  |    主键，自增，非空     |
|   userId   |  bigint  |     用户id，非空     |
|   teamId   |  bigint  |     队伍id，非空     |
|  joinTime  | datetime | 加入时间，非空，默认为当前时间 |
| createTime | datetime |  创建时间，默认为当前时间   |

```sql
CREATE TABLE `user_team`
(
    `id`         bigint   NOT NULL AUTO_INCREMENT COMMENT '主键，自增，非空',
    `userId`     bigint   NOT NULL COMMENT '用户id，非空',
    `teamId`     bigint   NOT NULL COMMENT '队伍id，非空',
    `joinTime`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间，非空，默认为当前时间',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认为当前时间',
    PRIMARY KEY (`id`),
    KEY `idx_userId` (`userId`) COMMENT '为userId字段添加索引',
    KEY `idx_teamId` (`teamId`) COMMENT '为teamId字段添加索引'
) COMMENT ='用户队伍表';

```

## 核心功能

## 匹配相似用户 => 余弦相似度算法

List<String> tagList集合：

- 根据每一个tagName去查找对应的 tagId、parentId
- parentTagWeights => map <parentId, weight>
- tags => <parentTagId, List\<childTagId\>>

算法详解：

```java
public class TagSimilarityCalculatorUtils {

    /**
     * 计算两个子标签列表的相似度
     *
     * @param tags1            第一个子标签列表
     * @param tags2            第二个子标签列表
     * @param parentTagWeights 父标签的权重
     * @return 相似度值，范围在-1到1之间
     */
    public static double calculateSimilarity(Map<Long, List<Long>> tags1,
                                             Map<Long, List<Long>> tags2,
                                             Map<Long, Double> parentTagWeights) {
        // 将子标签列表表示为向量
        Map<Long, Map<Long, Double>> vector1 = toVector(tags1, parentTagWeights);
        Map<Long, Map<Long, Double>> vector2 = toVector(tags2, parentTagWeights);

        // 计算余弦相似度
        return cosineSimilarity(vector1, vector2);
    }

    /**
     * 将子标签列表表示为向量
     *
     * @param tags             子标签列表
     * @param parentTagWeights 父标签的权重
     * @return 向量表示
     */
    private static Map<Long, Map<Long, Double>> toVector(Map<Long, List<Long>> tags, Map<Long, Double> parentTagWeights) {
        Map<Long, Map<Long, Double>> vector = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : tags.entrySet()) {
            Long parentId = entry.getKey();
            List<Long> tagIds = entry.getValue();
            for (Long tagId : tagIds) {
                if (parentTagWeights.containsKey(parentId)) {
                    double weight = parentTagWeights.get(parentId);
                    if (!vector.containsKey(parentId)) {
                        vector.put(parentId, new HashMap<>());
                    }
                    vector.get(parentId).put(tagId, weight);
                }
            }
        }
        return vector;
    }

    /**
     * 计算余弦相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度值，范围在-1到1之间
     */
    private static double cosineSimilarity(Map<Long, Map<Long, Double>> vector1, Map<Long, Map<Long, Double>> vector2) {
        // 计算分子部分
        double numerator = 0.0;
        for (Long parentId : vector1.keySet()) {
            if (vector2.containsKey(parentId)) {
                Map<Long, Double> subVector1 = vector1.get(parentId);
                Map<Long, Double> subVector2 = vector2.get(parentId);
                for (Long key : subVector1.keySet()) {
                    if (subVector2.containsKey(key)) {
                        numerator += subVector1.get(key) * subVector2.get(key);
                    }
                }
            }
        }

        // 计算分母部分
        double denominator = euclideanNorm(vector1) * euclideanNorm(vector2);

        // 计算余弦相似度
        if (denominator == 0.0) {
            return 0.0;
        } else {
            return numerator / denominator;
        }
    }

    /**
     * 计算向量的模长（欧几里得范数）
     *
     * @param vector 向量
     * @return 向量的模长
     */
    private static double euclideanNorm(Map<Long, Map<Long, Double>> vector) {
        double normSquared = 0.0;
        for (Map<Long, Double> subVector : vector.values()) {
            for (Double value : subVector.values()) {
                normSquared += value * value;
            }
        }
        return Math.sqrt(normSquared);
    }

}
```

具体推荐用户的逻辑：

- 按照当前登录的标签名称来得到map
- 跟每一个用户去做一个匹配
- 按照相似度从高到低来排序，返回

```java
@Override
public Page<UserVO> recommendUsers(HttpServletRequest request){
    UserVO loginUser=userManager.getLoginUser(request);
    String tags=loginUser.getTags();
    ThrowUtils.throwIf(StringUtils.isBlank(tags),StatusCode.OPERATION_ERROR,"您还没有选择您的标签！");

    LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.ne(User::getId,loginUser.getId())
    .isNotNull(User::getTags);
    List<User> userList=this.list(queryWrapper);

    @SuppressWarnings("UnstableApiUsage")
    Type type=new TypeToken<List<String>>(){}.getType();
    List<String> loginUserTagList=GSON.fromJson(tags,type);
    Map<Long, List<Long>>loginUserPIdChildTagIdListMap=this.getPIdChildTagIdListMap(loginUserTagList);
    List<Pair<User, Double>>similarityList=new ArrayList<>();
    for(User user:userList){
    List<String> userTagList=GSON.fromJson(user.getTags(),type);
    Map<Long, List<Long>>userPIdChildTagIdListMap=this.getPIdChildTagIdListMap(userTagList);
    // 计算相似度
    double similarity=TagSimilarityCalculatorUtils.calculateSimilarity(loginUserPIdChildTagIdListMap,
    userPIdChildTagIdListMap,PARENT_TAG_WEIGHTS);
    similarityList.add(new Pair<>(user,similarity));
    }
    // 按照相似度，从大到小排序，取前10个
    List<Pair<User, Double>>topUserPairList=similarityList.stream()
    .sorted(Comparator.comparing(Pair::getValue,Comparator.reverseOrder()))
    .limit(DEFAULT_PAGE_SIZE)
    .collect(Collectors.toList());

    List<UserVO> userVOList=topUserPairList.stream()
    .map(pair->getUserVO(pair.getKey()))
    .collect(Collectors.toList());

    Page<UserVO> userVOPage=new Page<>(DEFAULT_PAGE_NUM,DEFAULT_PAGE_SIZE,userVOList.size());
    userVOPage.setRecords(userVOList);

    return userVOPage;
}
```