package com.tfit.BdBiProcSrvShEduOmc.model.vo.base;

import lombok.Data;

/**
 * @Descritpion：文件导出模型
 * @author: tianfang_infotech
 * @date: 2019/1/17 11:37
 */
@Data
public class FileExport {
    /**
     * excel下载地址(url)
     */
    private String expFileUrl;

	public String getExpFileUrl() {
		return expFileUrl;
	}

	public void setExpFileUrl(String expFileUrl) {
		this.expFileUrl = expFileUrl;
	}
}
