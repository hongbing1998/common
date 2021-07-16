package org.example.common.core.annotation;


import java.lang.annotation.*;

/**
 * @author 李红兵
 * @date 2021/4/27
 * @description 操作人员
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Operator {
    /**
     * 是否需要登录权限，为true时如果未传用户信息会报未登录异常
     */
    boolean needAuth() default true;

    /**
     * 是否替换原始值
     */
    boolean override() default true;

    /**
     * needAuth为false并且override为true时，使用该默认值
     */
    String defaultName() default "system";

    /**
     * needAuth为false并且override为true时，，使用该默认值
     */
    long defaultId() default 0;

    /**
     * 生效分组，不同接口可能使用同一个请求体，但是取值逻辑可能不一样，这时候就需要指定分组
     */
    Class<?>[] groups() default { };
}


