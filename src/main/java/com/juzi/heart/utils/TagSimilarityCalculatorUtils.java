package com.juzi.heart.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagSimilarityCalculatorUtils {

    /**
     * 计算两个子标签列表的相似度
     *
     * @param tags1            第一个子标签列表
     * @param tags2            第二个子标签列表
     * @param parentTagWeights 父标签的权重
     * @return 相似度值，范围在-1到1之间
     */
    public static double calculateSimilarity(Map<Long, List<Long>> tags1,
                                             Map<Long, List<Long>> tags2,
                                             Map<Long, Double> parentTagWeights) {
        // 将子标签列表表示为向量
        Map<Long, Map<Long, Double>> vector1 = toVector(tags1, parentTagWeights);
        Map<Long, Map<Long, Double>> vector2 = toVector(tags2, parentTagWeights);

        // 计算余弦相似度
        return cosineSimilarity(vector1, vector2);
    }

    /**
     * 将子标签列表表示为向量
     *
     * @param tags             子标签列表
     * @param parentTagWeights 父标签的权重
     * @return 向量表示
     */
    private static Map<Long, Map<Long, Double>> toVector(Map<Long, List<Long>> tags, Map<Long, Double> parentTagWeights) {
        Map<Long, Map<Long, Double>> vector = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : tags.entrySet()) {
            Long parentId = entry.getKey();
            List<Long> tagIds = entry.getValue();
            for (Long tagId : tagIds) {
                if (parentTagWeights.containsKey(parentId)) {
                    double weight = parentTagWeights.get(parentId);
                    if (!vector.containsKey(parentId)) {
                        vector.put(parentId, new HashMap<>());
                    }
                    vector.get(parentId).put(tagId, weight);
                }
            }
        }
        return vector;
    }

    /**
     * 计算余弦相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度值，范围在-1到1之间
     */
    private static double cosineSimilarity(Map<Long, Map<Long, Double>> vector1, Map<Long, Map<Long, Double>> vector2) {
        // 计算分子部分
        double numerator = 0.0;
        for (Long parentId : vector1.keySet()) {
            if (vector2.containsKey(parentId)) {
                Map<Long, Double> subVector1 = vector1.get(parentId);
                Map<Long, Double> subVector2 = vector2.get(parentId);
                for (Long key : subVector1.keySet()) {
                    if (subVector2.containsKey(key)) {
                        numerator += subVector1.get(key) * subVector2.get(key);
                    }
                }
            }
        }

        // 计算分母部分
        double denominator = euclideanNorm(vector1) * euclideanNorm(vector2);

        // 计算余弦相似度
        if (denominator == 0.0) {
            return 0.0;
        } else {
            return numerator / denominator;
        }
    }

    /**
     * 计算向量的模长（欧几里得范数）
     *
     * @param vector 向量
     * @return 向量的模长
     */
    private static double euclideanNorm(Map<Long, Map<Long, Double>> vector) {
        double normSquared = 0.0;
        for (Map<Long, Double> subVector : vector.values()) {
            for (Double value : subVector.values()) {
                normSquared += value * value;
            }
        }
        return Math.sqrt(normSquared);
    }

}
