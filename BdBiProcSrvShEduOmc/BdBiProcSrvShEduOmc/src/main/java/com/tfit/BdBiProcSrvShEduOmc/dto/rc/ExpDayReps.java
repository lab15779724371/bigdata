package com.tfit.BdBiProcSrvShEduOmc.dto.rc;
import lombok.Data;

@Data
public class ExpDayReps {
	//美菜网等
	String repType;
	//导出报表编号
	String repId;
	//区域名称
	String distName;
	//地级城市
	String prefCity;
	//省或直辖市
	String province;
	//导出文件URL
	String expFileUrl;
}
