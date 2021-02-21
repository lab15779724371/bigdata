package com.tfit.BdBiProcSrvShEduOmc.dto.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Descritpion：身份验证模型
 * @author: tianfang_infotech
 * @date: 2019/1/3 10:30
 */
@Data
public class AuthorizationDTO {

    /**
     * 授权码
     */
    String authorization;
    
    public String getAuthorization() {
    	return null;
    }
}
