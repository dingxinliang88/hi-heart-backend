package com.juzi.heart.controller;

import com.juzi.heart.common.BaseResponse;
import com.juzi.heart.common.DeleteRequest;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.model.dto.tag.TagAddRequest;
import com.juzi.heart.model.dto.tag.TagEditRequest;
import com.juzi.heart.model.entity.Tag;
import com.juzi.heart.model.vo.tag.TagVO;
import com.juzi.heart.service.TagService;
import com.juzi.heart.utils.ResultUtils;
import com.juzi.heart.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping("/add")
    public BaseResponse<Long> addTag(@RequestBody TagAddRequest tagAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(tagAddRequest), StatusCode.PARAMS_ERROR, "新增标签参数不能为空！");
        Long tagId = tagService.addTag(tagAddRequest, request);
        return ResultUtils.success(tagId);
    }

    @GetMapping("/query/name")
    public BaseResponse<List<Tag>> queryTagByTagName(@RequestParam(value = "tagName") String tagName) {
        ThrowUtils.throwIf(StringUtils.isBlank(tagName), StatusCode.PARAMS_ERROR, "查询标签参数不能为空！");
        List<Tag> tagList = tagService.queryTagByTagName(tagName);
        String message = Objects.isNull(tagList) ? "没有查到哦～" : "查询成功";
        return ResultUtils.success(tagList, message);
    }

    @GetMapping("/query/id")
    public BaseResponse<List<Tag>> queryTagByParentId(@RequestParam(value = "parentId") Long parentId) {
        ThrowUtils.throwIf(parentId <= 0L, StatusCode.PARAMS_ERROR, "查询标签参数不能为空！");
        List<Tag> tagList = tagService.queryTagByParentId(parentId);
        String message = Objects.isNull(tagList) ? "没有查到哦～" : "查询成功";
        return ResultUtils.success(tagList, message);
    }

    @GetMapping("/tag_list")
    public BaseResponse<List<TagVO>> listTag() {
        List<TagVO> tagVOList = tagService.listTag();
        return ResultUtils.success(tagVOList);
    }

    @PutMapping("/edit")
    public BaseResponse<Boolean> editTag(@RequestBody TagEditRequest tagEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(tagEditRequest), StatusCode.PARAMS_ERROR, "修改标签参数不能为空！");
        Boolean editRes = tagService.editTag(tagEditRequest, request);
        return ResultUtils.success(editRes);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(deleteRequest), StatusCode.PARAMS_ERROR, "删除标签参数不能为空！");
        Boolean deleteRes = tagService.deleteTag(deleteRequest, request);
        return ResultUtils.success(deleteRes);
    }
}
