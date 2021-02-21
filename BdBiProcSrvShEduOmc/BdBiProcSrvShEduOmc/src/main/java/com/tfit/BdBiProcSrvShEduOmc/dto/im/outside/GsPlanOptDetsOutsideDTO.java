package com.tfit.BdBiProcSrvShEduOmc.dto.im.outside;

import java.util.List;

import lombok.Data;

@Data
public class GsPlanOptDetsOutsideDTO {
	String time;
	List<GsPlanOptDetsOutside> gsPlanOptDets;
	long msgId;
	
}
