package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据供应商证照列表应用模型
public class BdSupLicsAppMod {
	private static final Logger logger = LogManager.getLogger(BdSupLicsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化	
	String[] supplierName_Array = {"河南双汇", "上海清美"};
	String[] province_Array = {"河南漯河", "上海浦东"};
	String[] compType_Array = {"生产型", "生产型"};
	String[] fblNo_Array = {"JY33101050000548", "JY93101050000548"};
	String[] fblLicStartDate_Array = {"2015/12/29", "2015/12/29"};
	String[] fblLicExpireDate_Array = {"2020/12/28", "2020/12/28"};
	String[] fblExpireDate_Array = {"有效", "有效"};
	String[] blNo_Array = {"4165464815984945", "5165464815984945"};
	String[] blLicStartDate_Array = {"", ""};
	String[] blLicExpireDate_Array = {"", ""};
	String[] blExpireDate_Array = {"", ""};
	String[] fplNo_Array = {"", ""};
	String[] fplLicStartDate_Array = {"", ""};
	String[] fplLicExpireDate_Array = {"", ""};
	String[] fplExpireDate_Array = {"有效", "有效"};	
	
	//模拟数据函数
	private BdSupLicsDTO SimuDataFunc() {
		BdSupLicsDTO bsuliDto = new BdSupLicsDTO();
		//时戳
		bsuliDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据供应商证照列表模拟数据
		List<BdSupLics> bdDishList = new ArrayList<>();
		//赋值
		for (int i = 0; i < supplierName_Array.length; i++) {
			BdSupLics bsuli = new BdSupLics();
			bsuli.setSupplierName(supplierName_Array[i]);
			bsuli.setProvince(province_Array[i]);
			bsuli.setCompType(compType_Array[i]);
			bsuli.setFblNo(fblNo_Array[i]);
			bsuli.setFblLicStartDate(fblLicStartDate_Array[i]);
			bsuli.setFblLicExpireDate(fblLicExpireDate_Array[i]);
			bsuli.setFblExpireDate(fblExpireDate_Array[i]);
			bsuli.setBlNo(blNo_Array[i]);
			bsuli.setBlLicStartDate(blLicStartDate_Array[i]);
			bsuli.setBlLicExpireDate(blLicExpireDate_Array[i]);
			bsuli.setBlExpireDate(blExpireDate_Array[i]);
			bsuli.setFplNo(fplNo_Array[i]);
			bsuli.setFplLicStartDate(fplLicStartDate_Array[i]);
			bsuli.setFplLicExpireDate(fplLicExpireDate_Array[i]);
			bsuli.setFplExpireDate(fplExpireDate_Array[i]);
			bdDishList.add(bsuli);
		}
		//设置数据
		bsuliDto.setBdSupLics(bdDishList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = supplierName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bsuliDto.setPageInfo(pageInfo);
		//消息ID
		bsuliDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bsuliDto;
	}
	
	// 基础数据供应商证照列表模型函数
	public BdSupLicsDTO appModFunc(String supplierName, String distName, String prefCity, String province, String contact, String mobilePhone, String fblNo, String blNo, String regCapital, String page, String pageSize, Db1Service db1Service) {
		BdSupLicsDTO bsuliDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			bsuliDto = SimuDataFunc();
		}		

		return bsuliDto;
	}
}
