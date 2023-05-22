package com.juzi.heart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.heart.model.entity.UserTeam;
import com.juzi.heart.service.UserTeamService;
import com.juzi.heart.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author codejuzi
* @description 针对表【user_team(用户队伍表)】的数据库操作Service实现
* @createDate 2023-05-22 16:16:25
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




