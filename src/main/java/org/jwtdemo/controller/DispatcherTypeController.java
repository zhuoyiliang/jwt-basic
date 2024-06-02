package org.jwtdemo.controller;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author mengdanai
 */
@RestController
public class DispatcherTypeController {

    @GetMapping("/dispatcherType")
    public String getDispatcherType(HttpServletRequest request) {
        // 获取当前请求的DispatcherType
        DispatcherType dispatcherType = request.getDispatcherType();

        // 在IDEA的控制台打印DispatcherType
        System.out.println("Current DispatcherType: " + dispatcherType);

        // 返回响应
        return "Current DispatcherType: " + dispatcherType;
    }
}
