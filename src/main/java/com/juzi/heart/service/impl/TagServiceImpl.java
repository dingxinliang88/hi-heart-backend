package com.juzi.heart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.heart.common.DeleteRequest;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.exception.BusinessException;
import com.juzi.heart.manager.AuthManager;
import com.juzi.heart.mapper.TagMapper;
import com.juzi.heart.model.dto.tag.TagAddRequest;
import com.juzi.heart.model.dto.tag.TagEditRequest;
import com.juzi.heart.model.entity.Tag;
import com.juzi.heart.model.vo.tag.TagVO;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.TagService;
import com.juzi.heart.service.UserService;
import com.juzi.heart.utils.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.juzi.heart.constant.TagConstants.*;
import static com.juzi.heart.constant.UserConstants.ADMIN;

/**
 * @author codejuzi
 * @description 针对表【tag(标签表)】的数据库操作Service实现
 * @createDate 2023-05-20 16:09:40
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

    @Resource
    private UserService userService;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private AuthManager authManager;

    @Override
    public Long addTag(TagAddRequest tagAddRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(tagAddRequest), StatusCode.PARAMS_ERROR, "新增标签参数不能为空！");
        String tagName = tagAddRequest.getTagName();
        Long parentId = tagAddRequest.getParentId();
        ThrowUtils.throwIf(StringUtils.isBlank(tagName), StatusCode.PARAMS_ERROR, "标签名不能为空！");
        ThrowUtils.throwIf(parentId < 0, StatusCode.PARAMS_ERROR, "id不能小于0！");
        // 获取当前登录用户
        UserVO loginUser = userService.getLoginUser(request);
        Integer hasChildren = HAS_NO_CHILDREN;
        if (!DEFAULT_PARENT_ID.equals(parentId)) {
            // 如果不是添加的父标签，校验所属的父标签是否存在
            Tag tag = this.getById(parentId);
            ThrowUtils.throwIf(Objects.isNull(tag), StatusCode.NOT_FOUND_ERROR, "对应的父标签不存在");
        } else {
            // 添加的是父标签，只有管理员可以添加父标签
            ThrowUtils.throwIf(!ADMIN.equals(loginUser.getUserRole()), StatusCode.NO_AUTH_ERROR, "你不能添加父标签！");
            hasChildren = HAS_CHILDREN;
        }
        // 插入数据
        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setUserId(loginUser.getId());
        tag.setHasChildren(hasChildren);
        tag.setParentId(parentId);
        this.save(tag);
        return tag.getId();
    }

    @Override
    public List<Tag> queryTagByTagName(String tagName) {
        ThrowUtils.throwIf(StringUtils.isBlank(tagName), StatusCode.PARAMS_ERROR, "查询标签参数不能为空！");
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Tag::getTagName, tagName);
        return this.list(queryWrapper);
    }

    @Override
    public List<Tag> queryTagByParentId(Long parentId) {
        ThrowUtils.throwIf(parentId <= 0L, StatusCode.PARAMS_ERROR, "标签id不合法！");
        // 判断用户传的父标签是否存在并且是否是父标签
        Tag parentTag = this.getById(parentId);
        ThrowUtils.throwIf(!DEFAULT_PARENT_ID.equals(parentTag.getParentId()),
                StatusCode.PARAMS_ERROR, "该标签不是父标签");
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getParentId, parentTag.getId());
        return this.list(queryWrapper);
    }

    @Override
    public List<TagVO> listTag() {
        List<Tag> tagList = this.list();
        // 分组，按照父标签名称 || 默认分组（存放所有的子标签）
        Map<String, List<Tag>> parentTagMap = tagList.stream()
                .collect(Collectors.groupingBy(tag -> {
                    if (tag.getParentId() == 0) {
                        return tag.getTagName();
                    } else {
                        // 根据父标签id获取父标签名称
                        return tagList.stream()
                                .filter(t -> t.getId().equals(tag.getParentId()))
                                .map(Tag::getTagName)
                                .findFirst().orElse(DEFAULT_GROUP);
                    }
                }));

        List<TagVO> result = new ArrayList<>();
        for (String parentTagName : parentTagMap.keySet()) {
            List<Tag> childrenTags = parentTagMap.get(parentTagName);
            List<String> childTagNameList = childrenTags.stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.toList());
            TagVO tagVO = new TagVO();
            tagVO.setParentTagName(parentTagName);
            tagVO.setChildTagNameList(childTagNameList);
            result.add(tagVO);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {BusinessException.class})
    public Boolean editTag(TagEditRequest tagEditRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(tagEditRequest), StatusCode.PARAMS_ERROR, "修改标签参数不能为空");
        Long id = tagEditRequest.getId();
        String tagName = tagEditRequest.getTagName();
        Long parentId = tagEditRequest.getParentId();
        ThrowUtils.throwIf(id <= 0L, StatusCode.PARAMS_ERROR, "修改标签参数不合法！");
        ThrowUtils.throwIf(parentId < 0L, StatusCode.PARAMS_ERROR, "修改标签参数不合法！");
        Tag editTag = this.getById(id);
        ThrowUtils.throwIf(Objects.isNull(editTag), StatusCode.NOT_FOUND_ERROR, "要修改的标签不存在！");
        // 管理员 || 自己
        authManager.adminOrMe(editTag.getUserId(), request);

        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        // 修改tagName
        updateWrapper.eq(Tag::getId, id)
                .set(StringUtils.isNotBlank(tagName), Tag::getTagName, tagName);
        // 如果原本不是父标签，要修改为父标签
        if (!DEFAULT_PARENT_ID.equals(editTag.getParentId()) && DEFAULT_PARENT_ID.equals(parentId)) {
            updateWrapper.set(Tag::getParentId, DEFAULT_PARENT_ID);
            updateWrapper.set(Tag::getHasChildren, HAS_CHILDREN);
            return this.update(updateWrapper);
        }
        // 原本是父标签，现在要改成不是父标签
        if (DEFAULT_PARENT_ID.equals(editTag.getParentId()) && !DEFAULT_PARENT_ID.equals(parentId)) {
            // 判断parentId对应的标签是否是父标签
            Tag predicateTag = this.getById(parentId);
            ThrowUtils.throwIf(!DEFAULT_PARENT_ID.equals(predicateTag.getParentId()),
                    StatusCode.PARAMS_ERROR, "待挂载的标签不是父标签！");
            // 获取此父标签下的所有子标签，将其迁移到要修改的parentId对应的父标签下
            // update table tag set parentId = #{newParentId} where isDelete = 0 and parentId = #{id};
            tagMapper.migrateChildTags(parentId, id);
            updateWrapper.set(Tag::getParentId, parentId);
            updateWrapper.set(Tag::getHasChildren, HAS_NO_CHILDREN);
        }
        return this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = {BusinessException.class})
    public Boolean deleteTag(DeleteRequest deleteRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(deleteRequest), StatusCode.PARAMS_ERROR, "删除标签参数不能为空");
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id <= 0L, StatusCode.PARAMS_ERROR, "删除标签参数不合法！");
        Tag deleteTag = this.getById(id);
        ThrowUtils.throwIf(Objects.isNull(deleteTag), StatusCode.NOT_FOUND_ERROR, "要删除的标签不存在！");
        // 管理员 || 自己
        authManager.adminOrMe(deleteTag.getUserId(), request);

        if (DEFAULT_PARENT_ID.equals(deleteTag.getParentId())) {
            // 要删除的标签是父标签，需要将其下所有的子标签删除
            LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Tag::getParentId, id);
            List<Long> childTagIdList = this.list(queryWrapper).stream().map(Tag::getId).collect(Collectors.toList());
            this.removeBatchByIds(childTagIdList);
        }
        // 删除父标签
        return this.removeById(id);
    }
}




