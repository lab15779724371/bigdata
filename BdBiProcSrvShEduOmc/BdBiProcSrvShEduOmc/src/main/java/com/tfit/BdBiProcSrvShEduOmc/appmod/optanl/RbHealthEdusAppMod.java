package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbHealthEdus;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbHealthEdusDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//发布通报健康宣教列表应用模型
public class RbHealthEdusAppMod {
	private static final Logger logger = LogManager.getLogger(RbHealthEdusAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] bulletinId_Array = {"256510ab-a428-46b4-b3a7-6388191e1ffc20180321", "366510ab-a428-46b4-b3a7-6388191e1ffc20180321"};
	String[] title_Array = {"学生营养午餐怎么健康又美味", "学生营养午餐怎么进行菜谱搭配"};
	String[] pubOrg_Array = {"上海市教委", "上海市教委"};
	String[] pubDate_Array = {"2018-08-20", "2018-08-22"};
	
	//模拟数据函数
	private RbHealthEdusDTO SimuDataFunc() {
		RbHealthEdusDTO rheDto = new RbHealthEdusDTO();
		//时戳
		rheDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//发布通报健康宣教列表模拟数据
		List<RbHealthEdus> rbBulletins = new ArrayList<>();
		//赋值
		for (int i = 0; i < title_Array.length; i++) {
			RbHealthEdus rhe = new RbHealthEdus();
			rhe.setBulletinId(bulletinId_Array[i]);
			rhe.setTitle(title_Array[i]);
			rhe.setPubOrg(pubOrg_Array[i]);
			rhe.setPubDate(pubDate_Array[i]);
			rbBulletins.add(rhe);
		}
		//设置数据
		rheDto.setRbHealthEdus(rbBulletins);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = title_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		rheDto.setPageInfo(pageInfo);
		//消息ID
		rheDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rheDto;
	}
	
	// 发布通报健康宣教列表模型函数
	public RbHealthEdusDTO appModFunc(String startPubDate, String endPubDate, String title, String distName, String prefCity, String province, Db1Service db1Service) {
		RbHealthEdusDTO rheDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			rheDto = SimuDataFunc();
		}		

		return rheDto;
	}
}
