package com.tfit.BdBiProcSrvShEduOmc.dto.im.outside;

import lombok.Data;

@Data
public class PpDishDetsOutside {
	 //供餐时间
	  String dishDate;
	  //学校id
	  String schoolId;  
	  //操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	  String plaStatus;
}
