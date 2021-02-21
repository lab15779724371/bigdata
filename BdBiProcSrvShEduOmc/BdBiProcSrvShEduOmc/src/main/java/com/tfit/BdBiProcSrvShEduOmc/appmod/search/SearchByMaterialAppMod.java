package com.tfit.BdBiProcSrvShEduOmc.appmod.search;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchByMaterialDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchByMaterialDetail;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchLicense;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSupplier;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSupplyMatSup;
import com.tfit.BdBiProcSrvShEduOmc.obj.search.AppTEduMaterialDishD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//一键排查-排查信息应用模型
public class SearchByMaterialAppMod {
	private static final Logger logger = LogManager.getLogger(SearchByMaterialAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
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
	SearchByMaterialDTO searchSchList(String distIdorSCName,Map<String,String> inputMap,Db1Service db1Service, DbHiveService dbHiveService) {
		SearchByMaterialDTO bslDto = new SearchByMaterialDTO();
		List<SearchByMaterialDetail> schoolDetails = new ArrayList<SearchByMaterialDetail>();
		
		if(CommonUtil.isNotEmpty(inputMap.get("page"))) {
			this.curPageNum = Integer.parseInt(inputMap.get("page").toString());
		}
		if(CommonUtil.isNotEmpty(inputMap.get("pageSize"))) {
			this.pageSize = Integer.parseInt(inputMap.get("pageSize").toString());
		}
		
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<SearchByMaterialDetail> pageBean = new PageBean<SearchByMaterialDetail>(schoolDetails, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setSchoolDetails(pageBean.getCurPageData());
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		String startDate = inputMap.get("startDate");
		String endDate = inputMap.get("endDate");
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
		
		List<AppTEduMaterialDishD> searchMaterialList = dbHiveService.getAppTEduMaterialDishDList(listYearMonth, startDate, endDateAddOne,inputObj, -1, -1);
		if(searchMaterialList ==null || searchMaterialList.size()==0) {
			return bslDto;
		}
		//学校集合
		Set<String> schoolSet = new HashSet<>();
		//团餐公司编号集合
		Set<String> rmcIds = new HashSet<String>();
		//关联关系编号集合
		Set<String> schoolSupplierIds = new HashSet<String>();
		//学校和团餐公司对应关系集合（key：学校编号 value：对应的菜品的团餐公司集合）
		Map<String,Set<String>> schRmcMap = new HashMap<String,Set<String>>();
		Set<String>  rmcIdSchList = new HashSet<String>();
		
		//:key:学校好团餐公司关联关系编号 : value :学校编号+团餐公司编号
		Map<String,String> schSchoolSupplierIdMap = new HashMap<String,String>();
		for(AppTEduMaterialDishD appTEduMaterialDishD : searchMaterialList) {
			//学校集合
			schoolSet.add(appTEduMaterialDishD.getSchoolId());
			rmcIds.add(appTEduMaterialDishD.getSupplierId());
			schoolSupplierIds.add(appTEduMaterialDishD.getProjId());
			
			//组织学校团餐公司的对应关系
			rmcIdSchList = new HashSet<String>();
			if(schRmcMap.get(appTEduMaterialDishD.getSchoolId()) != null) {
				rmcIdSchList = schRmcMap.get(appTEduMaterialDishD.getSchoolId());
			}
			rmcIdSchList.add(appTEduMaterialDishD.getSupplierId());
			schRmcMap.put(appTEduMaterialDishD.getSchoolId(), rmcIdSchList);
			
			schSchoolSupplierIdMap.put(appTEduMaterialDishD.getProjId(),appTEduMaterialDishD.getSchoolId()+"_"+appTEduMaterialDishD.getSupplierId());
		}
		
		logger.info("***********************schoolSupplierIds:"+schoolSupplierIds);
		List<String> schoolIds = new ArrayList<String>(schoolSet);
		/**
		 * 一：获取数据
		 */
		//1.获取学校列表（所有取数的基数）
		List<SearchSch> searchSchList  = dbHiveService.getSchList(null, distIdorSCName,null,schoolIds,null, null);
		
		DateFormat format= new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		
		
		//2.三、关联学校详情(根据学校信息全部获取，根据每个学校组织数据)
		/**
		 * ②学校信息
		 */
	  	//获取学校证书信息，关联得出学校食品经营许可证
		//0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 6：税务登记证
		//7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 
		//14:运输许可证 15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证   
		List<Integer> licTypeList = new ArrayList<Integer>();
		licTypeList.add(1);
		licTypeList.add(0);
		List<SearchLicense> searchSchLicList  = db1Service.getLicenseList(null, distIdorSCName, schoolIds,null, licTypeList, 3,null, null, null);
		Map<String, SearchLicense> schLicLisMap = new HashMap<String, SearchLicense>();
		if(searchSchLicList!=null && searchSchLicList.size()>0) {
			schLicLisMap = searchSchLicList.stream().collect(Collectors.toMap(SearchLicense::getRelationIdAndlicType, Function.identity(), (key1, key2) -> key2));
		}
		/**
		 * ③证照信息（菜品对应团餐公司人员证照）
		 */
		//证书来源，0：供应商-，1：从业人员-雇员，2：商品，3：食堂--教委web学校的法人证
		List <String> rmcIdList = new ArrayList<String>();
		if(rmcIds !=null && rmcIds.size()>0) {
				rmcIdList=Arrays.asList(rmcIds.toArray(new String[0]));
		}
		if(rmcIdList == null || rmcIdList.size()==0) {
			rmcIdList.add("-10");
		}
		
		List <String> schoolSupplierIdTempList = new ArrayList<String>();
		if(schoolSupplierIds !=null && schoolSupplierIds.size()>0) {
			schoolSupplierIdTempList=Arrays.asList(schoolSupplierIds.toArray(new String[0]));
		}
		if(schoolSupplierIdTempList == null || schoolSupplierIdTempList.size()==0) {
			schoolSupplierIdTempList.add("-10");
		}
		
		//20:员工健康证； 22:A1证  23:B证  24:C证 25:A2证 
		licTypeList = new ArrayList<Integer>();
		licTypeList.add(20);
		licTypeList.add(22);
		licTypeList.add(23);
		licTypeList.add(24);
		licTypeList.add(25);
		
		List<SearchLicense> searchSupplierLicList  = db1Service.getEmployeeLicenseList(null, distIdorSCName, null, null,schoolSupplierIdTempList,licTypeList, 1,null, null, null);
		
		//key:schoolSupplierId value:shoolId+supplier
		//证书集合：key：学校编号+团餐公司编号 value 证书集合
		Map<String,Set<SearchLicense>> rmcPersonMap = new HashMap<String,Set<SearchLicense>>();
		Set<SearchLicense>  rmcPersonList = new HashSet<SearchLicense>();
		Integer comStat = 0;
		String schoolIdAndRmcId = "";
		String schoolId = "";
		if(searchSupplierLicList !=null && searchSupplierLicList.size()>0) {
			for(SearchLicense searchLicense:searchSupplierLicList) {
				
				if(searchLicense ==null) {
					continue;
				}
				
				schoolIdAndRmcId = schSchoolSupplierIdMap.get(searchLicense.getSchoolSupplierId());
				if(StringUtils.isNotEmpty(schoolIdAndRmcId)) {
					schoolId = schoolIdAndRmcId.split("_")[0];
				}
				
				//组织学校团餐公司的对应关系
				rmcPersonList = new HashSet<SearchLicense>();
				if(rmcPersonMap.get(schoolId+"_"+searchLicense.getSupplierId()) != null) {
					rmcPersonList = rmcPersonMap.get(schoolId+"_"+searchLicense.getSupplierId());
				}
				
				comStat = CommonUtil.compareStrDate(format.format(date), searchLicense.getLicEndDate(),"yyyy/MM/dd");
				
				if(comStat == 2) {
					comStat = 0;
				}else {
					comStat = 1;
				}
				searchLicense.setStat(String.valueOf(comStat));
				rmcPersonList.add(searchLicense);
				rmcPersonMap.put((schoolId+"_"+searchLicense.getSupplierId()), rmcPersonList);
			}
		}
		
		logger.info("***************************************rmcPersonMap:"+rmcPersonMap.toString());
		
		/**
		 * ④团餐公司信息
		 */
		List<SearchSupplier> rmcList = new ArrayList<SearchSupplier>();
		if(rmcIds !=null && rmcIds.size() >0) {
			rmcList= dbHiveService.getSupplierList(null, rmcIdList, null, null);
		}
		
		Map<String, SearchSupplier> rmcMap = new HashMap<String,SearchSupplier>();
		if(rmcList!=null && rmcList.size()>0) {
			rmcMap = rmcList.stream().collect(Collectors.toMap(SearchSupplier::getSupplierId, Function.identity(), (key1, key2) -> key2));
		}
				
		/**
		 * ⑥配送单明细
		 */
		List<CaMatSupDets> caMatSupDets = new ArrayList<>();
		//分页总数
		try {
			//获取列表  
			caMatSupDets = dbHiveService.getCaMatSupDetsList(listYearMonth, startDate, endDateAddOne, null, 
					distIdorSCName,schoolIds, null,null, null, null, inputObj.getWareBatchNo(), -1, -1, -1, 
					null, null, null,inputObj.getSupplyName(),inputObj.getMaterialName());
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
		
		//学校和供应商对应关系集合（key：学校编号 value：原料和供应商的关系map（key：供应商名称，value：原料集合））
		Map<String,Map<String,List<CaMatSupDets>>> schSupplierMap = new HashMap<String,Map<String,List<CaMatSupDets>>>();
		Set<String> supplierIds = new HashSet<String>();
		Map<String,List<CaMatSupDets>> supplierMatMap = new HashMap<String,List<CaMatSupDets>>();
		List<CaMatSupDets> caMatSupDetList = new ArrayList<CaMatSupDets>();
		Set<String>  supplierIdSchList = new HashSet<String>();
		//学校和供应商编号对应关系
		Map<String,Set<String>> schSupplierIdMap = new HashMap<String,Set<String>>();
		
		if(caMatSupDets !=null && caMatSupDets.size()>0) {
			for(CaMatSupDets caMatSupDetTemp:caMatSupDets) {
				supplierIds.add(caMatSupDetTemp.getSupplyId());
				
				//组织学校团餐公司的对应关系
				supplierIdSchList = new HashSet<String>();
				if(schSupplierIdMap.get(caMatSupDetTemp.getSchoolId()) != null) {
					supplierIdSchList = schSupplierIdMap.get(caMatSupDetTemp.getSchoolId());
				}
				supplierIdSchList.add(caMatSupDetTemp.getSupplyId());
				schSupplierIdMap.put(caMatSupDetTemp.getSchoolId(), supplierIdSchList);
				
				
				//组织学校和供应商的对应关系
				supplierMatMap = new HashMap<String,List<CaMatSupDets>>();
				
				if(schSupplierMap.get(caMatSupDetTemp.getSchoolId()) != null) {
					supplierMatMap = schSupplierMap.get(caMatSupDetTemp.getSchoolId());
				}
				caMatSupDetList = new ArrayList<CaMatSupDets>();
				if(supplierMatMap.get((caMatSupDetTemp.getSupplyId()+"_"+caMatSupDetTemp.getSupplierName())) !=null) {
					caMatSupDetList = supplierMatMap.get((caMatSupDetTemp.getSupplyId()+"_"+caMatSupDetTemp.getSupplierName()));
				}
				
				caMatSupDetList.add(caMatSupDetTemp);
				supplierMatMap.put((caMatSupDetTemp.getSupplyId()+"_"+caMatSupDetTemp.getSupplierName()), caMatSupDetList);
				schSupplierMap.put(caMatSupDetTemp.getSchoolId(), supplierMatMap);
			}
		}
		
		
		/**
		 * ⑤供应商信息
		 */
		//Map<String,List<String>> schSupplierMap = new HashMap<String,List<String>>();
		List<SearchSupplier> supplierList = null ;
		List<String> supplierIdsList = new ArrayList<String>();
		if(supplierIds !=null && supplierIds.size() >0) {
			supplierIdsList =Arrays.asList(supplierIds.toArray(new String[0]));
			supplierList= dbHiveService.getSupplierList(null, supplierIdsList, null, null);
		}
		Map<String, SearchSupplier> supplierMap = new HashMap<String, SearchSupplier>();
		if(supplierList!=null && supplierList.size()>0) {
			supplierMap = supplierList.stream().collect(Collectors.toMap(SearchSupplier::getSupplierId, Function.identity(), (key1, key2) -> key2));
		}

		
		//团餐公司、供应商证书信息
		//获取学校证书信息，关联得出学校食品经营许可证
		//0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 6：税务登记证
		//7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 
		//14:运输许可证 15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证   
		licTypeList = new ArrayList<Integer>();
		licTypeList.add(1);
		licTypeList.add(0);
		licTypeList.add(5);
		
		//供应商证书
		licTypeList.add(3);
		licTypeList.add(2);
		List<String> relationIdList = new ArrayList<String>();
		relationIdList.add("-10");
		if(supplierIdsList !=null && supplierIdsList.size()>0) {
			relationIdList.addAll(supplierIdsList);
		}
			
		if(rmcIdList !=null && rmcIdList.size() >0) {
			relationIdList.addAll(rmcIdList);
		}
		
		//证书来源，0：供应商-，1：从业人员-雇员，2：商品，3：食堂--教委web学校的法人证
		List<Integer> cerSourceList = new ArrayList<Integer>();
		cerSourceList.add(0);
		cerSourceList.add(2);
		List<SearchLicense> supplierLicList  = db1Service.getLicenseList(null, distIdorSCName, relationIdList,null, licTypeList, null,cerSourceList, null, null);
		Map<String, SearchLicense> supplierLicLisMap = new HashMap<>();
		if(supplierLicList!=null && supplierLicList.size()>0) {
			supplierLicLisMap = supplierLicList.stream().collect(Collectors.toMap(SearchLicense::getRelationIdAndlicType, Function.identity(), (key1, key2) -> key2));
		}
		logger.info("***********************supplierLicLisMap:"+supplierLicLisMap.toString());		
		
		/**
		 * 二：组织数据	
		 */
		if(searchSchList!=null && searchSchList.size()>0 ) {
			
			//团餐公司
			SearchSupplier rmcTemp = new SearchSupplier();
			List<SearchSupplier> rmcListTemp = new ArrayList<SearchSupplier>();
			Set<String> rmcIdListTemp = new HashSet<String>();
			
			//供应商
			SearchSupplier supplierTemp = new SearchSupplier();
			List<SearchSupplier> supplierListTemp = new ArrayList<SearchSupplier>();
			
			List<SearchSch> searchSchDetailList = new ArrayList<SearchSch>();
			//关联证书编号
			SearchLicense searchLicense = new SearchLicense();
			
			//人员证书
			List<SearchLicense> rmcPersonLiceListTemp = new ArrayList<SearchLicense>();
			
			//配货单
			List<SearchSupplyMatSup> supplyMatSupList = new ArrayList<SearchSupplyMatSup>();
			SearchSupplyMatSup searchSupplyMatSupTemp = new SearchSupplyMatSup();
			
			
			SearchByMaterialDetail schoolDetail = new SearchByMaterialDetail();
			//排序
			//配送单排序
			String[] matSortListMethods = {"getMatUseDate"};
	    	SortList<SearchSupplyMatSup> matSortList = new SortList<SearchSupplyMatSup>(); 

	    	//排序（团餐公司）
			String[] rmcMethods = {"getSupplierName"};
	    	SortList<SearchSupplier> rmcSortList = new SortList<SearchSupplier>(); 

			//排序(证书)
	    	SortList<SearchLicense> sortList = new SortList<SearchLicense>();  
			String[] methods = {"getWrittenName"};
			String[] sorts = {"asc"};
			String[] dataTypes = {"String"};
			
			//20:员工健康证
			Integer healthLicenseCount = 0;
			//22:A1证  
			Integer aoneLicenseCount = 0;
			//23:B证  
			Integer blicenseCount = 0;
			//24:C证 
			Integer clicenseCount = 0;
			//25:A2证
			Integer atwoLicenseCount = 0;
			
			for(SearchSch searchSch:searchSchList) {
				
				//20:员工健康证
				healthLicenseCount = 0;
				//22:A1证  
				aoneLicenseCount = 0;
				//23:B证  
				blicenseCount = 0;
				//24:C证 
				clicenseCount = 0;
				//25:A2证
				atwoLicenseCount = 0;
				
				schoolDetail = new SearchByMaterialDetail();
				if( searchSch ==null) {
					continue;
				}
				//学校名称
					schoolDetail.setSchName(searchSch.getSchName());
				//②学校信息
					searchSchDetailList = new ArrayList<SearchSch>();
					//关联证书编号
					searchLicense = schLicLisMap.get(searchSch.getSchoolId()+"_1");
					if(searchLicense == null) {
						searchLicense = schLicLisMap.get(searchSch.getSchoolId()+"_0");
					}
					searchSch.setLicNo("-");
					if(searchLicense !=null ) {
						searchSch.setLicNo(searchLicense.getLicNo());
					}
					searchSchDetailList.add(searchSch);
					schoolDetail.setSchList(searchSchDetailList);
					
				//④团餐公司信息
					//关联证书编号
					rmcListTemp = new ArrayList<SearchSupplier>();
					rmcIdListTemp = schRmcMap.get(searchSch.getSchoolId());
					rmcPersonLiceListTemp = new ArrayList<SearchLicense>();
					if(rmcIdListTemp!=null && rmcIdListTemp.size()>0) {
						for(String rmcId : rmcIdListTemp) {
							rmcTemp = rmcMap.get(rmcId);
							if(rmcTemp == null) {
								continue;
							}
							searchLicense = supplierLicLisMap.get(rmcId+"_1");
							if(searchLicense == null) {
								searchLicense = supplierLicLisMap.get(rmcId+"_0");
							}
							rmcTemp.setFblNo("-");
							rmcTemp.setFblExpireDate("-");
							if(searchLicense !=null ) {
								rmcTemp.setFblNo(searchLicense.getLicNo());
								rmcTemp.setFblExpireDate(searchLicense.getLicEndDate());
							}
							
							//统一社会信用代码证
							rmcTemp.setUscc("-");
							searchLicense = supplierLicLisMap.get(rmcId+"_5");
							if(searchLicense != null) {
								rmcTemp.setUscc(searchLicense.getLicNo());
							}
							rmcListTemp.add(rmcTemp);
							
							//③证照信息（团餐公司人员证书）
							logger.info("*************************searchSch.getSchoolId()+\"_\"+rmcId:"+searchSch.getSchoolId()+"_"+rmcId);
							logger.info("*************************rmcPersonMap.get(searchSch.getSchoolId()+\"_\"+rmcId):"+rmcPersonMap.get(searchSch.getSchoolId()+"_"+rmcId));
							if(rmcPersonMap!=null && rmcPersonMap.size()>0 
									&& rmcPersonMap.get(searchSch.getSchoolId()+"_"+rmcId) !=null && rmcPersonMap.get(searchSch.getSchoolId()+"_"+rmcId).size()>0) {
								for( SearchLicense searchLicenseObj:rmcPersonMap.get(searchSch.getSchoolId()+"_"+rmcId)) {
									if(searchLicenseObj ==null ) {
										continue;
									}
									//团餐公司名称
									searchLicenseObj.setSupplierName(rmcTemp.getSupplierName());
									rmcPersonLiceListTemp.add(searchLicenseObj);
									if(searchLicenseObj.getLicType() !=null) {
										if(searchLicenseObj.getLicType() == 20) {
											healthLicenseCount = healthLicenseCount +1;
										}else if(searchLicenseObj.getLicType() == 22) {
											aoneLicenseCount = aoneLicenseCount + 1;
										}else if(searchLicenseObj.getLicType() == 23) {
											blicenseCount = blicenseCount +1;
											
										}else if(searchLicenseObj.getLicType() == 24) {
											clicenseCount = clicenseCount + 1;
										}else if(searchLicenseObj.getLicType() == 25) {
											atwoLicenseCount = atwoLicenseCount + 1;
										}
									}
									
									if(searchLicense !=null ) {
										//schoolIdAndRmcId = schSchoolSupplierIdMap.get(searchLicense.getSchoolSupplierId());
										if(StringUtils.isNotEmpty(schoolIdAndRmcId)) {
											schoolId = schoolIdAndRmcId.split("_")[0];
										}
									}
								}
								
							}
							
						}
					}
					
					//排序(证书)
			    	sortList.Sort(rmcPersonLiceListTemp, methods, sorts, dataTypes);
			    	
			    	//排序（团餐公司）
			    	rmcSortList.Sort(rmcListTemp, rmcMethods, sorts, dataTypes);
			    	
					//20:员工健康证
			    	schoolDetail.setHealthLicenseCount(healthLicenseCount);
					//22:A1证  
					schoolDetail.setAoneLicenseCount(aoneLicenseCount);
					//23:B证  
					schoolDetail.setBlicenseCount(blicenseCount);
					//24:C证 
					schoolDetail.setClicenseCount(clicenseCount);
					//25:A2证
					schoolDetail.setAtwoLicenseCount(atwoLicenseCount);
					
					schoolDetail.setLicenseList(rmcPersonLiceListTemp);
					schoolDetail.setRmcList(rmcListTemp);
				//⑤供应商信息
					supplierListTemp = new ArrayList<SearchSupplier>();
					rmcIdListTemp = schSupplierIdMap.get(searchSch.getSchoolId());
					if(rmcIdListTemp !=null && rmcIdListTemp.size() >0) {
						for(String supplierId : rmcIdListTemp) {
							supplierTemp = supplierMap.get(supplierId);
							if(supplierTemp==null) {
								continue;
							}
							searchLicense = supplierLicLisMap.get(supplierId+"_1");
							if(searchLicense == null) {
								searchLicense = supplierLicLisMap.get(supplierId+"_0");
							}
							supplierTemp.setFblNo("-");
							supplierTemp.setFblExpireDate("-");
							if(searchLicense !=null ) {
								supplierTemp.setFblNo(searchLicense.getLicNo());
								supplierTemp.setFblExpireDate(searchLicense.getLicEndDate());
							}
							
							//统一社会信用代码证
							supplierTemp.setUscc("-");
							searchLicense = supplierLicLisMap.get(supplierId+"_5");
							if(searchLicense != null) {
								supplierTemp.setUscc(searchLicense.getLicNo());
							}
							//食品生产许可证
							supplierTemp.setFplNo("-");
							searchLicense = supplierLicLisMap.get(supplierId+"_3");
							if(searchLicense != null) {
								supplierTemp.setFplNo(searchLicense.getLicNo());
							}
							
							//食品流通许可证
							supplierTemp.setFcpNo("-");
							searchLicense = supplierLicLisMap.get(supplierId+"_2");
							if(searchLicense != null) {
								supplierTemp.setFcpNo(searchLicense.getLicNo());
							}
							supplierListTemp.add(supplierTemp);
						}
					}
					
			    	//排序（供应商公司）
			    	rmcSortList.Sort(supplierListTemp, rmcMethods, sorts, dataTypes);
			    	
					schoolDetail.setSupplierList(supplierListTemp);
				//⑥配送单明细
					supplierMatMap = schSupplierMap.get(searchSch.getSchoolId());
					supplyMatSupList = new ArrayList<SearchSupplyMatSup>();
					if(supplierMatMap !=null && supplierMatMap.size()>0) {
						for(Map.Entry<String,List<CaMatSupDets>> entry : supplierMatMap.entrySet()) {
							searchSupplyMatSupTemp = new SearchSupplyMatSup();
							searchSupplyMatSupTemp.setSupplierName(entry.getKey().substring(entry.getKey().indexOf("_")+1,entry.getKey().length()));
							if(entry.getValue() !=null ) {
								searchSupplyMatSupTemp.setMatSupCount(entry.getValue().size());
								searchSupplyMatSupTemp.setCaMatSupDets(entry.getValue());
							}
							supplyMatSupList.add(searchSupplyMatSupTemp);
						}
					}
					
			    	//排序（团餐公司）
			    	matSortList.Sort(supplyMatSupList, matSortListMethods, sorts, dataTypes);
			    	
					schoolDetail.setSupplyMatSupList(supplyMatSupList);
					schoolDetails.add(schoolDetail);
			}
		}
		
		
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		
		// 分页
		/*pageBean = new PageBean<SearchByMaterialDetail>(schoolDetails, curPageNum, pageSize);
		pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setSchoolDetails(pageBean.getCurPageData());*/
		
		
		pageInfo = new PageInfo();
		pageInfo.setPageTotal(schoolDetails==null?0:schoolDetails.size());
		pageInfo.setCurPageNum(1);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setSchoolDetails(schoolDetails);
		
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bslDto;
	}

	
	// 基础数据学校列表模型函数
	public SearchByMaterialDTO appModFunc(String token,Map<String,String> inputMap,Db1Service db1Service,Db2Service db2Service,DbHiveService dbHiveService) {
		SearchByMaterialDTO bslDto = null;
		
		String province = inputMap.get("province")==null?null:inputMap.get("province").toString();
		String distName = inputMap.get("distName")==null?null:inputMap.get("distName").toString();
		String prefCity = inputMap.get("prefCity")==null?null:inputMap.get("prefCity").toString();
		
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
					bslDto = searchSchList(distIdorSCName, inputMap,db1Service, dbHiveService);
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
					bslDto = searchSchList(distIdorSCName, inputMap,db1Service, dbHiveService);	
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
