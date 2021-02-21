package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//团餐公司列表应用模型
public class RmcListAppMod {
	private static final Logger logger = LogManager.getLogger(RmcListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"上海绿捷", "上海龙神"};
	String[] code_Array = {"4f5de487-8913-40c8-bcce-7a538be4ec29", "5389de487-8913-40c8-bcce-7a538be4ec29"};
		
	//模拟数据函数
	private RmcListDTO SimuDataFunc() {
		RmcListDTO rmlDto = new RmcListDTO();
		//时戳
		rmlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//团餐公司列表模拟数据
		List<NameCode> rmcList = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode rml = new NameCode();
			rml.setName(name_Array[i]);
			rml.setCode(code_Array[i]);
			rmcList.add(rml);
		}
		//设置数据
		rmlDto.setRmcList(rmcList);
		//消息ID
		rmlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rmlDto;
	}
	
	// 团餐公司列表模型函数
	public RmcListDTO appModFunc(String token, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		RmcListDTO rmlDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
			String distIdorSCName = null;
			if(distIdorSCName == null)
				distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
			if(tpsDoList != null) {
				//学校名称到学校id映射
				List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(null,1,1, 0);
		    	Map<String, String> schIdToAreaMap = new HashMap<>();
		    	for(int i = 0; i < tesDoList.size(); i++)
		    		schIdToAreaMap.put(tesDoList.get(i).getId(), tesDoList.get(i).getArea());
		    	//学校id和团餐公司id映射
		    	Map<String, String> supplierIdToAreaMap = new HashMap<>();
		    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
		    	if(tesDoList != null) {
		    		for(int i = 0; i < tessDoList.size(); i++) {
		    			if(schIdToAreaMap.containsKey(tessDoList.get(i).getSchoolId()))
		    				supplierIdToAreaMap.put(tessDoList.get(i).getSupplierId(), schIdToAreaMap.get(tessDoList.get(i).getSchoolId()));
		    		}
		    	}
				rmlDto = new RmcListDTO();
				//时戳
				rmlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//团餐公司列表模拟数据
				List<NameCode> rmcList = new ArrayList<>();
				//赋值
				for (int i = 0; i < tpsDoList.size(); i++) {
					if(distIdorSCName != null) {
						if(supplierIdToAreaMap.containsKey(tpsDoList.get(i).getId())) {
							String curDistId = supplierIdToAreaMap.get(tpsDoList.get(i).getId());
							if(!curDistId.equalsIgnoreCase(distIdorSCName))
								continue ;
						}
						else
							continue ;
					}
					NameCode rml = new NameCode();
					rml.setName(tpsDoList.get(i).getSupplierName());
					rml.setCode(tpsDoList.get(i).getId());			
					rmcList.add(rml);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rmcList, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				rmlDto.setPageInfo(pageInfo);
				//设置数据
				rmlDto.setRmcList(pageBean.getCurPageData());
				//消息ID
				rmlDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取团餐公司数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			rmlDto = SimuDataFunc();
		}		

		return rmlDto;
	}
}
