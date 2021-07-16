package org.example.common.context.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.example.common.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 李红兵
 * @date 2021/4/22 17:00
 * @description 请求上下文拦截器
 */
public class RequestContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        RequestContext.init(new RequestContext(), request);
        return true;
    }

    /**
     * 与postHandle区分，本拦截器的preHandle返回true，无论后续拦截器返回什么结果或者出现异常，都会调用该方法
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        RequestContext.clean();
    }
}
