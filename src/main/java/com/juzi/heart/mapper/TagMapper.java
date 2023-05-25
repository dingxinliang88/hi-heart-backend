package com.juzi.heart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.juzi.heart.model.entity.Tag;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author codejuzi
 * @description 针对表【tag(标签表)】的数据库操作Mapper
 * @createDate 2023-05-20 16:09:40
 * @Entity com.juzi.heart.model.entity.Tag
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 迁移子标签到新的父标签
     *
     * @param newParentId 新父标签id
     * @param oldParentId 老父标签id
     * @return true - 迁移成功
     */
    Boolean migrateChildTags(Long newParentId, Long oldParentId);

    /**
     * 获取标签名称获取标签
     *
     * @param tagNameList 标签名称列表
     * @return tag  list
     */
    List<Tag> getChildTagByTagName(List<String> tagNameList);

}




