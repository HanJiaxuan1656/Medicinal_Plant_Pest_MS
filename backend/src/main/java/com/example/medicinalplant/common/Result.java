package com.example.medicinalplant.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果类
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; // 状态码：1成功，0和其它数字为失败
    private String msg; // 提示信息
    private T data; // 数据
    private String token; // 新增 token 字段

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "操作成功";
        return result;
    }

    public static <T> Result<T> success(T object, String token) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        result.msg = "操作成功";
        result.token = token;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        result.msg = "操作成功";
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = code;
        return result;
    }
}