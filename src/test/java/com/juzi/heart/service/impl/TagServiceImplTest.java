package com.juzi.heart.service.impl;

import com.juzi.heart.model.vo.tag.TagVO;
import com.juzi.heart.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author codejuzi
 */
@SpringBootTest
class TagServiceImplTest {

    @Resource
    private TagService tagService;

    @Test
    void listTag() {
        List<TagVO> tagVOList = tagService.listTag();
        for (TagVO tagVO : tagVOList) {
            System.out.println("tagVO = " + tagVO);
        }
    }
}