package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RsUnitCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//留样单位编码列表应用模型
public class RsUnitCodesAppMod {
	private static final Logger logger = LogManager.getLogger(RmcListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"上海市徐汇区向阳小学", "上海市徐汇区华泾小学"};
	String[] code_Array = {"4f5de487-8913-40c8-bcce-7a538be4ec40", "4f5de487-8913-40c8-bcce-7a538be4ec61"};
	
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
		
	//模拟数据函数
	private RsUnitCodesDTO SimuDataFunc() {
		RsUnitCodesDTO rucDto = new RsUnitCodesDTO();
		//时戳
		rucDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//留样单位编码列表模拟数据
		List<NameCode> rsUnitCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ruc = new NameCode();
			ruc.setName(name_Array[i]);
			ruc.setCode(code_Array[i]);
			rsUnitCodes.add(ruc);
		}
		//设置数据
		rucDto.setRsUnitCodes(rsUnitCodes);
		//消息ID
		rucDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rucDto;
	}
	
	// 留样单位编码列表模型函数
	public RsUnitCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, SaasService saasService) {
		RsUnitCodesDTO rucDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//所有学校id
			List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distName,1,1, 5);
			if(tesDoList != null) {
				int tesDoListLen = tesDoList.size();
				TEduSchoolDo tesDo = null;
				rucDto = new RsUnitCodesDTO();
				//时戳
				rucDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//留样单位列表模拟数据
				List<NameCode> rsUnitCodes = new ArrayList<>();
				//赋值并去重
				for(int i = 0; i < tesDoListLen; i++) {
					NameCode ruc = new NameCode();
					tesDo = tesDoList.get(i);
					ruc.setName(tesDo.getSchoolName());
					ruc.setCode(tesDo.getId());
					rsUnitCodes.add(ruc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rsUnitCodes, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				rucDto.setPageInfo(pageInfo);
				//设置数据
				rucDto.setRsUnitCodes(pageBean.getCurPageData());
				//消息ID
				rucDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取留样单位数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			rucDto = SimuDataFunc();
		}		

		return rucDto;
	}
}
