package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.GenSchCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//总校编码列表应用模型
public class GenSchCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"上海市徐汇区世界小学", "上海市世界外国语小学", "上海市徐汇区向阳小学"};
	String[] code_Array = {"32bca1b9-9a16-42ff-8c90-6186d287be2c", "43bca1b9-9a16-42ff-8c90-6186d287be2c", "54bca1b9-9a16-42ff-8c90-6186d287be2c"};
	
	//模拟数据函数
	private GenSchCodesDTO SimuDataFunc() {
		GenSchCodesDTO gscDto = new GenSchCodesDTO();
		//时戳
		gscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//总校编码列表模拟数据
		List<NameCode> genSchCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode gsc = new NameCode();
			gsc.setName(name_Array[i]);
			gsc.setCode(code_Array[i]);			
			genSchCodes.add(gsc);
		}
		//设置数据
		gscDto.setGenSchCodes(genSchCodes);
		//消息ID
		gscDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gscDto;
	}
	
	// 总校编码列表模型函数
	public GenSchCodesDTO appModFunc(String token, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		GenSchCodesDTO gscDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			List<TEduSchoolDo> tesDoList = null;
			String distIdorSCName = distName;
			if(distIdorSCName == null)
				distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
			tesDoList = db1Service.getGenSchIdNameListByDs1(distIdorSCName);
			if(tesDoList != null) {
				gscDto = new GenSchCodesDTO();
				//时戳
				gscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//项目点编码列表模拟数据
				List<NameCode> rmcList = new ArrayList<>();
				//赋值
				for (int i = 0; i < tesDoList.size(); i++) {
					NameCode gsc = new NameCode();
					gsc.setName(tesDoList.get(i).getSchoolName());
					gsc.setCode(tesDoList.get(i).getId());			
					rmcList.add(gsc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rmcList, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				gscDto.setPageInfo(pageInfo);
				//设置数据
				gscDto.setGenSchCodes(pageBean.getCurPageData());
				//消息ID
				gscDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();		
			}
		}
		else {    //模拟数据
			//模拟数据函数
			gscDto = SimuDataFunc();
		}		
		return gscDto;
	}
}
