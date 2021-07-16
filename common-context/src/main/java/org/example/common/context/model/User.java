package org.example.common.context.model;

import lombok.*;

/**
 * @author 李红兵
 * @date 2021/4/23 14:08
 * @description portal的user
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @Deprecated
    private String uuid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String name;

    private String ip;

    private String orgCode;
}
