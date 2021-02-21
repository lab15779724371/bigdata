package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.SchNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//学校名称编码列表应用模型
public class SchNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(SchNameCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"上海市天山中学", "上海市徐汇区向阳小学"};
	String[] code_Array = {"4f5de487-8913-40c8-bcce-7a538be4ec29", "5389de487-8913-40c8-bcce-7a538be4ec29"};
		
	//模拟数据函数
	private SchNameCodesDTO SimuDataFunc() {
		SchNameCodesDTO rmlDto = new SchNameCodesDTO();
		//时戳
		rmlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//学校名称编码列表模拟数据
		List<NameCode> rmcList = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode rml = new NameCode();
			rml.setName(name_Array[i]);
			rml.setCode(code_Array[i]);
			rmcList.add(rml);
		}
		//设置数据
		rmlDto.setSchNameCodes(rmcList);
		//消息ID
		rmlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rmlDto;
	}
	
	// 学校名称编码列表模型函数
	public SchNameCodesDTO appModFunc(String token, String distName, String prefCity, String province, String userName, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		SchNameCodesDTO rmlDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			String distIdorSCName = distName;
			if(distIdorSCName == null)
				distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
			//所有学校id
			List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 1);
			if(tesDoList != null) {
				rmlDto = new SchNameCodesDTO();
				//时戳
				rmlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//学校名称编码列表模拟数据
				List<NameCode> rmcList = new ArrayList<>();
				//赋值
				for (int i = 0; i < tesDoList.size(); i++) {
					// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if (distIdorSCName != null) {
						if (!tesDoList.get(i).getArea().equalsIgnoreCase(distIdorSCName))
							continue;
					}
					NameCode rml = new NameCode();
					rml.setName(tesDoList.get(i).getSchoolName());
					rml.setCode(tesDoList.get(i).getId());			
					rmcList.add(rml);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rmcList, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				rmlDto.setPageInfo(pageInfo);
				//设置数据
				rmlDto.setSchNameCodes(pageBean.getCurPageData());
				//消息ID
				rmlDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取学校名称编码数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			rmlDto = SimuDataFunc();
		}		

		return rmlDto;
	}
}
