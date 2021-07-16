package org.example.common.core.util;

import org.example.common.core.function.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author 李红兵
 * @date 2021/4/5 17:25
 */
public class LambdaUtil {
    private static final String METHOD_NAME_WRITE_REPLACE = "writeReplace";

    /**
     * 获取方法引用的方法名称
     *
     * @param methodReference 方法引用
     * @return 给定方法引用对应的实现方法名称
     */
    public static String getMethodName(SFunction<?, ?> methodReference) {
        try {
            Method method = methodReference.getClass().getDeclaredMethod(METHOD_NAME_WRITE_REPLACE);
            method.setAccessible(true);
            return ((SerializedLambda) method.invoke(methodReference)).getImplMethodName();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取方法引用实现的类
     *
     * @param methodReference 方法引用
     * @return 给定方法引用实现的类
     */
    public static Class<?> getImplClass(SFunction<?, ?> methodReference) {
        try {
            Method method = methodReference.getClass().getDeclaredMethod(METHOD_NAME_WRITE_REPLACE);
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(methodReference);
            String implClassName = serializedLambda.getImplClass().replace('/', '.');
            return Class.forName(implClassName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
