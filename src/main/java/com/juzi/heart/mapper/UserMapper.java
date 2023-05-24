package com.juzi.heart.mapper;

import com.juzi.heart.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author codejuzi
 * @description 针对表【user】的数据库操作Mapper
 * @createDate 2023-05-15 20:18:05
 * @Entity com.juzi.heart.model.entity.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据搜索关键词查询用户id
     *
     * @param searchText 搜索关键词
     * @return user id list
     */
    List<Long> listLeaderOrCreatorId(String searchText);
}




