package com.tfit.BdBiProcSrvShEduOmc.dto.info.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfit.BdBiProcSrvShEduOmc.dto.base.PagedDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Descritpion：团餐公司回收废弃油脂检索模型
 * @author: tianfang_infotech
 * @date: 2019/1/3 10:11
 */
@Data
public class RmcRecoveryWasteOilSearchDTO extends PagedDTO {

    /**
     * 搜索类型(1:学校,2:团餐公司)
     */
    private Integer searchType;

    /**
     * 回收开始日期
     */
    @DateTimeFormat(pattern  = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startSubDate;

    /**
     * 回收结束日期
     */
    @DateTimeFormat(pattern  = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endSubDate;

    /**
     * 废弃油脂种类(0:未知,1:废水;2:含油废水)
     */
    private Integer woType;

    /**
     * 区域名称
     */
    private Integer distName;

    /**
     * 地级城市
     */
    private String prefCity;

    /**
     * 省或直辖市
     */
    private String province;

    /**
     * 团餐公司Id
     */
    private String rmcName;

    /**
     * 回收单位
     */
    private String recCompany;

    /**
     * 回收人
     */
    private String recPerson;
    
    /**
     * 区域编号集合 格式：["1","2"]
     */
    private String distNames;

	public Integer getSearchType() {
		return searchType;
	}

	public void setSearchType(Integer searchType) {
		this.searchType = searchType;
	}

	public Date getStartSubDate() {
		return startSubDate;
	}

	public void setStartSubDate(Date startSubDate) {
		this.startSubDate = startSubDate;
	}

	public Date getEndSubDate() {
		return endSubDate;
	}

	public void setEndSubDate(Date endSubDate) {
		this.endSubDate = endSubDate;
	}

	public Integer getWoType() {
		return woType;
	}

	public void setWoType(Integer woType) {
		this.woType = woType;
	}

	public Integer getDistName() {
		return distName;
	}

	public void setDistName(Integer distName) {
		this.distName = distName;
	}

	public String getPrefCity() {
		return prefCity;
	}

	public void setPrefCity(String prefCity) {
		this.prefCity = prefCity;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getRmcName() {
		return rmcName;
	}

	public void setRmcName(String rmcName) {
		this.rmcName = rmcName;
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
	
}
