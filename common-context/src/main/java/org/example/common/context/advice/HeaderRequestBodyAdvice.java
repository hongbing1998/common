package org.example.common.context.advice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.common.context.RequestContext;
import org.example.common.context.annotation.EnableHeader;
import org.example.common.context.annotation.Header;
import org.example.common.core.util.AnnotationUtil;
import org.example.common.core.util.CollectionUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author 李红兵
 * @date 2021/5/28 11:30
 */
@Slf4j
@ControllerAdvice
public class HeaderRequestBodyAdvice extends RequestBodyAdviceAdapter {
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasParameterAnnotation(EnableHeader.class);
    }

    @SneakyThrows
    @NonNull
    @Override
    public Object afterBodyRead(@NonNull Object body,
                                @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter,
                                @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取参数上的注解
        EnableHeader parameterAnnotation = parameter.getParameterAnnotation(EnableHeader.class);
        assert parameterAnnotation != null;

        // 将被Operator标注的String类型属性值填充为请求上下文的当前用户名，Integer类型填充用户ID
        AnnotationUtil.updateAnnotated(body, Header.class, (target, field, value, annotation) -> {
            // 生效条件：Header没指定分组 或者 Header指定有EnableHeader指定的同样的分组
            if (annotation.groups().length == 0
                    || CollectionUtil.containsSame(annotation.groups(), parameterAnnotation.groups())) {
                Class<?> fieldType = field.getType();
                if (!ClassUtils.isPrimitiveOrWrapper(fieldType) && !String.class.isAssignableFrom(fieldType)) {
                    log.warn("header只支持简单数据类型，当前属性类型[{}]", fieldType.getName());
                    return value;
                }

                // 原始值不为空且注解指定不覆盖，则不更新
                if (value != null && !annotation.override()) {
                    return value;
                }

                Object headerValue;
                HttpServletRequest request = RequestContext.currentRequest();

                if (String.class.isAssignableFrom(fieldType)) {
                    headerValue = RequestContext.decodeHeader(request, annotation.value());
                } else {
                    // 获取属性类型的包装类的参数类型为String的构造方法，使用header值作为参数构造实例
                    Class<?> fieldWrapperType = ClassUtils.resolvePrimitiveIfNecessary(fieldType);
                    Constructor<?> stringConstructor = fieldWrapperType.getDeclaredConstructor(String.class);
                    headerValue = stringConstructor.newInstance(request.getHeader(annotation.value()));
                }

                if (headerValue == null) {
                    log.warn("当前请求没有[{}]header", annotation.value());
                    return null;
                }

                return headerValue;
            }

            return value;
        });
        return body;
    }
}
