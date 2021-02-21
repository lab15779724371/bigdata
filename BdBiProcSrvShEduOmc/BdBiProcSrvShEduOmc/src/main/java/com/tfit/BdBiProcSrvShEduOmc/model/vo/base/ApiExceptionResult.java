package com.tfit.BdBiProcSrvShEduOmc.model.vo.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import lombok.Data;

import java.io.Serializable;

/**
 * @Descritpion：Api异常响应信息
 * @author: tianfang_infotech
 * @date: 2019/1/3 11:27
 */
@Data
public class ApiExceptionResult<T> implements Serializable {

    public ApiExceptionResult() {

    }

    public ApiExceptionResult(T t) {
        this.result = t;
    }

    /**
     * 消息编号
     */
    private Long msgId = AppModConfig.msgId++;

    /**
     * 响应信息
     */
    private T result;

    @JsonProperty("status")
    private Integer status = 200;

}
