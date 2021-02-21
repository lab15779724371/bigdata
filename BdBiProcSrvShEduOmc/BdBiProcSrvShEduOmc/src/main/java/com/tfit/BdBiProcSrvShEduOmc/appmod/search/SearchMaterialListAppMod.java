package com.tfit.BdBiProcSrvShEduOmc.appmod.search;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.MaterialList;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchMaterialListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.obj.search.AppTEduMaterialDishD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//一键排查-原料列表应用模型
public class SearchMaterialListAppMod {
	private static final Logger logger = LogManager.getLogger(SearchMaterialListAppMod.class.getName());
	
	//二级排序条件
	final String[] methods = {"getDistName", "getSchType"};
	final String[] sorts = {"asc", "asc"};
	final String[] dataTypes = {"String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 14;
	
	//获取证件索引，0对应食品经营许可证、1对应餐饮服务许可证
	int getLicIndex(String[] keyVals) {
		int i, index = 0;
		i = AppModConfig.getVarValIndex(keyVals, "slictype");
		if(i != -1) {
			if(!keyVals[i].equalsIgnoreCase("null")) 
				index = 0;
			else
				index = 1;
		}
		
		return index;
	}
	
	// 基础数据学校列表函数
	SearchMaterialListDTO searchSchList(String distIdorSCName,Map<String,String> inputMap,DbHiveService dbHiveService) {
		SearchMaterialListDTO bslDto = new SearchMaterialListDTO();
		List<AppTEduMaterialDishD> searchSchList = new ArrayList<>();
		
		if(CommonUtil.isNotEmpty(inputMap.get("page"))) {
			this.curPageNum = Integer.parseInt(inputMap.get("page").toString());
		}
		if(CommonUtil.isNotEmpty(inputMap.get("pageSize"))) {
			this.pageSize = Integer.parseInt(inputMap.get("pageSize").toString());
		}
		
		String startDate = inputMap.get("startDate")==null?BCDTimeUtil.convertNormalDate(null):inputMap.get("startDate").toString();
		String endDate = inputMap.get("endDate")==null?BCDTimeUtil.convertNormalDate(null):inputMap.get("endDate").toString();
		
		if(startDate==null || startDate.split("-").length < 2) {
    		startDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	if((endDate==null || endDate.split("-").length < 2)&& startDate!=null) {
    		endDate = startDate;
    	}else if (endDate==null || endDate.split("-").length < 2) {
    		endDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	
    	//获取开始日期、结束日期的年月集合(格式：年份_开始月份_结束月份)
    	List<String> listYearMonth = CommonUtil.getYearMonthList(startDate, endDate);
		//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		
		AppTEduMaterialDishD inputObj = CommonUtil.map2Object(inputMap, AppTEduMaterialDishD.class);
		if(inputObj == null) {
			//return new ApiResponse<>(IOTRspType.Param_VisitFrmErr, IOTRspType.Param_VisitFrmErr.getMsg()); 
		}
		//权限过滤
		if(CommonUtil.isEmpty(inputObj.getArea())) {
			inputObj.setArea(distIdorSCName);
		}
		
		searchSchList = dbHiveService.getAppTEduMaterialDishDList(listYearMonth, startDate, endDateAddOne,inputObj, -1, -1);
		//key:学校编号+供应商编号 value：学校原料实体
		Map<String,MaterialList> resultMap = new HashMap<>();
		//区集合
		Set<String> distSet = new HashSet<>();
		//key：学校编号 value：学校对象
		Map<String,SearchSch> schoolMap = new HashMap<>();
		
		for(AppTEduMaterialDishD appTEduMaterialDishD : searchSchList) {
			MaterialList materialList = resultMap.get(appTEduMaterialDishD.getSchoolId()+appTEduMaterialDishD.getSupplyId());
			if(materialList == null) {
				materialList = new MaterialList();
				try {
					BeanUtils.copyProperties(materialList, appTEduMaterialDishD);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				String dish = appTEduMaterialDishD.getDishes();
				if(CommonUtil.isNotEmpty(dish)) {
					List<Object> dishTemp = CommonUtil.changeStringToList(dish);
					materialList.setDishSet(new HashSet<Object>(dishTemp));
				}
			}else {
				String dish = appTEduMaterialDishD.getDishes();
				if(CommonUtil.isNotEmpty(dish)) {
					List<Object> dishTemp = CommonUtil.changeStringToList(dish);
					if(materialList.getDishSet() !=null) {
						materialList.getDishSet().addAll(dishTemp);
					}else {
						materialList.setDishSet(new HashSet<Object>(dishTemp));
					}
				}
			}
			resultMap.put(appTEduMaterialDishD.getSchoolId()+appTEduMaterialDishD.getSupplyId(), materialList);
			
			//区集合
			distSet.add(appTEduMaterialDishD.getArea());
			if(schoolMap.get(appTEduMaterialDishD.getSchoolId()) == null) {
				SearchSch searchSch = new SearchSch();
				searchSch.setSchoolId(appTEduMaterialDishD.getSchoolId());
				searchSch.setSchName(appTEduMaterialDishD.getSchoolName());
				schoolMap.put(appTEduMaterialDishD.getSchoolId(), searchSch);
			}
		}
		bslDto.setDistCount(distSet.size());
		bslDto.setSchoolCount(schoolMap.size());
		bslDto.setSchoolList(new ArrayList<>(schoolMap.values()));
		
		List<MaterialList> resultList = new ArrayList<MaterialList>(resultMap.values());
    	//排序
    	//SortList<SearchSchList> sortList = new SortList<SearchSchList>();  
    	//sortList.Sort(searchSchList, methods, sorts, dataTypes);
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		
		
		PageBean<MaterialList> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<MaterialList>(resultList, 1, resultList.size());
		}else {
			pageBean = new PageBean<MaterialList>(resultList, curPageNum, pageSize);
		}
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		// 设置数据
		bslDto.setMaterialList(pageBean.getCurPageData());
		
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bslDto;
	}	
	
	// 基础数据学校列表模型函数
	public SearchMaterialListDTO appModFunc(String token,Map<String,String> inputMap,
			Db1Service db1Service,Db2Service db2Service,DbHiveService dbHiveService) {
		
		String province = inputMap.get("province")==null?null:inputMap.get("province").toString();
		String distName = inputMap.get("distName")==null?null:inputMap.get("distName").toString();
		String prefCity = inputMap.get("prefCity")==null?null:inputMap.get("prefCity").toString();
		
		SearchMaterialListDTO bslDto = null;
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";  		
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
					// 基础数据学校列表函数
					bslDto = searchSchList(distIdorSCName, inputMap, dbHiveService);		
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据学校列表函数
					bslDto = searchSchList(distIdorSCName, inputMap, dbHiveService);	
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}															
		}
		else {    //模拟数据
			//模拟数据函数
			//bslDto = SimuDataFunc();
		}		

		return bslDto;
	}
}
