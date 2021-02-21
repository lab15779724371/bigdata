package com.tfit.BdBiProcSrvShEduOmc.exception;

/**
 * @Descritpion：非法参数异常类
 * @author: tianfang_infotech
 * @date: 2019/1/3 16:25
 */
public class IllegalArgsException extends RuntimeException {
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private Integer code;

    public IllegalArgsException(Integer code, String message) {
        super(message);
        this.setCode(code);
    }

    public IllegalArgsException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.setCode(code);
    }

    public IllegalArgsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
