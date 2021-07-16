/**
 * 上下文组件相关注解包
 * 历史原因Operator注解写在了common-core里面
 * 关于注解中的groups属性，参照spring参数校验注解中的groups
 * 不同接口可能使用同一个请求体，但是取值逻辑可能不一样，这时候就需要指定分组
 *
 * @author 李红兵
 * @date 2021/5/28 11:42
 */
package org.example.common.context.annotation;