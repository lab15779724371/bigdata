package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchKwDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//学校餐厨垃圾详情列表应用模型
public class SchKwDetsAppMod {
	private static final Logger logger = LogManager.getLogger(SchKwDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getPpName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//数组数据初始化	
	String[] recDate_Array = {"2018/08/01", "2018/08/01", "2018/08/01"};
	String[] distName_Array = {"11", "11", "11"};
	String[] ppName_Array = {"上海市徐汇区向阳小学", "上海市徐汇区世界小学", "上海市民办盛大花园小学"};
	String[] schType_Array = {"小学", "小学", "小学"};
	String[] rmcName_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司", "上海市民办盛大花园小学"};
	int[] recNum_Array = {1, 2, 1};
	String[] recComany_Array = {"上海市环兴环境资源利用有限公司", "上海市环兴环境资源利用有限公司", "上海市环兴环境资源利用有限公司"};
	String[] recPerson_Array = {"张山", "张山", "张山"};
	int[] recBillNum_Array = {1, 2, 1};
	
	//模拟数据函数
	private SchKwDetsDTO SimuDataFunc() {
		SchKwDetsDTO skdDto = new SchKwDetsDTO();
		//设置返回数据
		skdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<SchKwDets> schKwDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < recDate_Array.length; i++) {
			SchKwDets skd = new SchKwDets();
			skd.setRecDate(recDate_Array[i]);
			skd.setDistName(distName_Array[i]);
			skd.setPpName(ppName_Array[i]);
			skd.setSchType(schType_Array[i]);
			skd.setRmcName(rmcName_Array[i]);
			skd.setRecNum(String.valueOf(recNum_Array[i]));
			skd.setRecComany(recComany_Array[i]);
			skd.setRecPerson(recPerson_Array[i]);
			skd.setRecBillNum(recBillNum_Array[i]);
			schKwDets.add(skd);
		}
		//设置数据
		skdDto.setSchKwDets(schKwDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = recDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		skdDto.setPageInfo(pageInfo);
		//消息ID
		skdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return skdDto;
	}
	
	// 学校餐厨垃圾详情列表函数
	SchKwDetsDTO schKwDets(String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, String distName, 
			String prefCity, String province, String ppName, int schType, 
			String rmcName, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes) {
		SchKwDetsDTO skdDto = new SchKwDetsDTO();
		List<SchKwDets> schKwDets = new ArrayList<>();
		Map<String, String> schoolwasteMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null, keyVal = null;
		
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		if(distIdorSCName!=null && !"".equals(distIdorSCName)) {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,null,null,5);
		}else {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distNamesList,null,null);
		}
		for(i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		//团餐公司id和学校id映射
    	Map<String, String> schIdToSupplierIdMap = new HashMap<>();
    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
    	if(tesDoList != null) {
    		for(i = 0; i < tessDoList.size(); i++)
    			schIdToSupplierIdMap.put(tessDoList.get(i).getSchoolId(), tessDoList.get(i).getSupplierId());
    	}
		//团餐公司id和团餐公司名称
		Map<String, String> RmcIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
		// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_schoolwaste";
			schoolwasteMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (schoolwasteMap != null) {
				for (String curKey : schoolwasteMap.keySet()) {
					keyVal = schoolwasteMap.get(curKey);
					// 学校餐厨垃圾列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 12) {
						if(distIdorSCName != null) {
							if(keyVals[1].compareTo(distIdorSCName) != 0)
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(StringUtils.isEmpty(keyVals[1]) || !StringUtils.isNumeric(keyVals[1])) {
								continue;
							}
							if(!distNamesList.contains(keyVals[1])) {
								continue ;
							}
						}
						SchKwDets skd = new SchKwDets();
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(keyVals[3])) {
							j = schIdMap.get(keyVals[3]);
							tesDo = tesDoList.get(j-1);
						}
						//if(tesDo == null)
						//	continue;
						//就餐日期
						skd.setRecDate(dates[k].replaceAll("-", "/"));
						//区
						skd.setDistName("-");
						if(!keyVals[1].equalsIgnoreCase("null"))
							skd.setDistName(keyVals[1]);
						//项目点
						skd.setPpName("-");	
						if(tesDo !=null ) {
							skd.setPpName(tesDo.getSchoolName());	
						}
	    				//总校/分校
						int curSchGenBraFlag = 0;
						skd.setSchGenBraFlag("-");
	    				if(tesDo!=null &&tesDo.getIsBranchSchool() != null) {
	    					if(tesDo.getIsBranchSchool() == 1) { //分销
	    						curSchGenBraFlag = 2;
	    						skd.setSchGenBraFlag("分校");
	    					}
	    					else {   //总校
	    						curSchGenBraFlag = 1;
	    						skd.setSchGenBraFlag("总校");
	    					}
	    				}
	    				//分校数量
	    				int curBraCampusNum = 0;
	    				skd.setBraCampusNum(curBraCampusNum);
	    				if(curSchGenBraFlag == 1 && tesDo != null) {
	    					String curSchId = tesDo.getId();
	    					if(curSchId != null) {
	    						for(i = 0; i < tesDoList.size(); i++) {
	    							if(tesDoList.get(i).getParentId() != null) {
	    								if(curSchId.equalsIgnoreCase(tesDoList.get(i).getParentId())) {
	    									curBraCampusNum++;
	    								}
	    							}
	    						}
	    					}
	    					if(curBraCampusNum > 0)
	    						skd.setBraCampusNum(curBraCampusNum);
	    				}
	    				//关联总校
	    				skd.setRelGenSchName("-");
	    				if(tesDo!=null && schIdMap.containsKey(tesDo.getParentId())) {
	    					i = schIdMap.get(tesDo.getParentId())-1;
	    					skd.setRelGenSchName(tesDoList.get(i).getSchoolName());
	    				}    				
	    				//所属
	    				int curSubLevel = 0;
	    				if(tesDo!=null && tesDo.getDepartmentMasterId() != null) {
	    					curSubLevel = Integer.parseInt(tesDo.getDepartmentMasterId());
	    				}
	    				skd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
	    				//主管部门
	    				int curCompDep = 0;
	    				skd.setCompDep("其他");
	    				if(curSubLevel == 0) {      //其他
	    					if(tesDo!=null && CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					skd.setCompDep(AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel ==1) {      //部级   
	    					if(tesDo!=null &&  CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					skd.setCompDep(AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 2) {      //市级
	    					if(tesDo!=null &&  CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					skd.setCompDep(AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 3) {      //区级
	    					if(tesDo!=null && CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						String orgName = AppModConfig.compDepIdToNameMap3bd.get(tesDo.getDepartmentSlaveId());
	    						if(orgName != null) {
	    							curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
	    						}
	    					}
	    					skd.setCompDep(AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep)));
	    				}    				
	    				//所属区域名称
	    				skd.setSubDistName("-");
	    				if(tesDo!=null && tesDo.getSchoolAreaId() != null)
	    					skd.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));
						//学校学制
	    				skd.setSchType("-");
	    				if(tesDo!=null) {
	    					skd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));		
	    				}
						//学校性质
	    				skd.setSchProp("-");
	    				if(tesDo!=null) {
	    					skd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
	    				}
						//团餐公司
						skd.setRmcName("-");
						if(schIdToSupplierIdMap.containsKey(keyVals[3])) {
							String rmcId = schIdToSupplierIdMap.get(keyVals[3]);
							if(RmcIdToNameMap.containsKey(rmcId)) {
								skd.setRmcName(RmcIdToNameMap.get(rmcId));
							}
						}
						//回收数量
						if(!keyVals[5].equalsIgnoreCase("null")) {
							//注释原因：hive库中会出现带加号和带单位的值，目前之前取库，不做任何处理
							//float rcNum = Float.parseFloat(keyVals[5]);
							//BigDecimal bd = new BigDecimal(rcNum);
							//rcNum = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							skd.setRecNum(keyVals[5]);
						}
						//回收单位
						if(!keyVals[7].equalsIgnoreCase("null"))
							skd.setRecComany(keyVals[7]);
						//回收人
						skd.setRecPerson(keyVals[9]);
						//回收单据
						skd.setRecBillNum(0);
						if(!keyVals[11].equalsIgnoreCase("null"))
							skd.setRecBillNum(Integer.parseInt(keyVals[11]));
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[8];
						//判断项目点名称（判断索引0）
						if(ppName != null) {
							if(skd.getPpName().indexOf(ppName) == -1)
								flIdxs[0] = -1;
						}
						//判断学校类型（学制）（判断索引1）
						if(schType != -1) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(skd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(skd.getSchType());
								if(curSchType != schType)
									flIdxs[1] = -1;
							}
							else
								flIdxs[1] = -1;
						}else if(schTypesList!=null && schTypesList.size() >0) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(skd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(skd.getSchType());
								if(!schTypesList.contains(String.valueOf(curSchType))) {
									flIdxs[1] = -1;
								}
							}else{
								flIdxs[1] = -1;
							}
						}
						//判断团餐公司（判断索引2）
						if(rmcName != null && RmcIdToNameMap != null) {
							if(!(skd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
								flIdxs[2] = -1;
						}
						//回收单位名称（判断索引3）
						if(recComany != null) {
							if(skd.getRecComany().indexOf(recComany) == -1)
								flIdxs[3] = -1;
						}
						//回收人（判断索引4）
						if(recPerson != null) {
							if(skd.getRecPerson().indexOf(recPerson) == -1)
								flIdxs[4] = -1;
						}
						//判断学校性质（判断索引5）
						if(schProp != -1) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(skd.getSchProp());
							if(curSchProp != schProp)
								flIdxs[5] = -1;
						}else if(schPropsList!=null && schPropsList.size() >0) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(skd.getSchProp());
							if(!schPropsList.contains(String.valueOf(curSchProp))) {
								flIdxs[5] = -1;
							}
						}
						
