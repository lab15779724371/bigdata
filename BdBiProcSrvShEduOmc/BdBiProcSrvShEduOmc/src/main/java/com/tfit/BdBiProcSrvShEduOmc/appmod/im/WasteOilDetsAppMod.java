package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOilDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOilDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//废弃油脂详情列表应用模型
public class WasteOilDetsAppMod {
	private static final Logger logger = LogManager.getLogger(WasteOilDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化	
	String[] recDate_Array = {"2018/09/03", "2018/09/03", "2018/09/03"};
	String[] distName_Array = {"11", "11", "11"};
	int[] woType_Array = {0, 0, 1};
	String[] rmcName_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司", "上海市民办盛大花园小学"};
	String[] recNum_Array = {"50 公斤", "80 公斤", "30 公斤"};
	String[] recComany_Array = {"上海市环兴环境资源利用有限公司", "上海市环兴环境资源利用有限公司", "上海市环兴环境资源利用有限公司"};
	String[] recPerson_Array = {"张山", "张山", "张山"};
	int[] recBillNum_Array = {1, 2, 1};
	
	//模拟数据函数
	private WasteOilDetsDTO SimuDataFunc() {
		WasteOilDetsDTO wodDto = new WasteOilDetsDTO();
		//设置返回数据
		wodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<WasteOilDets> wasteOilDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < recDate_Array.length; i++) {
			WasteOilDets wod = new WasteOilDets();
			wod.setRecDate(recDate_Array[i]);
			wod.setDistName(distName_Array[i]);
			wod.setWoType(woType_Array[i]);
			wod.setRmcName(rmcName_Array[i]);
			wod.setRecNum(recNum_Array[i]);
			wod.setRecComany(recComany_Array[i]);
			wod.setRecPerson(recPerson_Array[i]);
			wod.setRecBillNum(recBillNum_Array[i]);
			wasteOilDets.add(wod);
		}
		//设置数据
		wodDto.setWasteOilDets(wasteOilDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = recDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		wodDto.setPageInfo(pageInfo);
		//消息ID
		wodDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return wodDto;
	}

	//废弃油脂详情列表模型函数
	public WasteOilDetsDTO appModFunc(String woType, String rmcName, String recComany, String recPerson, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service) {
		WasteOilDetsDTO wodDto = null;
		if (isRealData) { // 真实数据
			
		} else { // 模拟数据
			//模拟数据函数
			wodDto = SimuDataFunc();
		}

		return wodDto;
	}
}
