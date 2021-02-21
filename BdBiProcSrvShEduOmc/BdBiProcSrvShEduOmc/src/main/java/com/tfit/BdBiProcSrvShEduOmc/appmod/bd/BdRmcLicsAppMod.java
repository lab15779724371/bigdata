package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据团餐公司证照列表应用模型
public class BdRmcLicsAppMod {
	private static final Logger logger = LogManager.getLogger(BdRmcLicsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化	
	String[] compName_Array = {"上海绿捷", "上海龙神"};
	String[] distName_Array = {"松江区", "长宁区"};
	String[] compType_Array = {"团餐公司", "团餐公司"};
	String[] fblNo_Array = {"JY33101050000548", "JY83101050000548"};
	String[] fblLicOrgan_Array = {"上海市长宁区市场监督管理局", "上海市长宁区市场监督管理局"};
	String[] fblLicStartDate_Array = {"2015/12/29", "2015/12/29"};
	String[] fblLicExpireDate_Array = {"2020/12/28", "2020/12/28"};
	String[] fblExpireDate_Array = {"有效", "有效"};
	String[] blNo_Array = {"4165464815984945", "5165464815984945"};
	int[] regCapital_Array = {300, 300};
	String[] blLicOrgan_Array = {"", ""};
	String[] blLicStartDate_Array = {"", ""};
	String[] blLicExpireDate_Array = {"", ""};
	String[] blExpireDate_Array = {"有效", "有效"};
	
	//模拟数据函数
	private BdRmcLicsDTO SimuDataFunc() {
		BdRmcLicsDTO brmlDto = new BdRmcLicsDTO();
		//时戳
		brmlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据团餐公司证照列表模拟数据
		List<BdRmcLics> bdDishList = new ArrayList<>();
		//赋值
		for (int i = 0; i < compName_Array.length; i++) {
			BdRmcLics brml = new BdRmcLics();
			brml.setCompName(compName_Array[i]);
			brml.setDistName(distName_Array[i]);
			brml.setCompType(compType_Array[i]);
			brml.setFblNo(fblNo_Array[i]);
			brml.setFblLicOrgan(fblLicOrgan_Array[i]);
			brml.setFblLicStartDate(fblLicStartDate_Array[i]);
			brml.setFblLicExpireDate(fblLicExpireDate_Array[i]);
			brml.setFblExpireDate(fblExpireDate_Array[i]);
			brml.setBlNo(blNo_Array[i]);
			brml.setRegCapital(regCapital_Array[i]);
			brml.setBlLicOrgan(blLicOrgan_Array[i]);
			brml.setBlLicStartDate(blLicStartDate_Array[i]);
			brml.setBlLicExpireDate(blLicExpireDate_Array[i]);
			brml.setBlExpireDate(blExpireDate_Array[i]);
			bdDishList.add(brml);
		}
		//设置数据
		brmlDto.setBdRmcLics(bdDishList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = compName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		brmlDto.setPageInfo(pageInfo);
		//消息ID
		brmlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return brmlDto;
	}
	
	// 基础数据团餐公司证照列表模型函数
	public BdRmcLicsDTO appModFunc(String compName, String distName, String prefCity, String province, String contact, String mobilePhone, String fblNo, String blNo, String regCapital, String page, String pageSize, Db1Service db1Service) {
		BdRmcLicsDTO brmlDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			brmlDto = SimuDataFunc();
		}		

		return brmlDto;
	}
}
