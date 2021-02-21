package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import lombok.Data;

/**
 * 3.2.10.	学校排菜情况统计模型
 * @author Administrator
 *
 */
@Data
public class SchDishSitStatDTO {
	String time;
	List<SchDishSitStat> schDishSitStat;
	Long msgId;
}
