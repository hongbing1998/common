package org.example.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: edward
 * @date: 2019/11/5 17:27
 */
@Slf4j
public class BeanUtil extends BeanUtils {
    /**
     * 指定对象包括父类的所有属性
     *
     * @param o 要获取属性的对象
     * @return 指定对象包括父类的所有属性
     */
    public static List<Field> getAllFields(Object o) {
        if (Objects.isNull(o)) {
            return Collections.emptyList();
        }
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = o.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        return fields;
    }

    /**
     * 将指定对象属性拷贝到指定类对象的属性
     *
     * @param source      要拷贝属性的对象
     * @param targetClass 目标类
     * @param <T>         目标类类型
     * @return 拷贝后的对象
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (Objects.isNull(source)) {
            return null;
        }
        try {
            T t = targetClass.newInstance();
            BeanUtil.copyProperties(source, t);
            return t;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
