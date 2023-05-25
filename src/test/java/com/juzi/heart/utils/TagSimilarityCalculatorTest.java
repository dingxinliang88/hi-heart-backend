package com.juzi.heart.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author codejuzi
 */
class TagSimilarityCalculatorTest {

    @Test
    void calculateSimilarity() {

        Map<Long, List<Long>> tags1 = new HashMap<>() {{
            put(1L, Arrays.asList(2L, 3L, 4L));
            put(7L, Arrays.asList(8L, 9L));
            put(12L, Arrays.asList(13L, 15L));
        }};

        Map<Long, List<Long>> tags2 = new HashMap<>() {{
            put(1L, Arrays.asList(2L, 4L, 6L));
            put(7L, Arrays.asList(8L, 9L));
            put(12L, Arrays.asList(14L, 16L));
        }};

        Map<Long, List<Long>> tags3 = new HashMap<>() {{
            put(1L, Arrays.asList(4L, 5L));
            put(7L, Arrays.asList(8L, 10L));
            put(12L, Arrays.asList(14L, 16L));
        }};

        Map<Long, Double> parentTagWeights = new HashMap<>() {{
            put(1L, 5.0);
            put(7L, 3.0);
            put(12L, 2.0);
        }};

        double similarity1 = TagSimilarityCalculatorUtils.calculateSimilarity(tags1, tags2, parentTagWeights);
        System.out.println("similarity1 = " + similarity1);

        double similarity2 = TagSimilarityCalculatorUtils.calculateSimilarity(tags1, tags3, parentTagWeights);
        System.out.println("similarity2 = " + similarity2);

    }

}