package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import lombok.Data;

@Data
public class SearchDish {
	//项目点名称
	String schName;
	//团餐公司名称
	String rmcName;
	//菜单名称
	String menuName;
	//餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
	String caterType;
	
	//菜品集合（不同时间区分）
	List<SearchDateDishList> dateDiashList;
	//是否排菜，0:未排菜，1:已排菜(按学校)
	//Integer dishFlag = 0;
}
