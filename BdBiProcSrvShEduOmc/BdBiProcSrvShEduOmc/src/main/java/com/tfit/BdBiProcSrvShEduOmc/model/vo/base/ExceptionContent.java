package com.tfit.BdBiProcSrvShEduOmc.model.vo.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import lombok.Data;

/**
 * @Descritpion：异常内容
 * @author: tianfang_infotech
 * @date: 2019/1/4 10:28
 */
@Data
public class ExceptionContent {

    public ExceptionContent(){

    }

    public ExceptionContent(IOTRspType iotRspType) {
        this.setCode(iotRspType.getCode());
        this.setMsg(iotRspType.getMsg());
    }

    public ExceptionContent(IOTRspType iotRspType, String message) {
        this.setCode(iotRspType.getCode());
        this.setMsg(String.format("%s -> %s", iotRspType.getMsg(), message));
    }

    public ExceptionContent(Integer code, String message) {
        this.setCode(code);
        this.setMsg(message);
    }

    /**
     * 提示信息
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 业务处理状态码
     */
    @JsonProperty("code")
    private Integer code;
}
