package org.example.common.core.function;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @param <A> 要更新的对象类型
 * @param <B> 要更新的属性类型
 * @param <C> 要更新的属性原始值类型
 * @param <D> 属性标注的注解类型
 * @author 李红兵
 * @date 2021/4/27
 * @description UpdateAction
 */
@FunctionalInterface
public interface UpdateAction<A, B extends Field, C, D extends Annotation> {
    /**
     * 执行自定义更新动作，返回更新后的值或者空
     *
     * @param a 更新对象
     * @param b 要更新的属性
     * @param c 原始属性值，可能为空，处理时需要注意
     * @param d 属性标注的注解
     * @return 更新后的属性值
     * @throws Exception 如果非法访问属性
     */
    @Nullable
    C apply(A a, B b, @Nullable C c, D d) throws Exception;
}