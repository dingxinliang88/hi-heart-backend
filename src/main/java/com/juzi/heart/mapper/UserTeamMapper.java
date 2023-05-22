package com.juzi.heart.mapper;

import com.juzi.heart.model.entity.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author codejuzi
* @description 针对表【user_team(用户队伍表)】的数据库操作Mapper
* @createDate 2023-05-22 16:16:25
* @Entity com.juzi.heart.model.entity.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




