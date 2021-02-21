package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class WarnMaterialOverProDetsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnMaterialOverProDets> warnMaterialOverProDets;
	long msgId;
	
}
