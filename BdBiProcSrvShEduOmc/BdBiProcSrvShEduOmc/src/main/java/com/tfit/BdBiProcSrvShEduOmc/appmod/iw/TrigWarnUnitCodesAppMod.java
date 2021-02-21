package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.TrigWarnUnitCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//触发预警单位编码列表应用模型
public class TrigWarnUnitCodesAppMod {
	private static final Logger logger = LogManager.getLogger(TrigWarnUnitCodesAppMod.class.getName());
	
	// 是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;

	// 数组数据初始化
	String[] name_Array = { "松江区佘山镇中心幼儿园", "上海青浦区新希望民办小学" };
	String[] code_Array = { "003463bb-93f9-4c33-9154-e1576ab2d8a3", "003e2b98-3aec-41fb-99c0-1d984970b701" };
	
	// 模拟数据函数
	private TrigWarnUnitCodesDTO SimuDataFunc() {
		TrigWarnUnitCodesDTO twucDto = new TrigWarnUnitCodesDTO();
		// 时戳
		twucDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 触发预警单位编码列表模拟数据
		List<NameCode> trigWarnUnitCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode twuc = new NameCode();
			twuc.setName(name_Array[i]);
			twuc.setCode(code_Array[i]);
			trigWarnUnitCodes.add(twuc);
		}
		// 设置数据
		twucDto.setTrigWarnUnitCodes(trigWarnUnitCodes);
		// 消息ID
		twucDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return twucDto;
	}

	// 触发预警单位编码列表模型函数
	public TrigWarnUnitCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, SaasService saasService) {
		TrigWarnUnitCodesDTO twucDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
			if(tpsDoList != null) {
				twucDto = new TrigWarnUnitCodesDTO();
				//时戳
				twucDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//触发预警单位列表模拟数据
				List<NameCode> rmcList = new ArrayList<>();
				//赋值
				for (int i = 0; i < tpsDoList.size(); i++) {
					NameCode twuc = new NameCode();
					twuc.setName(tpsDoList.get(i).getSupplierName());
					twuc.setCode(tpsDoList.get(i).getId());	
					rmcList.add(twuc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rmcList, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				twucDto.setPageInfo(pageInfo);
				//设置数据
				twucDto.setTrigWarnUnitCodes(pageBean.getCurPageData());
				//消息ID
				twucDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取触发预警单位数据失败！");
			}			
		} else { // 模拟数据
			// 模拟数据函数
			twucDto = SimuDataFunc();
		}

		return twucDto;
	}
}
