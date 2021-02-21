package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class ExpCommonDTO {
	String startDate;
	String endDate;
	String ppName;
	String distName;
	String prefCity;
	String province;
	Integer sendFlag;
	String expFileUrl;
}
