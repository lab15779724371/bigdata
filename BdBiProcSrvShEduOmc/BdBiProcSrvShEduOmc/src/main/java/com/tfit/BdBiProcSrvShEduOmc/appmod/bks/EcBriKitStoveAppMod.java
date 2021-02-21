package com.tfit.BdBiProcSrvShEduOmc.appmod.bks;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.EcBriKitStove;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.EcBriKitStoveDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//应急指挥明厨亮灶应用模型
public class EcBriKitStoveAppMod {
	private static final Logger logger = LogManager.getLogger(EcBriKitStoveAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化
	String[] ppName_Array = {"上海市天山中学（食堂）", "上海市天山中学（食堂）", "上海市紫薇幼儿园（食堂）", "上海市紫薇幼儿园（食堂）", "上师大附属小学（食堂）", "上师大附属小学（食堂）"};
	String[] camId_Array = {"1", "2", "3", "4", "5", "6"};
	String[] vidSrcName_Array = {"上海市天山中学（食堂）-厨房 01", "上海市天山中学（食堂）-厨房 02", "上海市紫薇幼儿园（食堂）-厨房 01", "上海市紫薇幼儿园（食堂）-厨房 02", "上师大附属小学（食堂）-厨房 01", "上师大附属小学（食堂）-厨房 02"};
	String[] vidUrl_Array = {"/ecBriKitStove/1.mp4", "/ecBriKitStove/2.mp4", "/ecBriKitStove/3.mp4", "/ecBriKitStove/4.mp4", "/ecBriKitStove/5.mp4", "/ecBriKitStove/6.mp4"};

	//模拟数据函数
	private EcBriKitStoveDTO SimuDataFunc() {
		EcBriKitStoveDTO ebksDto = new EcBriKitStoveDTO();
		//时戳
		ebksDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//应急指挥明厨亮灶模拟数据
		List<EcBriKitStove> ecBriKitStove = new ArrayList<>();
		//赋值
		for (int i = 0; i < ppName_Array.length; i++) {
			EcBriKitStove ebks = new EcBriKitStove();
			ebks.setPpName(ppName_Array[i]);
			ebks.setCamId(camId_Array[i]);
			ebks.setVidSrcName(vidSrcName_Array[i]);
			ebks.setVidUrl(SpringConfig.video_srvdn + vidUrl_Array[i]);
			ecBriKitStove.add(ebks);
		}
		//设置数据
		ebksDto.setEcBriKitStove(ecBriKitStove);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = ppName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		ebksDto.setPageInfo(pageInfo);
		//消息ID
		ebksDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ebksDto;
	}
	
	// 应急指挥明厨亮灶模型函数
	public EcBriKitStoveDTO appModFunc(String ppName, String distName, String prefCity, String province, Db1Service db1Service) {
		EcBriKitStoveDTO ebksDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			ebksDto = SimuDataFunc();
		}		

		return ebksDto;
	}
}
