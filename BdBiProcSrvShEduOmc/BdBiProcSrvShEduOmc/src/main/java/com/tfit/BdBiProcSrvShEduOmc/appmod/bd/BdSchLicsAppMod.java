package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据学校证照列表应用模型
public class BdSchLicsAppMod {
	private static final Logger logger = LogManager.getLogger(BdSchLicsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] compName_Array = {"上海市天山中学", "上海市徐汇康宁外国语中学"};
	String[] distName_Array = {"长宁区", "徐汇区"};
	String[] schType_Array = {"高中", "初中"};
	String[] fblNo_Array = {"JY33101050000548", "JY54101050000559"};
	String[] fblOptUnit_Array = {"上海市天山中学食堂", "上海市徐汇康宁外国语中学食堂"};
	String[] fblLicOrgan_Array = {"上海市长宁区市场监督管理局", "上海市徐汇区市场监督管理局"};
	String[] fblLicStartDate_Array = {"2015/12/29", "2015/12/29"};
	String[] fblLicExpireDate_Array = {"2020/12/28", "2020/12/28"};
	String[] fblExpireDate_Array = {"有效", "有效"};
	String[] schLicNo_Array = {"", ""};
	String[] slOptUnit_Array = {"", ""};
	String[] slLicOrgan_Array = {"", ""};
	String[] slLicExpireDate_Array = {"", ""};
	String[] slExpireDate_Array = {"有效", "有效"};
	
	//模拟数据函数
	private BdSchLicsDTO SimuDataFunc() {
		BdSchLicsDTO bsliDto = new BdSchLicsDTO();
		//时戳
		bsliDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据学校证照列表模拟数据
		List<BdSchLics> bdDishList = new ArrayList<>();
		//赋值
		for (int i = 0; i < compName_Array.length; i++) {
			BdSchLics bsli = new BdSchLics();
			bsli.setCompName(compName_Array[i]);
			bsli.setDistName(distName_Array[i]);
			bsli.setSchType(schType_Array[i]);
			bsli.setFblNo(fblNo_Array[i]);
			bsli.setFblOptUnit(fblOptUnit_Array[i]);
			bsli.setFblLicOrgan(fblLicOrgan_Array[i]);
			bsli.setFblLicStartDate(fblLicStartDate_Array[i]);
			bsli.setFblLicExpireDate(fblLicExpireDate_Array[i]);
			bsli.setFblExpireDate(fblExpireDate_Array[i]);
			bsli.setSchLicNo(schLicNo_Array[i]);
			bsli.setSlOptUnit(slOptUnit_Array[i]);
			bsli.setSlLicOrgan(slLicOrgan_Array[i]);
			bsli.setSlLicExpireDate(slLicExpireDate_Array[i]);
			bsli.setSlExpireDate(slExpireDate_Array[i]);
			bdDishList.add(bsli);
		}
		//设置数据
		bsliDto.setBdSchLics(bdDishList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = compName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bsliDto.setPageInfo(pageInfo);
		//消息ID
		bsliDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bsliDto;
	}
	
	// 基础数据学校证照列表模型函数
	public BdSchLicsDTO appModFunc(String compName, String distName, String prefCity, String province, String contact, String mobilePhone, String fblNo, String blNo, String regCapital, String page, String pageSize, Db1Service db1Service) {
		BdSchLicsDTO bsliDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			bsliDto = SimuDataFunc();
		}		

		return bsliDto;
	}
}
