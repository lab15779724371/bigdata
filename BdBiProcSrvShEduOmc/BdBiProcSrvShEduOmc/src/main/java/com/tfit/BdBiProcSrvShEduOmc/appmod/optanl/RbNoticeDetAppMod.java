package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbNoticeDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbNoticeDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//发布通报通知详情应用模型
public class RbNoticeDetAppMod {
	private static final Logger logger = LogManager.getLogger(RbNoticeDetAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化	
	String bulletinId = "256510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String title = "阳光午餐系统第3次集中培训安排通知";
	String pubOrg = "上海市教委";
	String pubDate = "2018-08-20";
	int readNum = 58;
	String content = "各单位，各学校：兹定于2019年8月31日9:00在市教委大礼堂多功能厅举行“上海市阳光午餐追溯系统”操作第三次集中培训，请相关学校务必派遣相关人员参加（学校食堂负责人，食品安全负责人，系统操作人员等），内容重要，不得无故缺席。上海市教委2018-08-10";
	String lastBulletinId = "366510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String lastTitle = "阳光午餐系统第2次集中培训安排通知";
	String nextBulletinId = "496510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String nextTitle = "阳光午餐系统第1次集中培训安排通知";
	
	//模拟数据函数
	private RbNoticeDetDTO SimuDataFunc() {
		RbNoticeDetDTO rndDto = new RbNoticeDetDTO();
		//时戳
		rndDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//发布通报通知详情模拟数据
		RbNoticeDet rbNoticeDet = new RbNoticeDet();
		//赋值
		rbNoticeDet.setBulletinId(bulletinId);
		rbNoticeDet.setTitle(title);
		rbNoticeDet.setPubOrg(pubOrg);
		rbNoticeDet.setPubDate(pubDate);
		rbNoticeDet.setReadNum(readNum);
		rbNoticeDet.setContent(content);
		rbNoticeDet.setLastBulletinId(lastBulletinId);
		rbNoticeDet.setLastTitle(lastTitle);
		rbNoticeDet.setNextBulletinId(nextBulletinId);
		rbNoticeDet.setNextTitle(nextTitle);
		//设置数据
		rndDto.setRbNoticeDet(rbNoticeDet);
		//消息ID
		rndDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rndDto;
	}
	
	// 发布通报通知详情模型函数
	public RbNoticeDetDTO appModFunc(String bulletinId, String distName, String prefCity, String province, Db1Service db1Service) {
		RbNoticeDetDTO rndDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			rndDto = SimuDataFunc();
		}		

		return rndDto;
	}
}
