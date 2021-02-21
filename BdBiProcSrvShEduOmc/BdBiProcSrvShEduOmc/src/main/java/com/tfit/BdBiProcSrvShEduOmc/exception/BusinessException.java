package com.tfit.BdBiProcSrvShEduOmc.exception;

import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;

/**
 * @Descritpion：业务异常
 * @author: tianfang_infotech
 * @date: 2019/1/3 16:15
 */
public class BusinessException extends RuntimeException {

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private Integer code;

    public BusinessException(IOTRspType iotRspType) {
        super(iotRspType.getMsg());
        this.setCode(iotRspType.getCode());
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.setCode(code);
    }

    public BusinessException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.setCode(code);
    }

    public BusinessException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
