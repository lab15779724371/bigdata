package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProRecyclerSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RecUnitCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//回收单位编码列表应用模型
public class RecUnitCodesAppMod {
	private static final Logger logger = LogManager.getLogger(RecUnitCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"上海市环兴环境资源利用有限公司", "上海市工蚁环境资源利用有限公司"};
	String[] code_Array = {"5a5de487-8913-40c8-bcce-7a538be4ec40", "6b5de487-8913-40c8-bcce-7a538be4ec61"};
		
	//模拟数据函数
	private RecUnitCodesDTO SimuDataFunc() {
		RecUnitCodesDTO rucDto = new RecUnitCodesDTO();
		//时戳
		rucDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//回收单位编码列表模拟数据
		List<NameCode> recUnitCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ruc = new NameCode();
			ruc.setName(name_Array[i]);
			ruc.setCode(code_Array[i]);
			recUnitCodes.add(ruc);
		}
		// 分页
		PageBean<NameCode> pageBean = new PageBean<NameCode>(recUnitCodes, curPageNum, this.pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		rucDto.setPageInfo(pageInfo);
		//设置数据
		rucDto.setRecUnitCodes(recUnitCodes);
		//消息ID
		rucDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rucDto;
	}
	
	// 回收单位编码列表模型函数
	public RecUnitCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, SaasService saasService) {
		RecUnitCodesDTO rucDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			List<TProRecyclerSupplierDo> tprwDoList = saasService.getAllRecyclerIdName();
			if(tprwDoList != null) {
				rucDto = new RecUnitCodesDTO();
				//时戳
				rucDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//回收单位列表模拟数据
				List<NameCode> recUnitCodes = new ArrayList<>();
				//赋值并去重
				Map<String, String> recUnitNameToIdMap = new HashMap<>();
				for (int i = 0; i < tprwDoList.size(); i++) {
					recUnitNameToIdMap.put(tprwDoList.get(i).getSupplierName(), tprwDoList.get(i).getId());
				}
				for(String curKey : recUnitNameToIdMap.keySet()) {
					NameCode ruc = new NameCode();
					ruc.setName(curKey);
					ruc.setCode(curKey);
					recUnitCodes.add(ruc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(recUnitCodes, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				rucDto.setPageInfo(pageInfo);
				//设置数据
				rucDto.setRecUnitCodes(pageBean.getCurPageData());
				//消息ID
				rucDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取回收单位数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			rucDto = SimuDataFunc();
		}		

		return rucDto;
	}
}
