package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//基础数据学校列表应用模型
public class BdSchListAppMod {
	private static final Logger logger = LogManager.getLogger(BdSchListAppMod.class.getName());
	
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
	//食品经营许可证、餐饮服务许可证相关信息变量名称关键字
	String[] lictypes = {"slictype", "clictype"};
	String[] licpics = {"slicpic", "clicpic"};
	String[] licjobs = {"slicjob", "clicjob"};
	String[] licnos = {"slicno", "clicno"};
	String[] operations = {"soperation", "coperation"};
	String[] licdates = {"slicdate", "clicdate"};
	String[] enddates = {"senddate", "cenddate"};
	
	//数组数据初始化	
	String[] schStdName_Array = {"上海市徐汇区向阳小学", "上海市徐汇区世界小学"};
	String[] schGenBraFlag_Array = {"-", "总校"};
	int[] braCampusNum_Array = {0, 0};
	String[] relGenSchName_Array = {"-", "-"};
	String[] distName_Array = {"徐汇区", "徐汇区"};
	String[] detailAddr_Array = {"上海市徐汇区襄阳南路388弄15号", "上海市徐汇区武康路280弄2号"};
	String[] uscc_Array = {"310020156859102ZX", "410020156859102ZX"};
	String[] schType_Array = {"小学", "小学"};
	String[] schProp_Array = {"公办", "公办"};
	String[] subLevel_Array = {"区级", "区级"};
	String[] compDep_Array = {"徐汇区教育局", "徐汇区教育局"};
	String[] subDistName_Array = {"徐汇区", "徐汇区"};
	String[] remark_Array = {"-", "-"};
	String[] fblMb_Array = {"外包", "外包"};
	String[] optMode_Array = {"外包", "外包"};
	int[] studentNum_Array = {988, 988};
	int[] teacherNum_Array = {80, 80};
	String[] legalRep_Array = {"-", "-"};
	String[] lrMobilePhone_Array = {"-", "-"};
	String[] lrFixPhone_Array = {"-", "-"};
	String[] depHeadName_Array = {"-", "-"};
	String[] dhnMobilePhone_Array = {"-", "-"};
	String[] dhnFixPhone_Array = {"-", "-"};
	String[] dhnFax_Array = {"-", "-"};
	String[] dhnEmail_Array = {"-", "-"};
	String[] projContact_Array = {"-", "-"};
	String[] pcMobilePhone_Array = {"-", "-"};
	String[] pcFixPhone_Array = {"-", "-"};
	int[] isSetsem_Array = {1, 1};
	String[] licName_Array = {"食品经营许可证", "食品经营许可证"};
	String[] schPic_Array = {"-", "-"};
	String[] optUnit_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司"};
	String[] licNo_Array = {"-", "-"};
	String[] licIssueAuth_Array = {"-", "-"};
	String[] licIssueDate_Array = {"-", "-"};
	String[] validDate_Array = {"-", "-"};
	String[] relCompName_Array = {"-", "-"};
	
