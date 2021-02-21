package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdDishList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据菜品列表应用模型
public class BdDishListAppMod {
	private static final Logger logger = LogManager.getLogger(BdDishListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] rmcName_Array = {"上海绿捷", "上海绿捷"};
	String[] dishName_Array = {"香菇炒青菜（南西分180416）", "香菇炒青菜（南西分180416）"};
	String[] dishColor_Array = {"无", "无"};
	String[] dishShape_Array = {"无", "无"};
	String[] dishTech_Array = {"无", "无"};
	String[] dishTaste_Array = {"无", "无"};
	String[] dishStyle_Array = {"无", "无"};
	String[] dishCategory_Array = {"素菜", "素菜"};
	String[] protein_Array = {"1.24 g", "1.24 g"};
	String[] fat_Array = {"0.24 g", "0.24 g"};
	String[] carbohydrate_Array = {"74.94 g", "74.94 g"};
	String[] energy_Array = {"14.13 kj", "14.13 kj"};
	String[] dietaryFiber_Array = {"0.00 g", "0.00 g"};
	
	//模拟数据函数
	private BdDishListDTO SimuDataFunc() {
		BdDishListDTO bdlDto = new BdDishListDTO();
		//时戳
		bdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据菜品列表模拟数据
		List<BdDishList> bdDishList = new ArrayList<>();
		//赋值
		for (int i = 0; i < rmcName_Array.length; i++) {
			BdDishList bdl = new BdDishList();
			bdl.setRmcName(rmcName_Array[i]);
			bdl.setDishName(dishName_Array[i]);
			bdl.setDishColor(dishColor_Array[i]);
			bdl.setDishShape(dishShape_Array[i]);
			bdl.setDishTech(dishTech_Array[i]);
			bdl.setDishTaste(dishTaste_Array[i]);
			bdl.setDishStyle(dishStyle_Array[i]);
			bdl.setDishCategory(dishCategory_Array[i]);
			bdl.setProtein(protein_Array[i]);
			bdl.setFat(fat_Array[i]);
			bdl.setCarbohydrate(carbohydrate_Array[i]);
			bdl.setEnergy(energy_Array[i]);
			bdl.setDietaryFiber(dietaryFiber_Array[i]);
			bdDishList.add(bdl);
		}
		//设置数据
		bdlDto.setBdDishList(bdDishList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = rmcName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bdlDto.setPageInfo(pageInfo);
		//消息ID
		bdlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bdlDto;
	}
	
	// 基础数据菜品列表模型函数
	public BdDishListDTO appModFunc(String schName, String dishName, String dishColor, String dishShape, String dishTech, String dishTaste, String dishStyle, String dishCategory, String distName, String prefCity, String province, Db1Service db1Service) {
		BdDishListDTO bdlDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			bdlDto = SimuDataFunc();
		}		

		return bdlDto;
	}
}
