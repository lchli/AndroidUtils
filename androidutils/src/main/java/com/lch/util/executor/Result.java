package com.lch.util.executor;

public class Result<DATA> {
    private int code;
    private String msg;
    private DATA data;
    private boolean hasError = false;

    public Result(int code, String msg, DATA data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(DATA data) {
        this.data = data;
    }

    public Result() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
        hasError = true;
    }

    public DATA getData() {
        return data;
    }

    public void setData(DATA data) {
        this.data = data;
    }

    public boolean hasError() {
        return hasError;
    }
}