						//判断所属（判断索引8）
						if(subLevel != -1) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(skd.getSubLevel());
							if(curSubLevel != subLevel)
								flIdxs[6] = -1;
						}else if(subLevelsList!=null && subLevelsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(skd.getSubLevel());
							if(!subLevelsList.contains(String.valueOf(curSubLevel))) {
								flIdxs[6] = -1;
							}
						}
						//判断主管部门（判断索引9）
						if(compDep != -1) {
							curCompDep = 0;
							if(subLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[7] = -1;
								}
								else
									flIdxs[7] = -1;
							}
							else if(subLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[7] = -1;
								}
								else
									flIdxs[7] = -1;
							}
							else if(subLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[7] = -1;
								}
								else
									flIdxs[7] = -1;
							}
							else if(subLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[7] = -1;
								}
								else
									flIdxs[7] = -1;
							}
						}else if(compDepsList!=null && compDepsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(skd.getSubLevel());
							curCompDep = 0;
							if(curSubLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[7] = -1;
									}
								}
								else
									flIdxs[7] = -1;
							}
							else if(curSubLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[7] = -1;
									}
								}
								else
									flIdxs[7] = -1;
							}
							else if(curSubLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[7] = -1;
									}
								}
								else
									flIdxs[7] = -1;
							}
							else if(curSubLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(skd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[7] = -1;
									}
								}
								else
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[7] = -1;
									}
							}
							
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
							schKwDets.add(skd);
					}
					else
						logger.info("学校餐厨垃圾："+ curKey + "，格式错误！");
				}
			}
		}
		//排序
    	SortList<SchKwDets> sortList = new SortList<SchKwDets>();  
    	sortList.Sort3Level(schKwDets, methods, sorts, dataTypes);
		//时戳
		skdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<SchKwDets> pageBean = new PageBean<SchKwDets>(schKwDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		skdDto.setPageInfo(pageInfo);
		//设置数据
		skdDto.setSchKwDets(pageBean.getCurPageData());
		//消息ID
		skdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return skdDto;
	}	
	
	// 学校餐厨垃圾详情列表函数
	SchKwDetsDTO schKwDetsFromHive(String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, String distName, 
			String prefCity, String province, String ppName, int schType, 
			String rmcId, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		SchKwDetsDTO skdDto = new SchKwDetsDTO();
		List<SchKwDets> schKwDets = new ArrayList<>();
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	
    	//团餐公司id和团餐公司名称
		Map<String, String> rmcIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(int i = 0; i < tpsDoList.size(); i++) {
    			rmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
    	
		//所有学校id
		List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		tesDoList = db1Service.getTEduSchoolDoListByDs1(null,null,null,5);
		Map<String,TEduSchoolDo> schMap = tesDoList.stream().collect(Collectors.toMap(TEduSchoolDo::getId,(b)->b));
    	
    	// 时间段内各区餐厨垃圾学校回收总数
    	String startDate = dates[dates.length-1];
    	String endDate = dates[0];
		if(startDate==null || startDate.split("-").length < 2) {
    		startDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	if((endDate==null || endDate.split("-").length < 2)&& startDate!=null) {
    		endDate = startDate;
    	}else if (endDate==null || endDate.split("-").length < 2) {
    		endDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	
    	String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];
    	
		//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<KwCommonDets> commonDets = new ArrayList<>();
		commonDets = dbHiveRecyclerWasteService.getRecyclerWasteDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName,
				ppName, schType, rmcId,null, recComany, recPerson, schProp, subLevel, compDep, null,
				distNamesList, subLevelsList, compDepsList, schPropsList, schTypesList, 1, 1, (curPageNum-1)*pageSize, curPageNum*pageSize);
		//移除所有未null的值
		commonDets.removeAll(Collections.singleton(null));
		Integer pageTotalTemp = dbHiveRecyclerWasteService.getRecyclerWasteDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName,
				ppName, schType, rmcId,null, recComany, recPerson, schProp, subLevel, compDep, null,
				distNamesList, subLevelsList, compDepsList, schPropsList, schTypesList, 1, 1);
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
    	
		String relGenSchName = "";
		for(KwCommonDets kwCommonDets : commonDets) {
			relGenSchName = "";
			SchKwDets schKwDetObj = new SchKwDets();
			try {
				BeanUtils.copyProperties(schKwDetObj, kwCommonDets);
				//关联学校
				if(CommonUtil.isNotEmpty(schKwDetObj.getRelGenSchName()) && !"-".equals(schKwDetObj.getRelGenSchName())) {
					if(schMap.get(schKwDetObj.getRelGenSchName()) !=null) {
						relGenSchName = schMap.get(schKwDetObj.getRelGenSchName()).getSchoolName();
					}
				}
				
				if(CommonUtil.isNotEmpty(relGenSchName)) {
					schKwDetObj.setRelGenSchName(relGenSchName);
				}else {
					schKwDetObj.setRelGenSchName("-");
				}
				
				//学校对应团餐公司名称
				schKwDetObj.setRmcName("-");
			    if(CommonUtil.isNotEmpty(kwCommonDets.getSchSupplierId())) {
			    	if(rmcIdToNameMap.get(kwCommonDets.getSchSupplierId()) != null) {
			    		schKwDetObj.setRmcName(rmcIdToNameMap.get(kwCommonDets.getSchSupplierId()));
			    	}
			    }
			} catch (IllegalAccessException e) {
				continue;
			} catch (InvocationTargetException e) {
				continue;
			}
			
			schKwDets.add(schKwDetObj);
		}
		//时戳
		skdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		skdDto.setPageInfo(pageInfo);
		//设置数据
		skdDto.setSchKwDets(schKwDets);
		//消息ID
		skdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return skdDto;
	}

	//学校餐厨垃圾详情列表模型函数
	public SchKwDetsDTO appModFunc(String token, String recStartDate, String recEndDate, String ppName, 
			String distName, String prefCity, String province, String schType, String rmcName, 
			String recComany, String recPerson, String schProp,String subLevel,String compDep, 
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String page, String pageSize,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		SchKwDetsDTO skdDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if (isRealData) { // 真实数据
			// 日期
			String[] dates = null;
			if (recStartDate == null || recEndDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(recStartDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(recEndDate);
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";  		
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，
			//10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//学校性质，0:公办，1:民办，2:其他
			int curSchProp = -1;
			if(schProp != null)
				curSchProp = Integer.parseInt(schProp);
			
			//所属，0:其他，1:部属，2:市属，3: 区属
			int curSubLevel = -1;
			if(subLevel != null)
				curSubLevel = Integer.parseInt(subLevel);
			//主管部门，0:市教委，1:商委，2:教育部
			int curCompDep = -1;
			if(compDep != null)
				curCompDep = Integer.parseInt(compDep);
			
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
					// 学校餐厨垃圾详情列表函数
					if(methodIndex == 1) {
						skdDto = schKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, province, 
								ppName, curSchType, rmcName, recComany, recPerson, curSchProp,
								curSubLevel, curCompDep, 
								distNames,subLevels,compDeps,schProps,schTypes);		
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						
						//日期包含30天之前的数据则去hive库
						if(days >= 2) {
							skdDto = schKwDetsFromHive(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, province, 
									ppName, curSchType, rmcName, recComany, recPerson, curSchProp,
									curSubLevel, curCompDep, 
									distNames,subLevels,compDeps,schProps,schTypes,
									dbHiveRecyclerWasteService);
						}else {
							skdDto = schKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, province, 
									ppName, curSchType, rmcName, recComany, recPerson, curSchProp,
									curSubLevel, curCompDep, 
									distNames,subLevels,compDeps,schProps,schTypes);
						}
					}
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
					// 学校餐厨垃圾详情列表函数
					if(methodIndex == 1) {
						skdDto = schKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, province, ppName,
								curSchType, rmcName, recComany, recPerson, curSchProp,
								curSubLevel, curCompDep, 
								distNames,subLevels,compDeps,schProps,schTypes);
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						
						//日期包含30天之前的数据则去hive库
						if(days >= 2) {
							skdDto = schKwDetsFromHive(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, province, 
									ppName, curSchType, rmcName, recComany, recPerson, curSchProp,
									curSubLevel, curCompDep, 
									distNames,subLevels,compDeps,schProps,schTypes,
									dbHiveRecyclerWasteService);
						}else {
							skdDto = schKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, province, 
									ppName, curSchType, rmcName, recComany, recPerson, curSchProp,
									curSubLevel, curCompDep, 
									distNames,subLevels,compDeps,schProps,schTypes);
						}
					}
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}												
		} else { // 模拟数据
			//模拟数据函数
			skdDto = SimuDataFunc();
		}

		return skdDto;
	}
}
