package com.tfit.BdBiProcSrvShEduOmc.dto.info.manage;

import com.tfit.BdBiProcSrvShEduOmc.dto.base.AuthorizationDTO;
import lombok.Data;

/**
 * @Descritpion：废弃油脂种类编码列表
 * @author: tianfang_infotech
 * @date: 2019/1/7 15:11
 */
@Data
public class WasteOilTypeCodeSearchDTO extends AuthorizationDTO {

    /**
     * 区域名称
     */
    private String distName;

    /**
     * 地级城市
     */
    private String prefCity;

    /**
     * 省或直辖市
     */
    private String province;

	public String getDistName() {
		return distName;
	}

	public void setDistName(String distName) {
		this.distName = distName;
	}
}
