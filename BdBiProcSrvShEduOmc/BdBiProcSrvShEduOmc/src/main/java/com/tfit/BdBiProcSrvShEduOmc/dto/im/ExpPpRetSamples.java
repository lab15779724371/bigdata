package com.tfit.BdBiProcSrvShEduOmc.dto.im;
import lombok.Data;

@Data
public class ExpPpRetSamples {
	//开始日期，默认为当天日期，格式：xxxx-xx-xx
	String startDate;
	//结束日期，默认为当天日期，格式：xxxx-xx-xx
	String endDate;
	//项目点名称
	String ppName;
	//区域名称
	String distName;
	//地级城市
	String prefCity;
	//省或直辖市
	String province;
	//是否留样标识
	String rsFlag;
	//学校类型（学制）
	String schType;
	//导出文件URL
	String expFileUrl;
}
