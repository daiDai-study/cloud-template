package com.aac.kpi.common.model;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

@Getter
public enum ApiResultStatus {

    SUCCESS(HttpServletResponse.SC_OK, "成功"),

    FAIL_SERVER(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务端异常"),

    FAIL_CLIENT(HttpServletResponse.SC_BAD_REQUEST, "客户端异常"),

    FAIL_CLIENT_NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "资源不存在"),

    FAIL_CLIENT_UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "权限不足"),
    ;



    private Integer code;

    private String message;

    private ApiResultStatus(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
