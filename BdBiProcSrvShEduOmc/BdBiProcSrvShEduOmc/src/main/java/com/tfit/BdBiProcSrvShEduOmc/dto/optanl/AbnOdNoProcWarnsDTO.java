package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

/**
 * 3.2.4.	逾期未处理预警列表模型
 * @author Administrator
 *
 */
@Data
public class AbnOdNoProcWarnsDTO {
	String time;
	PageInfo pageInfo;
	List<AbnOdNoProcWarns> abnOdNoProcWarns;
	Long msgId;
}
