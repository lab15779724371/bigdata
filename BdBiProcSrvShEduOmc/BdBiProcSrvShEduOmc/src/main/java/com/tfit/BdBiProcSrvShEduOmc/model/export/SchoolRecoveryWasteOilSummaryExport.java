package com.tfit.BdBiProcSrvShEduOmc.model.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.FileExport;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：回收废弃油脂统计导出模型
 * @author: tianfang_infotech
 * @date: 2019/1/17 14:46
 */
@Data
public class SchoolRecoveryWasteOilSummaryExport extends FileExport {
    /**
     * 回收开始日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startSubDate;

    /**
     * 回收结束日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endSubDate;

    /**
     * 学校筛选方式，0:按主管部门，1:按所在地
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
