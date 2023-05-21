package com.juzi.heart.service;

import com.juzi.heart.common.DeleteRequest;
import com.juzi.heart.model.dto.tag.TagAddRequest;
import com.juzi.heart.model.dto.tag.TagEditRequest;
import com.juzi.heart.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.heart.model.vo.tag.TagVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author codejuzi
 * @description 针对表【tag(标签表)】的数据库操作Service
 * @createDate 2023-05-20 16:09:40
 */
public interface TagService extends IService<Tag> {

    /**
     * 新增tag
     *
     * @param tagAddRequest 新增tag请求封装信息
     * @param request       http request
     * @return 新tag的id
     */
    Long addTag(TagAddRequest tagAddRequest, HttpServletRequest request);

    /**
     * 根据tagName来模糊查询tag
     *
     * @param tagName tag name
     * @return tag list
     */
    List<Tag> queryTagByTagName(String tagName);

    /**
     * 根据标签对应的父标签id来查询tag
     *
     * @param parentId 父标签id
     * @return tag list
     */
    List<Tag> queryTagByParentId(Long parentId);

    /**
     * 获取所有的标签列表（父子标签）
     * 按照父标签分组，同时带有其子标签
     *
     * @return tag vo list
     */
    List<TagVO> listTag();


    /**
     * 修改标签信息（本人 || 管理员）
     *
     * @param tagEditRequest 标签修改请求封装信息
     * @param request        http request
     * @return true - 修改成功
     */
    Boolean editTag(TagEditRequest tagEditRequest, HttpServletRequest request);

    /**
     * 根据id删除tag
     *
     * @param deleteRequest 删除请求封装信息
     * @param request       http request
     * @return true - 删除成功
     */
    Boolean deleteTag(DeleteRequest deleteRequest, HttpServletRequest request);
}
