package com.juzi.heart.model.dto.team;

import com.juzi.heart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 新增队伍请求
 *
 * @author codejuzi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词 => 队伍名称、队伍简介、队伍队长名称
     */
    private String searchText;

    /**
     * 队伍状态，0 - 公开、1 - 私有，2 - 加密，默认为0
     */
    private Integer status;
}
