package org.example.common.core.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author: 李红兵
 * @date: 2021/4/5 17:08
 * @description: 可序列化函数接口
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable, Function<T, R> {
}