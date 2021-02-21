package com.tfit.BdBiProcSrvShEduOmc.model.vo;

import lombok.Data;

/**
 * @Descritpion：回收废弃油脂汇总模型
 * @author: tianfang_infotech
 * @date: 2019/1/3 11:15
 */
@Data
public class RecoveryWasteOilSummaryVO {

    /**
     * 回收日期
     */
    private String recDate;

    /**
     * 主管部门
     */
    private String compDep;

    /**
     * 区域名称
     */
    private String distName;

    /**
     * 回收次数
     */
    private Integer reFeq;

    /**
     * 回收数量
     */
    private Float rcNum;

	public String getRecDate() {
		return recDate;
	}

	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}

	public String getCompDep() {
		return compDep;
	}

	public void setCompDep(String compDep) {
		this.compDep = compDep;
	}

	public String getDistName() {
		return distName;
	}

	public void setDistName(String distName) {
		this.distName = distName;
	}

	public Integer getReFeq() {
		return reFeq;
	}

	public void setReFeq(Integer reFeq) {
		this.reFeq = reFeq;
	}

	public Float getRcNum() {
		return rcNum;
	}

	public void setRcNum(Float rcNum) {
		this.rcNum = rcNum;
	}
}
