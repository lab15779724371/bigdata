package com.tfit.BdBiProcSrvShEduOmc.model.vo.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Descritpion：Api响应信息（不包含分页）
 * @author: tianfang_infotech
 * @date: 2019/1/3 11:27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {

    public ApiResult() {

    }

    public ApiResult(T t) {
        this.data = t;
    }

    public ApiResult(T t, PageInfo pageInfo) {
        this.data = t;
        this.pageInfo = pageInfo;
    }

    /**
     * 消息编号
     */
    private Long msgId = AppModConfig.msgId++;

    /**
     * 响应时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time = new Date();

    /**
     * 数据信息
     */
    private T data;

    /**
     * 数据分页信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("pageInfo")
    private PageInfo pageInfo;

}
