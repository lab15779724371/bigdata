package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据供应商列表应用模型
public class BdSupListAppMod {
	private static final Logger logger = LogManager.getLogger(BdSupListAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
		
	//数组数据初始化
	String[] supplierName_Array = {"上海清美食品有限公司", "上海齐泓食品有限公司"};
	String[] supplierType_Array = {"生产类", "经销类"};
	String[] province_Array = {"上海市", "上海市"};
	String[] distCounty_Array = {"浦东新区", "青浦区"};
	String[] detAddress_Array = {"上海市浦东新区宣桥镇三灶工业园区宣春路201号", "上海市青浦区盈港东路6372号5号楼"};
	String[] blNo_Array = {"310225000140649", "4210225000141649"};
	String[] regCapital_Array = {"100万", "200万"};
	String[] fblNo_Array = {"JY1561655500555", "JY2661655500555"};
	String[] fcpNo_Array = {"LT1561655500555", "LT2661655500555"};
	String[] fplNo_Array = {"SCKX1561655500555", "SCKX2661655500555"};
	int[] relRmcNum_Array = {2, 3};
	
	//模拟数据函数
	private BdSupListDTO SimuDataFunc() {
		BdSupListDTO bslDto = new BdSupListDTO();
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据供应商列表模拟数据
		List<BdSupList> bdSupList = new ArrayList<>();
		//赋值
		for (int i = 0; i < supplierName_Array.length; i++) {
			BdSupList bsl = new BdSupList();
			bsl.setSupplierName(supplierName_Array[i]);
			bsl.setSupplierType(supplierType_Array[i]);
			bsl.setProvince(province_Array[i]);
			bsl.setDistCounty(distCounty_Array[i]);
			bsl.setDetAddress(detAddress_Array[i]);
			bsl.setBlNo(blNo_Array[i]);
			bsl.setRegCapital(regCapital_Array[i]);
			bsl.setFblNo(fblNo_Array[i]);
			bsl.setFcpNo(fcpNo_Array[i]);
			bsl.setFplNo(fplNo_Array[i]);
			bsl.setRelRmcNum(relRmcNum_Array[i]);
			bdSupList.add(bsl);
		}
		//设置数据
		bslDto.setBdSupList(bdSupList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = supplierName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bslDto;
	}
	
	// 基础数据供应商列表函数
	BdSupListDTO bdSupList(String distIdorSCName, List<TEduDistrictDo> tddList, Db1Service db1Service, Db2Service db2Service, SaasService saasService, String supplierName, int supplierType, String province, String blNo, String regCapital, String fblNo, String fcpNo) {
		BdSupListDTO bslDto = new BdSupListDTO();
		List<BdSupList> bdSupList = new ArrayList<>();
		Map<String, String> groupSupplierDetailMap = new HashMap<>();
		int i;
		String key = null, keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//查询变脸索引
		int[] queryVarIdxs = new int[13];
		for(i = 0; i < queryVarIdxs.length; i++)
			queryVarIdxs[i] = -1;
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 3);
		for(i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		//学校id和供应商id
    	Map<String, String> SchIdTosupIdMap = new HashMap<>();
    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
    	if(tessDoList != null) {
    		for(i = 0; i < tessDoList.size(); i++) {
    			SchIdTosupIdMap.put(tessDoList.get(i).getSchoolId(), tessDoList.get(i).getSupplierId());
    		}
    	}
		// 基础数据供应商详情
    	key = "supplier-detail";
    	groupSupplierDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
    	if (groupSupplierDetailMap != null) {
    		for (String curKey : groupSupplierDetailMap.keySet()) {
    			keyVal = groupSupplierDetailMap.get(curKey);
    			// 基础数据供应商列表
    			String[] keyVals = keyVal.split(";");
    			if(keyVals.length >= 20) {
    				BdSupList bsl = new BdSupList();
    				//供应商id
    				i = AppModConfig.getVarValIndex(keyVals, "id");
    				String rmcId = null;
    				if(i != -1)
    					rmcId = keyVals[i];
    				if(rmcId == null)
    					continue ;
    				//供应商名称
    				bsl.setSupplierName("-");
    				i = AppModConfig.getVarValIndex(keyVals, "suppliername");    				
    				if(i != -1) {
    					queryVarIdxs[0] = i;
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setSupplierName(keyVals[i]);
    				}
    				//供应商类型
    				bsl.setSupplierType("-");
    				i = AppModConfig.getVarValIndex(keyVals, "classify");    				
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						if(keyVals[i].equalsIgnoreCase("0"))
    							bsl.setSupplierType("-");
    						else if(keyVals[i].equalsIgnoreCase("1"))
    							bsl.setSupplierType("生产类");
    						else if(keyVals[i].equalsIgnoreCase("2"))
    							bsl.setSupplierType("经销类");
    					}
    				}
    				//省市
    				bsl.setProvince("-");
    				//区县
    				bsl.setDistCounty("-");
    				i = AppModConfig.getVarValIndex(keyVals, "area");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setDistCounty(keyVals[i]);
    				}
    				//详细地址
    				bsl.setDetAddress("-");
    				i = AppModConfig.getVarValIndex(keyVals, "address");
    				if(i != -1) {
    					queryVarIdxs[1] = i;
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setDetAddress(keyVals[i]);
    				}
    				//营业执照编号
    				i = AppModConfig.getVarValIndex(keyVals, "businesslicense");
    				bsl.setBlNo("-");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setBlNo(keyVals[i]);
    				}
    				//注册资本
    				bsl.setRegCapital("-");
    				i = AppModConfig.getVarValIndex(keyVals, "regcapital");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setRegCapital(keyVals[i]);
    				}
    				//食品经营许可证编号
    				bsl.setFblNo("-");
    				i = AppModConfig.getVarValIndex(keyVals, "foodbusiness");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setFblNo(keyVals[i]);
    				}
    				//食品流通许可证编号
    				bsl.setFcpNo("-");
    				i = AppModConfig.getVarValIndex(keyVals, "foodcirculation");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setFcpNo(keyVals[i]);
    				}
    				//食品生产许可证编号
    				bsl.setFplNo("-");
    				i = AppModConfig.getVarValIndex(keyVals, "foodproduce");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setFplNo(keyVals[i]);
    				}
    				//关联供应商数量
    				bsl.setRelRmcNum(0);
    				//条件判断
    				boolean isAdd = true;
    				int[] flIdxs = new int[7];
    				//判断供应商名称（判断索引0）
    				if(supplierName != null) {
    					if(bsl.getSupplierName().indexOf(supplierName) == -1)
    						flIdxs[0] = -1;
    				}
    				//判断供应商类型（判断索引1）
    				if(supplierType != -1) {
    					int curSupplierType = AppModConfig.supplierTypeNameToIdMap.get(bsl.getSupplierType());
    					if(curSupplierType != supplierType)
    						flIdxs[1] = -1;
    				}   
    				//判断省市（判断索引2）
    				if(province != null) {
    					if(bsl.getProvince().indexOf(province) == -1)
    						flIdxs[2] = -1;
    				}
    				//判断营业执照（判断索引3）
    				if(blNo != null) {
    					if(bsl.getBlNo().indexOf(blNo) == -1)
    						flIdxs[3] = -1;
    				}    				
    				//判断注册资本（判断索引4）
    				if(regCapital != null) {
    					if(bsl.getRegCapital().indexOf(regCapital) == -1)
    						flIdxs[4] = -1;
    				}
    				//判断食品经营许可证（判断索引5）
    				if(fblNo != null) {
    					if(bsl.getFblNo().indexOf(fblNo) == -1)
    						flIdxs[5] = -1;
    				}
    				//判断食品流通许可证（判断索引6）
    				if(fcpNo != null) {
    					if(bsl.getFcpNo().indexOf(regCapital) == -1)
    						flIdxs[6] = -1;
    				}
    				//总体条件判断
    				for(i = 0; i < flIdxs.length; i++) {
    					if(flIdxs[i] == -1) {
    						isAdd = false;
    						break;
    					}
    				}
    				//是否满足条件
    				if(isAdd)
    					bdSupList.add(bsl);
    			}
    			else
    				logger.info("基础数据学校："+ curKey + "，格式错误！");
    		}
    	}
    	//排序
    	//SortList<BdSupList> sortList = new SortList<BdSupList>();  
    	//sortList.Sort(bdSupList, methods, sorts, dataTypes);
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<BdSupList> pageBean = new PageBean<BdSupList>(bdSupList, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setBdSupList(pageBean.getCurPageData());
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bslDto;
	}	
	
	// 基础数据供应商列表模型函数
	public BdSupListDTO appModFunc(String token, String supplierName, String supplierType, String distName, String prefCity, String province, String blNo, String regCapital, String fblNo, String fcpNo, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		BdSupListDTO bslDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			//供应商类型
			int curSupplierType = -1;
			if(supplierType != null) {
				curSupplierType = Integer.parseInt(supplierType);
			}
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) {    // 按区域，省或直辖市处理
				List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
				// 查找是否存在该区域和省市
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					if (curTdd.getId().compareTo(distName) == 0) {
						bfind = true;
						distIdorSCName = curTdd.getId();
						break;
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据供应商列表函数
					province = null;
					bslDto = bdSupList(distIdorSCName, tddList, db1Service, db2Service, saasService, supplierName, curSupplierType, province, blNo, regCapital, fblNo, fcpNo);		
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tddList = null;
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					tddList = db1Service.getListByDs1IdName();
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据供应商列表函数
					province = null;
					bslDto = bdSupList(distIdorSCName, tddList, db1Service, db2Service, saasService, supplierName, curSupplierType, province, blNo, regCapital, fblNo, fcpNo);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}														
		}
		else {    //模拟数据
			//模拟数据函数
			bslDto = SimuDataFunc();
		}		

		return bslDto;
	}
}
