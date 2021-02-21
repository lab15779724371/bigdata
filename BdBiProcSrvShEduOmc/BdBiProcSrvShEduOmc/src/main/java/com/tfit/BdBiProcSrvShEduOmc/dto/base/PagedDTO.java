package com.tfit.BdBiProcSrvShEduOmc.dto.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Descritpion：分页模型
 * @author: tianfang_infotech
 * @date: 2019/1/3 10:28
 */
@Data
public class PagedDTO extends AuthorizationDTO {

    public PagedDTO() {
        this.page = 1;
        this.pageSize = 20;
    }

    /**
     * 页号(默认：1)
     */
    @NotNull(message = "page is required")
    private Integer page;

    /**
     * 页大小(默认：20)
     */
    @NotNull(message = "pageSize is required")
    private Integer pageSize;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
