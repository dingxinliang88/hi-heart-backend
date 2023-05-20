package com.juzi.heart.model.dto.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class TagEditRequest implements Serializable {

    private static final long serialVersionUID = -8024456461397792455L;

    /**
     * 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称（非空，唯一，唯一索引）
     */
    private String tagName;

    /**
     * 父标签id（分类用，自身是父标签值为-1，非空）
     */
    private Long parentId;
}
