package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//学校列表应用模型
public class SchListAppMod {
	private static final Logger logger = LogManager.getLogger(SchListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] schName_Array = {"上海天山中学", "上海师范大学附属中学", "上海徐汇康宁外国语中学"};
	String[] schId_Array = {"21bca1b9-9a16-42ff-8c90-6186d287be2c", "32bca1b9-9a16-42ff-8c90-6186d287be2c", "43bca1b9-9a16-42ff-8c90-6186d287be2c"};	
	
	//模拟数据函数
	private SchListDTO SimuDataFunc() {
		SchListDTO sclDto = new SchListDTO();
		//时戳
		sclDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//学校列表模拟数据
		List<SchList> schList = new ArrayList<>();
		//赋值
		for (int i = 0; i < schName_Array.length; i++) {
			SchList scl = new SchList();
			scl.setSchName(schName_Array[i]);
			scl.setSchId(schId_Array[i]);
			schList.add(scl);
		}
		//设置数据
		sclDto.setSchList(schList);
		//分页信息
		PageInfo pageInfo = new PageInfo();
		pageTotal = schName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		sclDto.setPageInfo(pageInfo);
		//消息ID
		sclDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sclDto;
	}
	
	// 学校列表模型函数
	public SchListDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		SchListDTO sclDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			sclDto = SimuDataFunc();
		}		

		return sclDto;
	}
}
