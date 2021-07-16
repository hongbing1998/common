package org.example.common.core.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.common.core.function.Action;
import org.example.common.core.function.UpdateAction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 注解工具
 *
 * @author 李红兵
 * @date 2021/2/7
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationUtil extends cn.hutool.core.annotation.AnnotationUtil {
    /**
     * 对于每一个被指定注解标注的属性执行指定的处理动作
     *
     * @param target          要处理的对象
     * @param annotationClass 要处理的注解类
     * @param action          对于每一个被注解标识的属性的处理方式
     * @param <T>             要处理的注解类型
     */
    public static <T extends Annotation> void handleAnnotated(Object target,
                                                              Class<T> annotationClass,
                                                              Action<Object, Field, Object, T> action) {
        HashMap<Object, Object> handledMap = new HashMap<>(16);
        handleAnnotated(target, annotationClass, action, handledMap);
        handledMap.clear();
    }

    /**
     * 对于每一个被指定注解标注的属性执行指定的更新动作
     * 多元属性每一项与单体属性执行同一更新逻辑
     *
     * @param target          要更新的对象
     * @param annotationClass 要处理的注解类
     * @param updateAction    对于每一个被注解标识的属性的更新方式
     * @param <T>             要处理的注解类型
     */
    public static <T extends Annotation> void updateAnnotated(Object target,
                                                              Class<T> annotationClass,
                                                              UpdateAction<Object, Field, Object, T> updateAction) {
        handleAnnotated(target, annotationClass, (object, field, value, annotation) -> {
            // 该属性是多元类型
            if (value instanceof Collection || value instanceof Iterator || value instanceof Enumeration) {
                // 集合转数组，修改每一项的值
                Collection collection = CollectionUtil.convertToCollection(value);
                Object[] array = collection.toArray();
                for (int i = 0; i < array.length; i++) {
                    array[i] = updateAction.apply(object, field, array[i], annotation);
                }
                // 清空集合，将修改后的数组重新添加进集合
                collection.clear();
                Collections.addAll(collection, array);
                return;
            }

            // Map类型直接修改对应值
            if (value instanceof Map) {
                Map map = (Map) value;
                for (Object key : map.keySet()) {
                    map.put(key, updateAction.apply(object, field, map.get(key), annotation));
                }
                return;
            }

            // 数组类型元素可以直接修改，单独拿出来
            if (field.getType().isArray()) {
                for (int i = 0; i < Array.getLength(value); i++) {
                    Array.set(value, i, updateAction.apply(object, field, Array.get(value, i), annotation));
                }
                return;
            }

            // 该属性是单体类型
            field.set(object, updateAction.apply(object, field, value, annotation));
        });
    }

    /**
     * 对于每一个被指定注解标注的属性执行指定的处理动作
     *
     * @param target          要处理的对象
     * @param annotationClass 要处理的注解类
     * @param action          对于每一个被注解标识的属性的处理方式
     * @param handledMap      存储已遍历过的对象，避免无限递归
     * @param <T>             要处理的注解类型
     */
    @SneakyThrows
    private static <T extends Annotation> void handleAnnotated(Object target,
                                                               Class<T> annotationClass,
                                                               Action<Object, Field, Object, T> action,
                                                               Map<Object, Object> handledMap) {
        // 对象本身是空值、基本数据类型、动作是空和已处理过该对象的情况不处理
        if (target == null || action == null
                || BeanUtil.isSimpleValueType(target.getClass())
                || handledMap.put(target, target) != null) {
            return;
        }

        // 集合、Map、迭代器、数组、枚举递归处理每一项
        if (target instanceof Collection || target.getClass().isArray()
                || target instanceof Map || target instanceof Iterator || target instanceof Enumeration) {
            Collection collection = CollectionUtil.convertToCollection(target);

            for (Object item : collection) {
                handleAnnotated(item, annotationClass, action, handledMap);
            }
            return;
        }

        // 遍历处理该对象的每一个属性
        List<Field> allFields = BeanUtil.getAllFields(target);
        for (Field field : allFields) {
            // 当前属性是基本数据类型
            if (BeanUtil.isSimpleValueType(field.getType())) {
                // 有注解标注则执行动作
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    action.run(target, field, field.get(target), field.getDeclaredAnnotation(annotationClass));
                }
                continue;
            }

            // 获取当前属性值
            field.setAccessible(true);
            Object fieldValue = field.get(target);

            // 当前属性值为空
            if (fieldValue == null) {
                continue;
            }

            // 当前属性是集合、键值对、迭代器、枚举（与Enum不是同一个东西）类型
            if (fieldValue instanceof Collection || fieldValue.getClass().isArray()
                    || fieldValue instanceof Map || fieldValue instanceof Iterator || fieldValue instanceof Enumeration) {
                Collection collection = CollectionUtil.convertToCollection(fieldValue);
                if (collection.isEmpty()) {
                    continue;
                }

                Class contentType = null;
                for (Object item : collection) {
                    if (item != null) {
                        contentType = item.getClass();
                        break;
                    }
                }
                if (contentType == null) {
                    continue;
                }

                // 集合内部元素是基本数据类型
                if (BeanUtil.isSimpleValueType(contentType)) {
                    if (field.isAnnotationPresent(annotationClass)) {
                        action.run(target, field, fieldValue, field.getDeclaredAnnotation(annotationClass));
                    }
                    continue;
                }

                // 集合内部元素不是基本数据类型，遍历递归处理每一个元素
                for (Object item : collection) {
                    handleAnnotated(item, annotationClass, action, handledMap);
                }
                continue;
            }

            // 当前属性是实体对象类型
            handleAnnotated(fieldValue, annotationClass, action, handledMap);
        }
    }
}
