package org.example.common.core.util;


import org.example.common.core.function.SFunction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 李红兵
 * @date: 2021/4/2 17:02
 * @description: 枚举工具
 */
public class EnumUtil extends cn.hutool.core.util.EnumUtil {
    /**
     * 获取枚举所有值的方法名
     */
    private static final String METHOD_NAME_VALUES = "values";

    /**
     * 枚举索引缓存，使用枚举类和映射方法的哈希值作为缓存key
     */
    private static final Map<Integer, Map<?, ?>> ENUM_VALUES_CACHE = new ConcurrentHashMap<>();

    /**
     * 按照指定值和映射规则获取枚举值
     *
     * @param mapper 属性映射规则函数
     * @param value  枚举某一属性值
     * @param <E>    枚举类型
     * @return E的一个枚举示例
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<?>> E valueOf(SFunction<E, ?> mapper, Object value) {
        try {
            // 拿到方法引用的具体信息
            String methodName = LambdaUtil.getMethodName(mapper);
            Class<E> enumClass = (Class<E>) LambdaUtil.getImplClass(mapper);

            // 从索引缓存中取该枚举类型的索引
            int indexCacheKey = Objects.hash(enumClass, methodName);
            Map<?, ?> index = ENUM_VALUES_CACHE.get(indexCacheKey);

            // 没有索引先创建索引
            if (index == null) {
                // 通过反射拿到该枚举的所有值
                Method valuesMethod = enumClass.getDeclaredMethod(METHOD_NAME_VALUES);
                List<E> enumValues = Arrays.asList((E[]) valuesMethod.invoke(enumClass));

                // 创建索引并缓存
                index = CollectionUtil.index(enumValues, mapper);
                ENUM_VALUES_CACHE.put(indexCacheKey, index);
            }

            // 从索引中取值
            return (E) index.get(value);
        } catch (Exception e) {
            throw new RuntimeException("枚举转换异常");
        }
    }
}
