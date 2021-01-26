package com.aac.kpi.common.model;

import com.aac.kpi.common.exception.BizException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * API 返回数据 通用格式
 * @param <T> 数据类型
 */
@Data
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    private ApiResult(){
    }


    private ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private ApiResult(ApiResultStatus apiResultStatus, T data){
        this.code = apiResultStatus.getCode();
        this.message = apiResultStatus.getMessage();
        this.data = data;
    }

    public static <T> T getData(ApiResult<T> result){
        T ob;
        if(result == null){
            throw new BizException("Api结果数据获取异常");
        }
        if(!ApiResultStatus.SUCCESS.getCode().equals(result.getCode())){
            throw new BizException("Api结果数据获取异常: " + result.getMessage());
        }
        if((ob = result.getData()) == null){
            throw new BizException("Api结果数据格式错误: " + result.getMessage());
        }
        return ob;
    }

    public static ApiResult ofSuccess(){
        return ofSuccess(null);
    }

    public static <T> ApiResult ofSuccess(T data){
        return new ApiResult<T>(ApiResultStatus.SUCCESS, data);
    }

    public static <T> ApiResult ofFailServer(){
        return new ApiResult<T>(ApiResultStatus.FAIL_SERVER, null);
    }

    public static <T> ApiResult ofFailServer(String message){
        return new ApiResult<T>(ApiResultStatus.FAIL_SERVER.getCode(), message, null);
    }

    public static <T> ApiResult ofFailServer(T data){
        return new ApiResult<T>(ApiResultStatus.FAIL_SERVER, data);
    }

    public static <T> ApiResult ofFailServer(String message, T data){
        return new ApiResult<T>(ApiResultStatus.FAIL_SERVER.getCode(), message, data);
    }

    public static <T> ApiResult ofFailClient(){
        return new ApiResult<T>(ApiResultStatus.FAIL_CLIENT, null);
    }

    public static <T> ApiResult ofFailClient(String message){
        return new ApiResult<T>(ApiResultStatus.FAIL_CLIENT.getCode(), message, null);
    }

    public static <T> ApiResult ofFailClientNotFound(){
        return new ApiResult<T>(ApiResultStatus.FAIL_CLIENT_NOT_FOUND, null);
    }

    public static <T> ApiResult ofFailClientUnauthorized(){
        return new ApiResult<T>(ApiResultStatus.FAIL_CLIENT_UNAUTHORIZED, null);
    }
}
