package com.juzi.heart.manager;

import com.juzi.heart.model.entity.Tag;
import com.juzi.heart.model.vo.tag.TagVO;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.juzi.heart.constant.TagConstants.DEFAULT_GROUP;
import static com.juzi.heart.constant.TagConstants.DEFAULT_PARENT_ID;
import static com.juzi.heart.constant.TagRedisConstants.*;

/**
 * @author codejuzi
 */
@Service
public class TagManager {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 将tag list分组，封装成tag vo list
     *
     * @param tagList tag list
     * @return tag vo list
     */
    public List<TagVO> getTagVOList(List<Tag> tagList) {
        // 分组，按照父标签名称 || 默认分组（存放所有的子标签）
        Map<String, List<Tag>> parentTagMap = tagList.stream()
                .collect(Collectors.groupingBy(tag -> {
                    if (DEFAULT_PARENT_ID.equals(tag.getParentId())) {
                        return tag.getTagName();
                    } else {
                        // 根据父标签id获取父标签名称
                        return tagList.stream()
                                .filter(t -> t.getId().equals(tag.getParentId()))
                                .map(Tag::getTagName)
                                .findFirst().orElse(DEFAULT_GROUP);
                    }
                }));

        List<TagVO> tagVOList = new ArrayList<>();
        for (String parentTagName : parentTagMap.keySet()) {
            List<Tag> childrenTags = parentTagMap.get(parentTagName);
            List<String> childTagNameList = childrenTags.stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.toList());
            TagVO tagVO = new TagVO();
            tagVO.setParentTagName(parentTagName);
            tagVO.setChildTagNameList(childTagNameList);
            tagVOList.add(tagVO);
        }
        return tagVOList;
    }

    /**
     * 缓存分组后的标签列表
     *
     * @param tagVOList tag vo list
     */
    public void cacheTagVOList(List<TagVO> tagVOList) {
        HashOperations<String, String, List<String>> opsForHash = redisTemplate.opsForHash();
        // 写缓存，key|filed|value => TAG_CACHE_KEY | parentTagName | childTagNameList
        for (TagVO tagVO : tagVOList) {
            opsForHash.put(
                    TAG_CACHE_KEY,
                    tagVO.getParentTagName(),
                    tagVO.getChildTagNameList()
            );
        }
        // 设置过期时间
        redisTemplate.expire(TAG_CACHE_KEY, TAG_CACHE_TTL, TimeUnit.MINUTES);
    }

    /**
     * 缓存父标签id
     *
     * @param parentTagIdList parent tag id list
     */
    public void cacheParentTagId(List<Long> parentTagIdList) {
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        opsForSet.add(P_TAG_ID_KEY, parentTagIdList);
        // 设置过期时间
        redisTemplate.expire(P_TAG_ID_KEY, P_TAG_ID_CACHE_TTL, TimeUnit.HOURS);
    }
}