	//模拟数据函数
	private BdSchListDTO SimuDataFunc() {
		BdSchListDTO bslDto = new BdSchListDTO();
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据学校列表模拟数据
		List<BdSchList> warnSchLics = new ArrayList<>();
		//赋值
		for (int i = 0; i < Math.max(schStdName_Array.length, actPageSize); i++) {
			BdSchList bsl = new BdSchList();			
			bsl.setSchStdName(schStdName_Array[i]);
			bsl.setSchGenBraFlag(schGenBraFlag_Array[i]);
			bsl.setBraCampusNum(braCampusNum_Array[i]);
			bsl.setRelGenSchName(relGenSchName_Array[i]);
			bsl.setDistName(distName_Array[i]);;
			bsl.setDetailAddr(detailAddr_Array[i]);
			bsl.setUscc(uscc_Array[i]);
			bsl.setSchType(schType_Array[i]);
			bsl.setSchProp(schProp_Array[i]);
			bsl.setSubLevel(subLevel_Array[i]);
			bsl.setCompDep(compDep_Array[i]);
			bsl.setSubDistName(subDistName_Array[i]);
			bsl.setRemark(remark_Array[i]);
			bsl.setFblMb(fblMb_Array[i]);
			bsl.setOptMode(optMode_Array[i]);
			bsl.setStudentNum(studentNum_Array[i]);
			bsl.setTeacherNum(teacherNum_Array[i]);
			bsl.setLegalRep(legalRep_Array[i]);
			bsl.setLrMobilePhone(lrMobilePhone_Array[i]);
			bsl.setLrFixPhone(lrFixPhone_Array[i]);
			bsl.setDepHeadName(depHeadName_Array[i]);
			bsl.setDhnMobilePhone(dhnMobilePhone_Array[i]);
			bsl.setDhnFixPhone(dhnFixPhone_Array[i]);
			bsl.setDhnFax(dhnFax_Array[i]);
			bsl.setDhnEmail(dhnEmail_Array[i]);
			bsl.setProjContact(projContact_Array[i]);
			bsl.setPcMobilePhone(pcMobilePhone_Array[i]);
			bsl.setPcFixPhone(pcFixPhone_Array[i]);
			bsl.setIsSetsem(isSetsem_Array[i]);
			bsl.setLicName(licName_Array[i]);
			bsl.setSchPic(schPic_Array[i]);
			bsl.setOptUnit(optUnit_Array[i]);
			bsl.setLicNo(licNo_Array[i]);
			bsl.setLicIssueAuth(licIssueAuth_Array[i]);
			bsl.setLicIssueDate(licIssueDate_Array[i]);
			bsl.setValidDate(validDate_Array[i]);
			bsl.setRelCompName(relCompName_Array[i]);			
			warnSchLics.add(bsl);
		}
		//设置数据
		bslDto.setBdSchList(warnSchLics);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		if(actPageSize == 0)
			pageTotal = schStdName_Array.length;
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
	BdSchListDTO bdSchList(String distIdorSCName, List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, 
			String schName, int schType, int schProp, int isSetsem, int optMode, String relCompName, int schGenBraFlag, 
			String relGenSchName, int subLevel, String compDep, int fblMb, String uscc,String departmentId) {
		BdSchListDTO bslDto = new BdSchListDTO();
		List<BdSchList> bdSchList = new ArrayList<>();
		Map<String, String> schoolDetailMap = new HashMap<>();
		int i, j;
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
		//所有总校id
		Map<String, String> genSchIdToNameMap = new HashMap<>();
		List<TEduSchoolDo> genSchList = db1Service.getGenSchIdNameListByDs1(distIdorSCName);
		for(i = 0; i < genSchList.size(); i++) {
			genSchIdToNameMap.put(genSchList.get(i).getId(), genSchList.get(i).getSchoolName());
		}
		//学校id和供应商id
    	Map<String, String> SchIdTosupIdMap = new HashMap<>();
    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
    	if(tessDoList != null) {
    		for(i = 0; i < tessDoList.size(); i++) {
    			SchIdTosupIdMap.put(tessDoList.get(i).getSchoolId(), tessDoList.get(i).getSupplierId());
    		}
    	}
		//团餐公司id和团餐公司名称
		Map<String, String> RmcIdToNameMap = new HashMap<>();
		List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
		if(tpsDoList != null) {
			for(i = 0; i < tpsDoList.size(); i++) {
				RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
			}
		}
		
		//管理部门
		//获取用户信息，用于匹配部门的编辑人
		Map<String,DepartmentObj> departmentMap = new HashMap<String,DepartmentObj>();
		List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
		if(deparmentList != null) {
			departmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
		}
		// 各区基础数据学校详情
    	key = "schoolDetail";
    	schoolDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
    	if (schoolDetailMap != null) {
    		for (String curKey : schoolDetailMap.keySet()) {
    			keyVal = schoolDetailMap.get(curKey);
    			// 基础数据学校列表
    			String[] keyVals = keyVal.split(";");
    			if(keyVals.length >= 82) {
    				BdSchList bsl = new BdSchList();
    				//学校信息（项目点）
    				TEduSchoolDo tesDo = null;
    				i = AppModConfig.getVarValIndex(keyVals, "id");
    				if(i != -1) {
    					bsl.setId(keyVals[i]);
    					if(schIdMap.containsKey(keyVals[i])) {
    						j = schIdMap.get(keyVals[i]);
    						tesDo = tesDoList.get(j-1);
    					}
    				}
    				if(tesDo == null)
    					continue;
    				//学校规范名称
    				i = AppModConfig.getVarValIndex(keyVals, "schoolname");
    				bsl.setSchStdName("-");
    				if(i != -1) {
    					queryVarIdxs[0] = i;
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setSchStdName(keyVals[i]);
    				}
    				//总校/分校
    				i = AppModConfig.getVarValIndex(keyVals, "isbranchschool");
    				int curSchGenBraFlag = 0;
    				bsl.setSchGenBraFlag("-");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						if(keyVals[i].equalsIgnoreCase("0")) {
    							curSchGenBraFlag = 1;
    							bsl.setSchGenBraFlag("总校");
    						}
    						else if(keyVals[i].equalsIgnoreCase("1")) {
    							curSchGenBraFlag = 2;
    							bsl.setSchGenBraFlag("分校");
    						}
    					}
    				}
    				//分校数量
    				int curBraCampusNum = 0;
    				bsl.setBraCampusNum(curBraCampusNum);
    				if(curSchGenBraFlag == 1) {
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
    						bsl.setBraCampusNum(curBraCampusNum);
    				}
    				//关联总校
    				bsl.setRelGenSchName("-");
    				i = AppModConfig.getVarValIndex(keyVals, "parentid");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						if(genSchIdToNameMap.containsKey(keyVals[i])) {
    							bsl.setRelGenSchName(genSchIdToNameMap.get(keyVals[i]));
    						}
    					}
    				}
    				//所在区
    				bsl.setDistName("-");
    				i = AppModConfig.getVarValIndex(keyVals, "area");
    				if(i != -1) {
    					queryVarIdxs[1] = i;
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setDistName(AppModConfig.distIdToNameMap.get(keyVals[i]));
    				}
    				//地址
    				bsl.setDetailAddr("-");
    				i = AppModConfig.getVarValIndex(keyVals, "address");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setDetailAddr(keyVals[i]);
    				}
    				//统一社会信用代码
    				bsl.setUscc("-");
    				i = AppModConfig.getVarValIndex(keyVals, "socialcreditcode");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setUscc(keyVals[i]);
    				}
    				//学校学制
    				bsl.setSchType("-");
    				i = AppModConfig.getVarValIndex(keyVals, "level");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						int curSchType = Integer.parseInt(keyVals[i]);
    						bsl.setSchType(AppModConfig.schTypeIdToNameMap.get(curSchType));
    					}
    				}
    				//学校性质
    				bsl.setSchProp("-");
    				if(CommonUtil.isNotEmpty(tesDo.getSchoolNature())) {
    					bsl.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
    				}
    				//所属
    				/*int curSubLevel = 0;
    				bsl.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
    				i = AppModConfig.getVarValIndex(keyVals, "departmentmasterid");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						curSubLevel = Integer.parseInt(keyVals[i]);
    						bsl.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
    					}
    				}*/
    				
    				//2019-09-21改为从mysql中获取
    				int curSubLevel = 0;
    				bsl.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
    				String departmentMasterId = tesDo.getDepartmentMasterId();
    				if(CommonUtil.isNotEmpty(departmentMasterId)) {
						curSubLevel = Integer.parseInt(departmentMasterId);
						bsl.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
					}
    				
    				//主管部门
    				/*bsl.setCompDep("其他");
    				i = AppModConfig.getVarValIndex(keyVals, "departmentslaveid");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						if(curSubLevel == 0) {      //其他     							
    							bsl.setCompDep(AppModConfig.compDepIdToNameMap0.get(keyVals[i]));
    						}
    						else if(curSubLevel == 1) {      //部级   
    							bsl.setCompDep(AppModConfig.compDepIdToNameMap1.get(keyVals[i]));
    						}
    						else if(curSubLevel == 2) {      //市级
    							bsl.setCompDep(AppModConfig.compDepIdToNameMap2.get(keyVals[i]));
    						}
    						else if(curSubLevel == 3) {      //区级
    							bsl.setCompDep(AppModConfig.compDepIdToNameMap3bd.get(keyVals[i]));
    						}
    					}
    				}*/
    				
    				//2019-09-21改为从mysql中获取
    				bsl.setCompDep("-");
    				String departmentSlaveId = tesDo.getDepartmentSlaveId();
					if(CommonUtil.isNotEmpty(departmentSlaveId)) {
						if(curSubLevel == 0) {      //其他     							
							bsl.setCompDep(AppModConfig.compDepIdToNameMap0.get(departmentSlaveId));
						}
						else if(curSubLevel == 1) {      //部级   
							bsl.setCompDep(AppModConfig.compDepIdToNameMap1.get(departmentSlaveId));
						}
						else if(curSubLevel == 2) {      //市级
							bsl.setCompDep(AppModConfig.compDepIdToNameMap2.get(departmentSlaveId));
						}
						else if(curSubLevel == 3) {      //区级
							bsl.setCompDep(AppModConfig.compDepIdToNameMap3bd.get(departmentSlaveId));
						}
					}
					
					//管理部门编号
    				bsl.setDepartmentId(tesDo.getDepartmentId());
    				//管理部门名称
    				bsl.setDepartmentName(departmentMap.get(tesDo.getDepartmentId())==null?"":departmentMap.get(tesDo.getDepartmentId()).getDepartmentName());
					
    				//所属区
    				bsl.setSubDistName("-");
    				if(tesDo.getSchoolAreaId() != null) {
    					bsl.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));
    				}
    				//备注说明
    				bsl.setRemark("-");
    				i = AppModConfig.getVarValIndex(keyVals, "");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setRemark(keyVals[i]);
    				}
    				//食品经营许可证主体
    				bsl.setFblMb("-");
    				if(tesDo.getLicenseMainType() != null) {
    					int curFblMb = Integer.parseInt(tesDo.getLicenseMainType());
    					bsl.setFblMb(AppModConfig.fblMbIdToNameMap.get(curFblMb));
    				}
    				//供餐模式
    				bsl.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
    				//学生人数
    				bsl.setStudentNum(0);
    				i = AppModConfig.getVarValIndex(keyVals, "studentsamount");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setStudentNum(Integer.parseInt(keyVals[i]));
    				}
    				//教职工人数
    				bsl.setTeacherNum(0);
    				i = AppModConfig.getVarValIndex(keyVals, "staffamount");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setTeacherNum(Integer.parseInt(keyVals[i]));
    				}
    				//法定代表人
    				bsl.setLegalRep("-");
    				i = AppModConfig.getVarValIndex(keyVals, "corporation");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setLegalRep(keyVals[i]);
    				}
    				//手机
    				bsl.setLrMobilePhone("-");
    				i = AppModConfig.getVarValIndex(keyVals, "corporationtelephone");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setLrMobilePhone(keyVals[i]);
    				}
    				//座机
    				bsl.setLrFixPhone("-");
    				i = AppModConfig.getVarValIndex(keyVals, "corporationway");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setLrFixPhone(keyVals[i]);
    				}
    				//部门负责人姓名
    				bsl.setDepHeadName("-");
    				i = AppModConfig.getVarValIndex(keyVals, "departmenthead");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setDepHeadName(keyVals[i]);
    				}
    				//手机
    				bsl.setDhnMobilePhone("-");
    				i = AppModConfig.getVarValIndex(keyVals, "departmentmobilephone");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setDhnMobilePhone(keyVals[i]);
    				}
    				//座机
    				bsl.setDhnFixPhone("-");
    				i = AppModConfig.getVarValIndex(keyVals, "departmenttelephone");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setDhnFixPhone(keyVals[i]);
    				}
    				//传真
    				bsl.setDhnFax("-");
    				i = AppModConfig.getVarValIndex(keyVals, "departmentfax");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						bsl.setDhnFax(keyVals[i]);
    				}
    				//电子邮件
    				bsl.setDhnEmail("-");
    				i = AppModConfig.getVarValIndex(keyVals, "departmentemail");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setDhnEmail(keyVals[i]);
    				}
    				//项目联系人
    				bsl.setProjContact("-");
    				i = AppModConfig.getVarValIndex(keyVals, "foodsafetypersion");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setProjContact(keyVals[i]);
    				}
    				//手机
    				bsl.setPcMobilePhone("-");
    				i = AppModConfig.getVarValIndex(keyVals, "foodsafetymobilephone");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setPcMobilePhone(keyVals[i]);
    				}
    				//座机
    				bsl.setPcFixPhone("-");
    				i = AppModConfig.getVarValIndex(keyVals, "foodsafetytelephone");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setPcFixPhone(keyVals[i]);
    				}
    				//学期设置
    				bsl.setIsSetsem(0);
    				i = AppModConfig.getVarValIndex(keyVals, "gongcan");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setIsSetsem(Integer.parseInt(keyVals[i]));
    				}
    				//获取证件索引，0对应食品经营许可证、1对应餐饮服务许可证
    				int licIndex =  getLicIndex(keyVals);
    				//证件名称
    				bsl.setLicName("-");
    				i = AppModConfig.getVarValIndex(keyVals, lictypes[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						if(licIndex == 0)
    							bsl.setLicName("食品经营许可证");
    						else if(licIndex == 1)
    							bsl.setLicName("餐饮服务许可证");
    					}
    				}
    				//图片
    				bsl.setSchPic("-");
    				i = AppModConfig.getVarValIndex(keyVals, licpics[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) {
    						String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
    						bsl.setSchPic(picSrvDn + keyVals[i]);
    					}
    				}
    				//经营单位
    				bsl.setOptUnit("-");
    				i = AppModConfig.getVarValIndex(keyVals, licjobs[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setOptUnit(keyVals[i]);
    				}
    				//许可证号
    				bsl.setLicNo("-");
    				i = AppModConfig.getVarValIndex(keyVals, licnos[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setLicNo(keyVals[i]);
    				}
    				//发证机关
    				bsl.setLicIssueAuth("-");
    				i = AppModConfig.getVarValIndex(keyVals, operations[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setLicIssueAuth(keyVals[i]);
    				}
    				//发证日期
    				bsl.setLicIssueDate("-");
    				i = AppModConfig.getVarValIndex(keyVals, licdates[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setLicIssueDate(keyVals[i]);
    				}
    				//有效日期
    				bsl.setValidDate("-");
    				i = AppModConfig.getVarValIndex(keyVals, enddates[licIndex]);
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						bsl.setValidDate(keyVals[i]);
    				}
    				//关联单位名称
    				bsl.setRelCompName("-");
    				if(SchIdTosupIdMap.containsKey(tesDo.getId())) {
						String supId = SchIdTosupIdMap.get(tesDo.getId());
						if(RmcIdToNameMap.containsKey(supId)) {
							bsl.setRelCompName(RmcIdToNameMap.get(supId));
						}
					}	
    				//条件判断
    				boolean isAdd = true;
    				int[] flIdxs = new int[13];
    				//判断学校名称（判断索引0）
    				if(schName != null && queryVarIdxs[0] != -1) {
    					if(keyVals[queryVarIdxs[0]].indexOf(schName) == -1)
    						flIdxs[0] = -1;
    				}
    				//判断区域（判断索引1）
    				if(distIdorSCName != null && queryVarIdxs[1] != -1) {
    					if(!distIdorSCName.equalsIgnoreCase(keyVals[queryVarIdxs[1]]))
    						flIdxs[1] = -1;
    				}
    				//判断学校类型（学制）（判断索引2）
    				if(schType != -1) {
    					if(AppModConfig.schTypeNameToIdMap.containsKey(bsl.getSchType())) {
    						int curSchType = AppModConfig.schTypeNameToIdMap.get(bsl.getSchType());
    						if(curSchType != schType)
    							flIdxs[2] = -1;
    					}
    					else
    						flIdxs[2] = -1;
    				}
    				//判断学校性质（判断索引3）
    				if(schProp != -1) {
    					if(AppModConfig.schPropNameToIdMap.containsKey(bsl.getSchProp())) {
    						int curSchProp = AppModConfig.schPropNameToIdMap.get(bsl.getSchProp());
    						if(curSchProp != schProp)
    							flIdxs[3] = -1;
    					}
    					else
    						flIdxs[3] = -1;
    				}
    				//判断学期设置（判断索引4）
    				if(isSetsem != -1) {
    					int curIsSetsem = bsl.getIsSetsem();
    					if(curIsSetsem != isSetsem)
    						flIdxs[4] = -1;
    				}
    				//判断经营模式（供餐模式）（判断索引5）
    				if(optMode != -1) {
    					if(AppModConfig.optModeNameToIdMap.containsKey(bsl.getOptMode())) {
    						int curOptMode = AppModConfig.optModeNameToIdMap.get(bsl.getOptMode());
    						if(curOptMode != optMode)
    							flIdxs[5] = -1;
    					}
    					else
    						flIdxs[5] = -1;
    				}
    				//判断关联单位（判断索引6）
    				if(relCompName != null) {
    					if(RmcIdToNameMap.containsKey(relCompName)) {
    						if(!bsl.getRelCompName().equalsIgnoreCase(RmcIdToNameMap.get(relCompName)))
    							flIdxs[6] = -1;
    					}
    					else  {
    						if(bsl.getRelCompName().indexOf(relCompName) == -1)
    							flIdxs[6] = -1;
    					}
    				}
    				//判断总校/分校（判断索引7）
    				if(schGenBraFlag != -1) {
    					if(AppModConfig.genBraSchNameToIdMap.containsKey(bsl.getSchGenBraFlag())) {
    						int curschGenBraFlag = AppModConfig.genBraSchNameToIdMap.get(bsl.getSchGenBraFlag());
    						if(curschGenBraFlag != schGenBraFlag)
    							flIdxs[7] = -1;
    					}
    					else
    						flIdxs[7] = -1;
    				}
    				//判断关联总校（判断索引8）
    				if(relGenSchName != null) {
    					if(genSchIdToNameMap.containsKey(relGenSchName)) {
    						if(!bsl.getRelGenSchName().equalsIgnoreCase(genSchIdToNameMap.get(relGenSchName)))
    							flIdxs[8] = -1;
    					}
    				}
    				//判断所属（判断索引9）
    				if(subLevel != -1) {
    					if(AppModConfig.subLevelNameToIdMap.containsKey(bsl.getSubLevel())) {
    						curSubLevel = AppModConfig.subLevelNameToIdMap.get(bsl.getSubLevel());
    						if(curSubLevel != subLevel)
    							flIdxs[9] = -1;
    					}
    					else
    						flIdxs[9] = -1;
    				}
    				//判断主管部门（判断索引10）
    				if(compDep != null) {
    					if(subLevel == 0) {    //其他
    						if(AppModConfig.compDepNameToIdMap0.containsKey(bsl.getCompDep())) {
    							String curCompDep = AppModConfig.compDepNameToIdMap0.get(bsl.getCompDep());
    							if(!curCompDep.equalsIgnoreCase(compDep))
    								flIdxs[10] = -1;
    						}
    					}
    					else if(subLevel == 1) {    //部级
    						if(AppModConfig.compDepNameToIdMap1.containsKey(bsl.getCompDep())) {
    							String curCompDep = AppModConfig.compDepNameToIdMap1.get(bsl.getCompDep());
    							if(!curCompDep.equalsIgnoreCase(compDep))
    								flIdxs[10] = -1;
    						}
    					}
    					else if(subLevel == 2) {    //市级
    						if(AppModConfig.compDepNameToIdMap2.containsKey(bsl.getCompDep())) {
    							String curCompDep = AppModConfig.compDepNameToIdMap2.get(bsl.getCompDep());
    							if(!curCompDep.equalsIgnoreCase(compDep))
    								flIdxs[10] = -1;
    						}
    					}
    					else if(subLevel == 3) {    //区级
    						if(AppModConfig.compDepNameToIdMap3.containsKey(bsl.getCompDep())) {
    							String curCompDep = AppModConfig.compDepNameToIdMap3.get(bsl.getCompDep());
    							if(!curCompDep.equalsIgnoreCase(compDep))
    								flIdxs[10] = -1;
    						}
    					}
    					else
    						flIdxs[10] = -1;
    				}
    				//判断证件主体（判断索引11）
    				if(fblMb != -1) {
    					if(AppModConfig.fblMbNameToIdMap.containsKey(bsl.getFblMb())) {
    						int curFblMb = AppModConfig.fblMbNameToIdMap.get(bsl.getFblMb());
    						if(curFblMb != fblMb)
    							flIdxs[11] = -1;
    					}
    				}
    				//判断统一社会信用代码（判断索引12）
    				if(uscc != null) {
    					if(bsl.getUscc().indexOf(uscc) == -1)
    						flIdxs[12] = -1;
    				}
    				//判断统一社会信用代码（判断索引12）
    				if(CommonUtil.isNotEmpty(departmentId)) {
    					if(!departmentId.equals(bsl.getDepartmentId()))
    						flIdxs[12] = -1;
    				}
    				
    				
    				//总体条件判断
    				for(i = 0; i < flIdxs.length; i++) {
    					if(flIdxs[i] == -1) {
    						isAdd = false;
    						break;
    					}
    				}
    				//是否满足条件
    				if(isAdd) {
    					bdSchList.add(bsl);
    					logger.info("**********:"+keyVal);
    				}
    			}
    			else
    				logger.info("基础数据学校："+ curKey + "，格式错误！");
    		}
    	}
    	//排序
    	SortList<BdSchList> sortList = new SortList<BdSchList>();  
    	sortList.Sort(bdSchList, methods, sorts, dataTypes);
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<BdSchList> pageBean = new PageBean<BdSchList>(bdSchList, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setBdSchList(pageBean.getCurPageData());
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bslDto;
	}	
	
	// 基础数据学校列表模型函数
	public BdSchListDTO appModFunc(String token, String schName, String distName, String prefCity, 
			String province, String schType, String schProp, String isSetsem, String optMode, 
			String relCompName, String schGenBraFlag, String relGenSchName, String subLevel, 
			String compDep, String fblMb, String uscc, String departmentId,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		BdSchListDTO bslDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";  		
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//学校性质，0:公办，1:民办，2:其他
			int curSchProp = -1;
			if(schProp != null)
				curSchProp = Integer.parseInt(schProp);
			//学期设置，0:未设置，1:已设置
			int curIsSetsem = -1;
			if(isSetsem != null)
				curIsSetsem = Integer.parseInt(isSetsem);
			//经营模式（供餐模式），0:自行加工，1:现场加工，2:快餐配送，3:食品加工商
			int curOptMode = -1;
			if(optMode != null)
				curOptMode = Integer.parseInt(optMode);
			//总分校标识，0:无，1:总校，2:分校
			int curSchGenBraFlag = -1;
			if(schGenBraFlag != null)
				curSchGenBraFlag = Integer.parseInt(schGenBraFlag);
			//所属，0:区级，1:市级，2:部级，21:其他
			int curSubLevel = -1;
			if(subLevel != null)
				curSubLevel = Integer.parseInt(subLevel);
			//证件主体，0:学校，1:外包
			int curFblMb = -1;
			if(fblMb != null)
				curFblMb = Integer.parseInt(fblMb);
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
					if(CommonUtil.isEmpty(departmentId))
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据学校列表函数
					bslDto = bdSchList(distIdorSCName, tddList, db1Service, saasService, schName, 
							curSchType, curSchProp, curIsSetsem, curOptMode, relCompName, 
							curSchGenBraFlag, relGenSchName, curSubLevel, compDep, curFblMb, uscc,departmentId);		
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tddList = null;
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					tddList = db1Service.getListByDs1IdName();
					distIdorSCName = null;
				}
				if (bfind) {
					if(CommonUtil.isEmpty(departmentId))
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据学校列表函数
					bslDto = bdSchList(distIdorSCName, tddList, db1Service, saasService, schName, curSchType,
							curSchProp, curIsSetsem, curOptMode, relCompName, curSchGenBraFlag, 
							relGenSchName, curSubLevel, compDep, curFblMb, uscc,departmentId);
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
