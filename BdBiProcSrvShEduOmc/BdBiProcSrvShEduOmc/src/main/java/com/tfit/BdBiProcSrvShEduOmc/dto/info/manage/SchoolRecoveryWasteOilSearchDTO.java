package com.tfit.BdBiProcSrvShEduOmc.dto.info.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfit.BdBiProcSrvShEduOmc.dto.base.PagedDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Descritpion：回收废弃油脂列表检索模型
 * @author: tianfang_infotech
 * @date: 2019/1/3 10:11
 */
@Data
public class SchoolRecoveryWasteOilSearchDTO extends PagedDTO {

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
     * 项目点名称(学校)
     */
    private String ppName;

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
     * 学校类型（学制）：0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，
     *     10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
     */
    private Integer schType;

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
     * 所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
     */
    private String subLevel;
    
    /**
     * 主管部门，按主管部门有效
     */
    private String compDep;
    
    /**
     * 学校性质，0:公办，1:民办，2:其他
     */
    private Integer schProp;
    
    /**
     * 区域编号集合 格式：["1","2"]
     */
    private String distNames;
    
    /**
     * 所属集合 格式：["1","2"]
     */
    private String subLevels;
    
    /**
     * 主管部门集合 格式：["1","2"]
     */
    private String compDeps;
    
    /**
     * 学校性质集合 格式：["1","2"]
     */
    private String schProps;
    
    /**
     * 学校类型（学制），集合 格式：["1","2"]
     */
    private String schTypes;

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

	public String getPpName() {
		return ppName;
	}

	public void setPpName(String ppName) {
		this.ppName = ppName;
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

	public Integer getSchType() {
		return schType;
	}

	public void setSchType(Integer schType) {
		this.schType = schType;
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
