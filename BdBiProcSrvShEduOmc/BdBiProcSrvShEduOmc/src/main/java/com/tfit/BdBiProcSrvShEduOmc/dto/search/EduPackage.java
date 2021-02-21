package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import lombok.Data;

/**
 * 项目点供餐业务明细
 * @author Administrator
 *
 */
@Data
public class EduPackage {
	//学校编号
	String schoolId;
	//供餐时间
	String useDate;
	//0 不供餐  1供餐
	Integer haveClass;
}
