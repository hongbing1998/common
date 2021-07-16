package org.example.common.context.annotation;

import java.lang.annotation.*;

/**
 * @author 李红兵
 * @date 2021/5/28 11:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface EnableHeader {
    /**
     * 生效分组，不同接口可能使用同一个请求体，但是取值逻辑可能不一样，这时候就需要指定分组
     */
    Class<?>[] groups() default {};
}
