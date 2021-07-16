package org.example.common.context;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.example.common.context.constant.HttpHeaders;
import org.example.common.context.model.User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author 李红兵
 * @date 2021/4/22 16:38
 * @description 请求上下文
 */
@Slf4j
public class RequestContext {
    private User user;

    private String requestId;

    private String branchCode;

    private HttpServletRequest request;

    private static final ThreadLocal<RequestContext> CURRENT_CONTEXT = new ThreadLocal<>();

    /**
     * 初始化
     *
     * @param currentContext 本次请求上下文
     * @param currentRequest 本次请求
     */
    public static void init(RequestContext currentContext, HttpServletRequest currentRequest) {
        log.debug("初始化请求上下文...........................................................");

        currentContext.request = currentRequest;
        long userId = -1L;
        String userIdStr = currentRequest.getHeader(HttpHeaders.HEADER_PORTAL_ID);
        if (!StringUtils.isEmpty(userIdStr)) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.debug("解析userId失败。userId={}", userIdStr);
            }
        }
        String username = currentRequest.getHeader(HttpHeaders.HEADER_PORTAL_USERNAME);
        currentContext.user = userId == -1 || StringUtils.isEmpty(username) ? null : User.builder()
                .uuid(username)
                .username(username)
                .id(userId)
                .name(decodeHeader(currentRequest, HttpHeaders.HEADER_PORTAL_REAL_NAME))
                .ip(currentRequest.getHeader(HttpHeaders.HEADER_CLIENT_IP))
                .orgCode(currentRequest.getHeader(HttpHeaders.HEADER_PORTAL_ORGANIZATION_CODE))
                .build();
        currentContext.requestId = currentRequest.getHeader(HttpHeaders.HEADER_REQUEST_ID);

        // 多机构编码，默认空字符
        String branchHeader = currentRequest.getHeader(HttpHeaders.HEADER_PORTAL_ORGANIZATION_CODE);
        currentContext.branchCode = Optional.ofNullable(branchHeader).orElse(Strings.EMPTY);
        log.debug("当前机构是:{}。 header: {}", currentContext.branchCode, branchHeader);

        CURRENT_CONTEXT.set(currentContext);
    }

    /**
     * 获取本次请求上下文
     *
     * @return 本次请求上下文
     */
    public static RequestContext current() {
        return CURRENT_CONTEXT.get();
    }

    /**
     * 获取portal请求的用户信息
     *
     * @return 用户信息
     */
    public static User currentUser() {
        return current().user;
    }

    /**
     * 获取当前请求
     *
     * @return 当前请求
     */
    public static HttpServletRequest currentRequest() {
        return current().request;
    }

    /**
     * 获取请求id
     *
     * @return 请求头设置的请求id
     */
    public static String currentRequestId() {
        return current().requestId;
    }

    /**
     * 获取请求机构
     *
     * @return 请求头设置的请求 机构
     */
    public static String currentBranchCode() {
        return current().branchCode;
    }

    /**
     * 清理上下文
     */
    public static void clean() {
        log.debug("清理请求上下文.............................................................");
        CURRENT_CONTEXT.remove();
    }

    /**
     * 解码请求头
     */
    public static String decodeHeader(HttpServletRequest request, String headerName) {
        String header = request.getHeader(headerName);
        return header == null ? null : new String(header.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
