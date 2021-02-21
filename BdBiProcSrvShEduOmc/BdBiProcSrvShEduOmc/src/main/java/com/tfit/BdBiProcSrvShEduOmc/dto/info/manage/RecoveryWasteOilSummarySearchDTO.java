package com.tfit.BdBiProcSrvShEduOmc.dto.info.manage;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.tfit.BdBiProcSrvShEduOmc.dto.base.PagedDTO;

import lombok.Data;

/**
 * @Descritpion：回收废弃油脂摘要检索模型
 * @author: tianfang_infotech
 * @date: 2019/1/3 10:11
 */
@Data
public class RecoveryWasteOilSummarySearchDTO extends PagedDTO {

    /**
     * 搜索类型(1:学校,2:团餐公司)
     */
    private Integer searchType;

    /**
     * 回收开始日期
     */
    @DateTimeFormat(pattern  = "yyyy-MM-dd")
    private Date startSubDate;

    /**
     * 回收结束日期
     */
    @DateTimeFormat(pattern  = "yyyy-MM-dd")
    private Date endSubDate;

    /**
     * 学校筛选方式，0:按主管部门，1:按所在地（暂不启用）
     */
    private Integer schSelModel;

    /**
     * 所属，0:其他，1:部属，2:市属，3: 区属
     */
    private String subLevel;

    /**
     * 主管部门
     */
    private String compDep;

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
    
    /**
     * 省集合
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

	public Integer getSchSelModel() {
		return schSelModel;
	}

	public void setSchSelModel(Integer schSelModel) {
		this.schSelModel = schSelModel;
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

	public String getDistName() {
		return distName;
	}

	public void setDistName(String distName) {
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
}
