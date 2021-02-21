package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//项目点编码列表应用模型
public class PpNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(PpNameCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"松江区佘山镇中心幼儿园", "上海青浦区新希望民办小学"};
	String[] code_Array = {"003463bb-93f9-4c33-9154-e1576ab2d8a3", "003e2b98-3aec-41fb-99c0-1d984970b701"};
	
	//模拟数据函数
	private PpNameCodesDTO SimuDataFunc() {
		PpNameCodesDTO pncDto = new PpNameCodesDTO();
		//时戳
		pncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//项目点编码列表模拟数据
		List<NameCode> rmcList = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode pnc = new NameCode();
			pnc.setName(name_Array[i]);
			pnc.setCode(code_Array[i]);
			rmcList.add(pnc);
		}
		//设置数据
		pncDto.setPpNameCodes(rmcList);
		//消息ID
		pncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pncDto;
	}
	
	// 项目点编码列表模型函数
	public PpNameCodesDTO appModFunc(String token, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		PpNameCodesDTO pncDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			List<TEduSchoolDo> tesDoList = null;
			String departmentId = null;
			if(distName == null)
				tesDoList = db1Service.getTEduSchoolDoListByDs1(null,null,null, 0);
			else
				tesDoList = db1Service.getTEduSchoolDoListByDs1(distName,null,null, 0);
			if(departmentId == null)
				departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
			if(tesDoList != null) {
				pncDto = new PpNameCodesDTO();
				//时戳
				pncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//项目点编码列表模拟数据
				List<NameCode> rmcList = new ArrayList<>();
				//赋值
				for (int i = 0; i < tesDoList.size(); i++) {
					// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if (departmentId != null) {
						if (!tesDoList.get(i).getDepartmentId().equalsIgnoreCase(departmentId))
							continue;
					}
					NameCode pnc = new NameCode();
					pnc.setName(tesDoList.get(i).getSchoolName());
					pnc.setCode(tesDoList.get(i).getId());			
					rmcList.add(pnc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rmcList, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				pncDto.setPageInfo(pageInfo);
				//设置数据
				pncDto.setPpNameCodes(pageBean.getCurPageData());
				//消息ID
				pncDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取团餐公司数据失败");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			pncDto = SimuDataFunc();
		}		

		return pncDto;
	}
}
