package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.Set;

import lombok.Data;

@Data
public class SearchDateDishList {
	//时间（1-14）
	String date;
	//星期（例：星期一）
	String week;
	//是否供餐，0:否，1:是
	Integer mealFlag = 0;
	//菜品集合
	Set<String> dishList;
}
