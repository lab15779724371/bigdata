package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import lombok.Data;

/**
 * 3.2.2.	业务明细数据信息模型
 * @author Administrator
 *
 */
@Data
public class BusiDetDataInfoDTO {
	String time;
	List<BusiDetDataInfo> busiDetDataInfo;
	Long msgId;
}
