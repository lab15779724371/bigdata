package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduMenuGroupDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MenuNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//菜单名称编码列表应用模型
public class MenuNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(RecUnitCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"国内班", "国际班"};
	String[] code_Array = {"0", "1"};
		
	//模拟数据函数
	private MenuNameCodesDTO SimuDataFunc() {
		MenuNameCodesDTO mncDto = new MenuNameCodesDTO();
		//时戳
		mncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//菜单名称编码列表模拟数据
		List<NameCode> menuNameCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode mnc = new NameCode();
			mnc.setName(name_Array[i]);
			mnc.setCode(code_Array[i]);
			menuNameCodes.add(mnc);
		}
		//设置数据
		mncDto.setMenuNameCodes(menuNameCodes);
		//消息ID
		mncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mncDto;
	}
	
	// 菜单名称编码列表模型函数
	public MenuNameCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, SaasService saasService) {
		MenuNameCodesDTO mncDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			List<TEduMenuGroupDo> temgDoList = saasService.getAllMenuGroupIdName();
			if(temgDoList != null) {
				mncDto = new MenuNameCodesDTO();
				//时戳
				mncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//菜单名称列表模拟数据
				List<NameCode> menuNameCodes = new ArrayList<>();
				//赋值
				Map<String, String> menuNameToIdMap = new HashMap<>();
				for (int i = 0; i < temgDoList.size(); i++) {
					menuNameToIdMap.put(temgDoList.get(i).getMenuGroupName(), String.valueOf(i+1));
				}
				for(String curKey : menuNameToIdMap.keySet()) {
					NameCode ruc = new NameCode();
					ruc.setName(curKey);
					ruc.setCode(curKey);
					menuNameCodes.add(ruc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(menuNameCodes, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				mncDto.setPageInfo(pageInfo);
				//设置数据
				mncDto.setMenuNameCodes(pageBean.getCurPageData());
				//消息ID
				mncDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取菜单组数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			mncDto = SimuDataFunc();
		}		

		return mncDto;
	}
}
