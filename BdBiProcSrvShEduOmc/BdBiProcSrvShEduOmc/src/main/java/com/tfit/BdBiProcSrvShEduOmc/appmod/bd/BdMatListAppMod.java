package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdMatList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdMatListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据原料列表
public class BdMatListAppMod {
	private static final Logger logger = LogManager.getLogger(BdMatListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] rmcName_Array = {"上海绿捷", "上海神龙"};
	String[] matName_Array = {"土鸡蛋", "土鸡蛋"};
	String[] standardName_Array = {"鸡蛋", "鸡蛋"};
	String[] matCategory_Array = {"蛋类产品", "蛋类产品"}; 
	String[] spec_Array = {"个", "个"};
	String[] basicUnit_Array = {"公斤", "公斤"};
	String[] actualUnit_Array = {"斤", "斤"};
	String[] distrAttr_Array = {"统配", "统配"};
	String[] shelfLife_Array = {"30 天", "30 天"};
	String[] remarks_Array = {"", "30 天"};
	int[] matClassify_Array = {1, 1};
	float[] netMatRate_Array = {(float) 100.00, (float) 100.00};
	
	//模拟数据函数
	private BdMatListDTO SimuDataFunc() {
		BdMatListDTO bmlDto = new BdMatListDTO();
		//时戳
		bmlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据原料列表模拟数据
		List<BdMatList> bdMatList = new ArrayList<>();
		//赋值
		for (int i = 0; i < rmcName_Array.length; i++) {
			BdMatList bml = new BdMatList();
			bml.setRmcName(rmcName_Array[i]);
			bml.setMatName(matName_Array[i]);
			bml.setStandardName(standardName_Array[i]);
			bml.setMatCategory(matCategory_Array[i]);
			bml.setMatClassify(matClassify_Array[i]);
			bml.setSpec(spec_Array[i]);
			bml.setBasicUnit(basicUnit_Array[i]);
			bml.setActualUnit(actualUnit_Array[i]);
			bml.setNetMatRate(netMatRate_Array[i]);
			bml.setDistrAttr(distrAttr_Array[i]);
			bml.setShelfLife(shelfLife_Array[i]);
			bml.setRemarks(remarks_Array[i]);
			bdMatList.add(bml);
		}
		//设置数据
		bmlDto.setBdMatList(bdMatList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = rmcName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bmlDto.setPageInfo(pageInfo);
		//消息ID
		bmlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bmlDto;
	}
	
	// 基础数据原料列表模型函数
	public BdMatListDTO appModFunc(String rmcName, String matName, String standardName, String matCategory, int matClassify, String distName, String prefCity, String province, Db1Service db1Service) {
		BdMatListDTO bmlDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			bmlDto = SimuDataFunc();
		}		

		return bmlDto;
	}
}
