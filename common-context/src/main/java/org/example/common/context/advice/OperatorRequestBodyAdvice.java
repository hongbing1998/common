package org.example.common.context.advice;

import org.example.common.context.RequestContext;
import org.example.common.context.model.User;
import org.example.common.core.annotation.Operator;
import org.example.common.core.util.AnnotationUtil;
import org.example.common.core.util.CollectionUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * @author 李红兵
 * @date 2021/4/27 14:44
 * @description OperatorRequestBodyAdvice
 */
@ControllerAdvice
public class OperatorRequestBodyAdvice extends RequestBodyAdviceAdapter {
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasParameterAnnotation(Operator.class);
    }

    @NonNull
    @Override
    public Object afterBodyRead(@NonNull Object body,
                                @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter,
                                @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取参数上的注解
        Operator parameterAnnotation = parameter.getParameterAnnotation(Operator.class);
        assert parameterAnnotation != null;

        // 将被Operator标注的String类型属性值填充为请求上下文的当前用户名，Integer或Long类型填充用户ID
        AnnotationUtil.updateAnnotated(body, Operator.class, (target, field, value, annotation) -> {
            // 生效条件：属性注解上没指定分组 或者 属性注解上指定有参数注解指定的同样的分组
            if (annotation.groups().length == 0
                    || CollectionUtil.containsSame(annotation.groups(), parameterAnnotation.groups())) {
                User user = RequestContext.currentUser();
                if (user == null) {
                    if (annotation.needAuth()) {
                        //  throw new BusinessException(ErrorCodeEnum.ERROR_NEED_LOGIN);
                    }
                    user = User.builder()
                            .id(annotation.defaultId())
                            .name(annotation.defaultName())
                            .build();
                }

                if (Long.class.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())) {
                    return annotation.override() ? user.getId() : value;
                }

                if (String.class.isAssignableFrom(field.getType())) {
                    return annotation.override() ? user.getName() : value;
                }
            }

            return value;
        });
        return body;
    }
}
