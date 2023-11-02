package com.mcb.imspring.web.interceptor;

import com.mcb.imspring.web.view.ModelAndView;
import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerInterceptor 是类似于 AOP 中切面的一种概念逻辑，在 Controller 执行前后进行自定义逻辑处理
 *        │   ▲
 *        ▼   │
 *      ┌───────┐
 *      │Filter1│
 *      └───────┘
 *        │   ▲
 *        ▼   │
 *      ┌───────┐
 *      │Filter2│
 *      └───────┘
 *        │   ▲
 *        ▼   │
 * ┌─────────────────┐
 * │DispatcherServlet│<───┐
 * └─────────────────┘    │
 *  │ ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┼ ─ ─ ─ ┐
 *  │                     │
 *  │ │            ┌────────────┐ │
 *  │              │   Render   │
 *  │ │            └────────────┘ │
 *  │                     ▲
 *  │ │                   │       │
 *  │              ┌────────────┐
 *  │ │            │ModelAndView│ │
 *  │              └────────────┘
 *  │ │                   ▲       │ Interceptor 的处理范围
 *  │    ┌───────────┐    │
 *  ├─┼─>│Controller1│────┤       │
 *  │    └───────────┘    │
 *  │ │                   │       │
 *  │    ┌───────────┐    │
 *  └─┼─>│Controller2│────┘       │
 *       └───────────┘
 *    └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
 * 和 Filter相比，Interceptor 拦截范围不是后续整个处理流程，而是仅针对 Controller 拦截
 * 使用 Interceptor 的好处是 Interceptor 本身是 Spring 管理的 Bean，因此注入任意 Bean 都非常简单。
 * 此外可以应用多个 Interceptor，并通过简单的 @Order 指定顺序。
 */
public interface HandlerInterceptor {
    /**
     * 在 Controller 执行之前调用，如果返回 false，Controller 不执行
     */
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    /**
     * Controller 执行之后，且页面渲染之前调用
     */
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) {
    }

    /**
     * 页面渲染之后调用，一般用于资源清理操作
     */
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) {
    }
}
