package com.tfit.BdBiProcSrvShEduOmc.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：学校回收废弃油脂详细列表
 * @author: tianfang_infotech
 * @date: 2019/1/7 16:11
 */
@Data
public class SchoolRecoveryWasteOilDetailVO {

    /**
     * 回收日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date recDate;

    /**
     * 区域名称
     */
    private String distName;

    /**
     * 项目点名称
     */
    private String ppName;

    /**
     * 总分校标识
     */
    private String schGenBraFlag;

    /**
     * 分校数量
     */
    private Integer braCampusNum;

    /**
     * 关联总校
     */
    private String relGenSchName;

    /**
     * 所属，0:其他，1:部属，2:市属，3: 区属
     */
    private String subLevel;

    /**
     * 主管部门
     */
    private String compDep;
    
    //所属区域名称
    private String subDistName;

    /**
     * 学校类型（学制）
     */
    private String schType;

    /**
     * 团餐公司名称
     */
    private String rmcName;
    
    //学校性质
    private String schProp;

    /**
     * 废弃油脂种类，0:废油，1:含油废水
     */
    private String woType;

    /**
     * 回收数量(改为String类型原因：数据库中存在带单位的数量)
     */
    private String recNum;

    /**
     * 回收单位
     */
    private String recCompany;

    /**
     * 回收人
     */
    private String recPerson;

    /**
     * 回收单据数
     */
    private Integer recBillNum;

	public Date getRecDate() {
		return recDate;
	}

	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}

	public String getDistName() {
		return distName;
	}

	public void setDistName(String distName) {
		this.distName = distName;
	}

	public String getPpName() {
		return ppName;
	}

	public void setPpName(String ppName) {
		this.ppName = ppName;
	}

	public String getSchGenBraFlag() {
		return schGenBraFlag;
	}

	public void setSchGenBraFlag(String schGenBraFlag) {
		this.schGenBraFlag = schGenBraFlag;
	}

	public Integer getBraCampusNum() {
		return braCampusNum;
	}

	public void setBraCampusNum(Integer braCampusNum) {
		this.braCampusNum = braCampusNum;
	}

	public String getRelGenSchName() {
		return relGenSchName;
	}

	public void setRelGenSchName(String relGenSchName) {
		this.relGenSchName = relGenSchName;
	}

	public String getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(String subLevel) {
		this.subLevel = subLevel;
	}

	public String getCompDep() {
		return compDep;
	}

	public void setCompDep(String compDep) {
		this.compDep = compDep;
	}

	public String getSubDistName() {
		return subDistName;
	}

	public void setSubDistName(String subDistName) {
		this.subDistName = subDistName;
	}

	public String getSchType() {
		return schType;
	}

	public void setSchType(String schType) {
		this.schType = schType;
	}
	public String getSchProp() {
		return schProp;
	}

	public void setSchProp(String schProp) {
		this.schProp = schProp;
	}

	public String getRmcName() {
		return rmcName;
	}

	public void setRmcName(String rmcName) {
		this.rmcName = rmcName;
	}

	public String getWoType() {
		return woType;
	}

	public void setWoType(String woType) {
		this.woType = woType;
	}

	public String getRecNum() {
		return recNum;
	}

	public void setRecNum(String recNum) {
		this.recNum = recNum;
	}

	public String getRecCompany() {
		return recCompany;
	}

	public void setRecCompany(String recCompany) {
		this.recCompany = recCompany;
	}

	public String getRecPerson() {
		return recPerson;
	}

	public void setRecPerson(String recPerson) {
		this.recPerson = recPerson;
	}

	public Integer getRecBillNum() {
		return recBillNum;
	}

	public void setRecBillNum(Integer recBillNum) {
		this.recBillNum = recBillNum;
	}
}
