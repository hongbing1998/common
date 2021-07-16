package org.example.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author: 李红兵
 * @date: 2021/4/12 00:34
 * @description: 标注在controller方法上，标识该方法响应体开启脱敏
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableMask {
}
