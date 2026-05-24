package com.eas.dddcore;

/**
 * 统一响应消息对象
 * 用于 REST 接口的统一响应格式。
 */
public class ResponseMessage {

    private int code;
    private String message;
    private Object data;

    public ResponseMessage() {
    }

    public ResponseMessage(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseMessage success() {
        return new ResponseMessage(200, "success", null);
    }

    public static ResponseMessage success(Object data) {
        return new ResponseMessage(200, "success", data);
    }

    public static ResponseMessage success(String message, Object data) {
        return new ResponseMessage(200, message, data);
    }

    public static ResponseMessage error(int code, String message) {
        return new ResponseMessage(code, message, null);
    }

    public static ResponseMessage error(String message) {
        return new ResponseMessage(500, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
