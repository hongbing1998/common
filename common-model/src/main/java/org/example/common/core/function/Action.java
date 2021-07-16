package org.example.common.core.function;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @param <A> 指定对象类型
 * @param <B> 指定属性类型
 * @param <C> 指定属性值类型
 * @param <D> 指定属性标注的注解类型
 * @author 李红兵
 * @date 2021/1/10
 */
@FunctionalInterface
public interface Action<A, B extends Field, C, D extends Annotation> {
    /**
     * 执行自定义动作
     *
     * @param a 要执行的对象
     * @param b 要处理的属性
     * @param c 属性值，可能为空
     * @param d 属性上的注解
     * @throws Exception 如果非法访问属性
     */
    void run(A a, B b, @Nullable C c, D d) throws Exception;
}