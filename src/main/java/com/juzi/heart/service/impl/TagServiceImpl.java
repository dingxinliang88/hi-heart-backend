package com.juzi.heart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.mapper.TagMapper;
import com.juzi.heart.model.dto.tag.TagAddRequest;
import com.juzi.heart.model.dto.tag.TagEditRequest;
import com.juzi.heart.model.entity.Tag;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.TagService;
import com.juzi.heart.service.UserService;
import com.juzi.heart.utils.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

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
        Integer hasChildren = HAS_CHILDREN;
        if (!DEFAULT_PARENT_ID.equals(parentId)) {
            // 如果不是添加的父标签，校验所属的父标签是否存在
            Tag tag = this.getById(parentId);
            ThrowUtils.throwIf(Objects.isNull(tag), StatusCode.NOT_FOUND_ERROR, "对应的父标签不存在");
            hasChildren = HAS_NO_CHILDREN;
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
    public Boolean editTag(TagEditRequest tagEditRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(tagEditRequest), StatusCode.PARAMS_ERROR, "修改标签参数不能为空");
        Long id = tagEditRequest.getId();
        String tagName = tagEditRequest.getTagName();
        Long parentId = tagEditRequest.getParentId();
        ThrowUtils.throwIf(id <= 0L, StatusCode.PARAMS_ERROR, "修改标签参数不合法！");
        ThrowUtils.throwIf(parentId < 0L, StatusCode.PARAMS_ERROR, "修改标签参数不合法！");
        UserVO loginUser = userService.getLoginUser(request);
        Tag editTag = this.getById(id);
        ThrowUtils.throwIf(Objects.isNull(editTag), StatusCode.NOT_FOUND_ERROR, "要修改的标签不存在！");
        boolean isAdmin = ADMIN.equals(loginUser.getUserRole());
        boolean isMe = editTag.getUserId().equals(loginUser.getId());
        // 管理员 || 自己  可以修改
        ThrowUtils.throwIf(!(isAdmin || isMe), StatusCode.NO_AUTH_ERROR, "你无权修改此标签!");

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
}




