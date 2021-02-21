package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProDishesDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.DishNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//菜品名称编码列表应用模型
public class DishNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(DishNameCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"西红柿炒蛋", "刀豆鸡丁", "豆腐肉丝汤"};
	String[] code_Array = {"西红柿炒蛋", "刀豆鸡丁", "豆腐肉丝汤"};
	
	//模拟数据函数
	private DishNameCodesDTO SimuDataFunc() {
		DishNameCodesDTO dncDto = new DishNameCodesDTO();
		//时戳
		dncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//菜品名称编码列表模拟数据
		List<NameCode> dishNameCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode dnc = new NameCode();
			dnc.setName(name_Array[i]);
			dnc.setCode(code_Array[i]);
			dishNameCodes.add(dnc);
		}
		//设置数据
		dncDto.setDishNameCodes(dishNameCodes);
		//消息ID
		dncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dncDto;
	}
	
	// 菜品名称编码列表模型函数
	public DishNameCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, SaasService saasService) {
		DishNameCodesDTO dncDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			List<TProDishesDo> tpdDoList = saasService.getAllDishNames();
			if(tpdDoList != null) {
				dncDto = new DishNameCodesDTO();
				//时戳
				dncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//菜品名称编码列表模拟数据
				List<NameCode> dishNameCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < tpdDoList.size(); i++) {
					NameCode dnc = new NameCode();
					dnc.setName(tpdDoList.get(i).getName());
					dnc.setCode(tpdDoList.get(i).getName());
					dishNameCodes.add(dnc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(dishNameCodes, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				dncDto.setPageInfo(pageInfo);
				//设置数据
				dncDto.setDishNameCodes(pageBean.getCurPageData());
				//消息ID
				dncDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取菜品名称数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			dncDto = SimuDataFunc();
		}		

		return dncDto;
	}
}
