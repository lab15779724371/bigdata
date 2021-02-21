package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.Announces;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.AnnouncesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//通告列表应用模型
public class AnnouncesAppMod {
	private static final Logger logger = LogManager.getLogger(AnnouncesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化
	String[] annId_Array = {"21175c6e-da9d-498b-9a70-d5a4b8cc1552", "32275c6e-da9d-498b-9a70-d5a4b8cc1552", "43375c6e-da9d-498b-9a70-d5a4b8cc1552", "54475c6e-da9d-498b-9a70-d5a4b8cc1552", "65575c6e-da9d-498b-9a70-d5a4b8cc1552", "76675c6e-da9d-498b-9a70-d5a4b8cc1552", "87775c6e-da9d-498b-9a70-d5a4b8cc1552"};
	String[] announcName_Array = {"上海市食品药品监督管理局关于不合格食品风险控制情况的通告（2018年第20期）", "本市2018年第11轮网络餐饮服务监测结果", "关于杨浦中学食堂检查结果通知报告", "学生营养午餐怎么健康又美味", "国外中小学生校餐大观", "7-9岁学生营养摄入建议参考", "阳光午餐系统第2次集中培训安排通知"};
	String[] orgName_Array = {"上海市教委", "上海市教委", "上海市教委", "上海市教委", "上海市教委", "上海市教委", "上海市教委"};
	String[] announcDate_Array = {"2018/09/05", "2018/09/05", "2018/09/05", "2018/09/05", "2018/09/05", "2018/09/04", "2018/09/03"};
	
	//模拟数据函数
	private AnnouncesDTO SimuDataFunc() {
		AnnouncesDTO ansDto = new AnnouncesDTO();
		//时戳
		ansDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//通告列表模拟数据
		List<Announces> announcs = new ArrayList<>();
		//赋值
		for (int i = 0; i < announcName_Array.length; i++) {
			Announces ans = new Announces();
			ans.setAnnId(annId_Array[i]);
			ans.setAnnouncName(announcName_Array[i]);
			ans.setOrgName(orgName_Array[i]);
			ans.setAnnouncDate(announcDate_Array[i]);
			announcs.add(ans);
		}
		//设置数据
		ansDto.setAnnounces(announcs);
		//消息ID
		ansDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ansDto;
	}
	
	// 通告列表模型函数
	public AnnouncesDTO appModFunc(String distName, String prefCity, String province, int timeType, String date, Db1Service db1Service) {
		AnnouncesDTO ansDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			ansDto = SimuDataFunc();
		}		

		return ansDto;
	}
}
