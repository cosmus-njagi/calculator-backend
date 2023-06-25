package com.pycs.calculatorbackend.model;

/**
 * @author njagi
 * @Date 25/06/2023
 */
public class Response {
    private int code;

    private String message;

    private Error error;

    private String returnCode;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public Response(int code, String message, Error error) {
        this.code = code;
        this.message = message;
        this.error = error;
    }

    public Response() {

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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
