package com.juzi.heart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.heart.model.entity.Tag;
import com.juzi.heart.service.TagService;
import com.juzi.heart.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author codejuzi
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2023-05-20 16:09:40
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




