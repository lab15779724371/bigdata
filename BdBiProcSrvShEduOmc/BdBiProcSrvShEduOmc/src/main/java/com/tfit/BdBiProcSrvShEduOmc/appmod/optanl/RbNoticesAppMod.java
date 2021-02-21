package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbNotices;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbNoticesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//发布通报通知列表应用模型
public class RbNoticesAppMod {
	private static final Logger logger = LogManager.getLogger(RbNoticesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;	
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化	
	String[] bulletinId_Array = {"256510ab-a428-46b4-b3a7-6388191e1ffc20180321", "366510ab-a428-46b4-b3a7-6388191e1ffc20180321"};
	String[] title_Array = {"系统操作培训的通知", "系统深入培训的通知"};
	String[] sendee_Array = {"徐汇区教育局,闵行区教育局,普陀区教育局", "徐汇区教育局,闵行区教育局,普陀区教育局"};
	String[] pubOrg_Array = {"上海市教委", "上海市教委"};
	String[] pubDate_Array = {"2018-08-20", "2018-08-22"};
	
	//模拟数据函数
	private RbNoticesDTO SimuDataFunc() {
		RbNoticesDTO rnsDto = new RbNoticesDTO();
		//时戳
		rnsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//发布通报通知列表模拟数据
		List<RbNotices> rbBulletins = new ArrayList<>();
		//赋值
		for (int i = 0; i < title_Array.length; i++) {
			RbNotices rns = new RbNotices();
			rns.setBulletinId(bulletinId_Array[i]);
			rns.setTitle(title_Array[i]);
			rns.setTitle(sendee_Array[i]);
			rns.setPubOrg(pubOrg_Array[i]);
			rns.setPubDate(pubDate_Array[i]);
			rbBulletins.add(rns);
		}
		//设置数据
		rnsDto.setRbNotices(rbBulletins);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = bulletinId_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		rnsDto.setPageInfo(pageInfo);
		//消息ID
		rnsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rnsDto;
	}
	
	// 发布通报通知列表模型函数
	public RbNoticesDTO appModFunc(String startPubDate, String endPubDate, String title, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service) {
		RbNoticesDTO rnsDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			rnsDto = SimuDataFunc();
		}		

		return rnsDto;
	}
}
