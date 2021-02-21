package com.tfit.BdBiProcSrvShEduOmc.dto;

public class IOTException extends RuntimeException {

    private Integer errorCode = null;

    public IOTException(Integer errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public IOTException(Integer errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public IOTException(Integer errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public IOTException(Integer errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public static IOTException createNew(IOTRspType ispError, Throwable cause) {
        return new IOTException(ispError.getCode(), ispError.getMsg(), cause);
    }

    public static IOTException createNew(IOTRspType ispError) {
        return new IOTException(ispError.getCode(), ispError.getMsg());
    }
}
