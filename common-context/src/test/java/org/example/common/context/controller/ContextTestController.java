package org.example.common.context.controller;

import org.example.common.context.RequestContext;
import org.example.common.context.annotation.EnableHeader;
import org.example.common.context.model.ReqBody;
import org.example.common.context.model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李红兵
 * @date 2021/4/26 18:23
 * @description ContextTestController
 */
@RestController
@RequestMapping("/context-test")
public class ContextTestController {
    @RequestMapping("/request/get-user")
    public User getUser() {
        return RequestContext.currentUser();
    }

    @RequestMapping("/request/get-person")
    public ReqBody getPerson(@RequestBody @EnableHeader ReqBody reqBody) {
        return reqBody;
    }
}
