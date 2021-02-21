package com.tfit.BdBiProcSrvShEduOmc.appmod.is;

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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdFoodSafetyGradeDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserDataPermInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//信息共享食品安全等级详情列表应用模型
public class IsFsgDetsAppMod {
	private static final Logger logger = LogManager.getLogger(IsFsgDetsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] ppName_Array = {"上海市徐汇区向阳小学", "上海市徐汇区世界小学"};
	String[] schType_Array = {"小学", "小学"};
	String[] dinnerMod_Array = {"自营", "自营"};
	String[] rmcName_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司"};
	String[] licNo_Array = {"JY31026558588885", "JY31026558588857"};
	String[] compAddress_Array = {"上海市徐汇区襄阳南路388弄15号", "上海市徐汇区武康路280弄2号"};
	String[] lastInspDate_Array = {"2018-09-06", "2018-09-06"};
	int[] grade_Array = {0, 1};
	
	//模拟数据函数
	private IsFsgDetsDTO SimuDataFunc() {
		IsFsgDetsDTO ifdDto = new IsFsgDetsDTO();
		//时戳
		ifdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//信息共享食品安全等级详情列表模拟数据
		List<IsFsgDets> isFsgDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < ppName_Array.length; i++) {
			IsFsgDets ifd = new IsFsgDets();
			ifd.setPpName(ppName_Array[i]);
			ifd.setSchType(schType_Array[i]);
			ifd.setDinnerMod(dinnerMod_Array[i]);
			ifd.setRmcName(rmcName_Array[i]);
			ifd.setLicNo(licNo_Array[i]);
			ifd.setCompAddress(compAddress_Array[i]);
			ifd.setLastInspDate(lastInspDate_Array[i]);
			ifd.setGrade(grade_Array[i]);
			isFsgDets.add(ifd);
		}
		//设置数据
		ifdDto.setIsFsgDets(isFsgDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = ppName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		ifdDto.setPageInfo(pageInfo);
		//消息ID
		ifdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ifdDto;
	}
	
	// 信息共享食品安全等级详情列表函数按所在区
	private IsFsgDetsDTO isFsgDetsByLocality(String distIdorSCName, List<TEduDistrictDo> tedList, String ppName, String licNo, int grade,
			String distNames, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		IsFsgDetsDTO ifdDto = new IsFsgDetsDTO();
		List<IsFsgDets> isFsgDets = new ArrayList<>();
		List<TEduBdFoodSafetyGradeDo> tebfsgDoList = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 3);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getSchoolName(), i);
		}
		//学校id和供应商id
    	Map<String, String> SchIdTosupIdMap = new HashMap<>();
    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
    	if(tessDoList != null) {
    		for(int i = 0; i < tessDoList.size(); i++) {
    			SchIdTosupIdMap.put(tessDoList.get(i).getSchoolId(), tessDoList.get(i).getSupplierId());
    		}
    	}
		//团餐公司id和团餐公司名称
		Map<String, String> RmcIdToNameMap = new HashMap<>();
		List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
		if(tpsDoList != null) {
			for(int i = 0; i < tpsDoList.size(); i++) {
				RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
			}
		}		
		//按区域获取数据
		if(distIdorSCName == null) {
			//获取所有食品安全等级记录
			tebfsgDoList = db2Service.getAllTEduBdFoodSafetyGradeDos();
		}
		else {
			String distName = AppModConfig.distIdToNameMap.get(distIdorSCName);
			//获取食品安全等级记录以区域名称
			tebfsgDoList = db2Service.getTEduBdFoodSafetyGradeDoByDistName(distName);
		}
		if(tebfsgDoList != null && tesDoList != null) {
			for(int i = 0; i < tebfsgDoList.size(); i++) {
				TEduBdFoodSafetyGradeDo tebfsgDo = tebfsgDoList.get(i);
				String distId = AppModConfig.distNameToIdMap.get(tebfsgDo.getDistName());
				if(distId != null) {
					IsFsgDets ifd = new IsFsgDets();
					TEduSchoolDo tesDo = null;
					if(schIdMap.containsKey(tebfsgDo.getPpName())) {
						int j = schIdMap.get(tebfsgDo.getPpName());
						tesDo = tesDoList.get(j);
					}
					//区域名称
					ifd.setDistName(tebfsgDo.getDistName());
					//项目点名称
					ifd.setPpName(tebfsgDo.getPpName());
					//学制
					ifd.setSchType("-");
					if(tesDo != null) {
						ifd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
    				}
					//主管部门					
					ifd.setCompDep("其他");
					if(tesDo != null) {
						int curSubLevel = 0;
						int curCompDep = 0;
						if(tesDo.getDepartmentMasterId() != null) {
							curSubLevel = Integer.parseInt(tesDo.getDepartmentMasterId());
						}
						if(curSubLevel == 0) {      //其他
							if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
								curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
							}
							ifd.setCompDep(AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep)));
						}
						else if(curSubLevel ==1) {      //部级   
							if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
								curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
							}
							ifd.setCompDep(AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep)));
						}
						else if(curSubLevel == 2) {      //市级
							if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
								curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
							}
							ifd.setCompDep(AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep)));
						}
						else if(curSubLevel == 3) {      //区级
							if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
								String orgName = AppModConfig.compDepIdToNameMap3bd.get(tesDo.getDepartmentSlaveId());
								if(orgName != null) {
									curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
								}
							}
							ifd.setCompDep(AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep)));
						}
					}
					//供餐模式
					ifd.setDinnerMod("-");
					if(tesDo != null) {
						ifd.setDinnerMod(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
					}
					//团餐公司
					ifd.setRmcName("-");
					if(tesDo != null) {
						if(SchIdTosupIdMap.containsKey(tesDo.getId())) {
							String supId = SchIdTosupIdMap.get(tesDo.getId());
							if(RmcIdToNameMap.containsKey(supId)) {
								ifd.setRmcName(RmcIdToNameMap.get(supId));
							}
						}	
					}
					//证件号码
					ifd.setLicNo(tebfsgDo.getLicNo());
					//单位地址
					ifd.setCompAddress(tebfsgDo.getPpAddress());
					//最近检查日期
					ifd.setLastInspDate(tebfsgDo.getInspDate());
					//安全等级
					if(AppModConfig.cantSaftGradNameToIdMap.containsKey(tebfsgDo.getLevelName())) {
						ifd.setGrade(AppModConfig.cantSaftGradNameToIdMap.get(tebfsgDo.getLevelName()));
					}
					else {
						ifd.setGrade(11);
					}
					//条件判断
					boolean isAdd = true;
					int[] flIdxs = new int[3];
					//项目点名称（判断索引0）
					if(ppName != null) {
						if(ifd.getPpName().indexOf(ppName) == -1)
							flIdxs[0] = -1;
					}
					//经营许可证（判断索引1）
					if(licNo != null) {
						if(ifd.getLicNo().indexOf(licNo) == -1)
							flIdxs[1] = -1;
					}
					//等级（判断索引2）
					if(grade != -1) {
						if(ifd.getGrade() != grade)
							flIdxs[1] = -1;
					}
					//总体条件判断
					for(int j = 0; j < flIdxs.length; j++) {
						if(flIdxs[j] == -1) {
							isAdd = false;
							break;
						}
					}
					//是否满足条件
					if(isAdd)
						isFsgDets.add(ifd);
				}
			}
		}
		//时戳
    	ifdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<IsFsgDets> pageBean = new PageBean<IsFsgDets>(isFsgDets, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	ifdDto.setPageInfo(pageInfo);
    	// 设置数据
    	ifdDto.setIsFsgDets(pageBean.getCurPageData());
    	// 消息ID
    	ifdDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();

		return ifdDto;
	}
	
	// 信息共享食品安全等级详情列表函数
	private IsFsgDetsDTO isFsgDets(String distIdorSCName, List<TEduDistrictDo> tedList, 
			int schSelMode, int subLevel, int compDep, String subDistName, String ppName, String licNo, int grade,
			String subLevels,String compDeps,String distNames, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		IsFsgDetsDTO ifdDto = null;
		//筛选学校模式
		if(schSelMode == 0) {    //按主管部门
			ifdDto = isFsgDetsByLocality(distIdorSCName, tedList, ppName, licNo, grade, distNames, db1Service, db2Service, saasService);
		}
		else if(schSelMode == 1) {  //按所在地
			ifdDto = isFsgDetsByLocality(distIdorSCName, tedList, ppName, licNo, grade, distNames, db1Service, db2Service, saasService);			
		}    	

		return ifdDto;
	}
	
	// 信息共享食品安全等级详情列表模型函数
	public IsFsgDetsDTO appModFunc(String token, String schSelMode, String subLevel, String compDep, String subDistName, String distName, String prefCity, String province, String ppName, String licNo, String grade, String subLevels,String compDeps,String distNames, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		IsFsgDetsDTO ifdDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			//学校筛选方式，0:按主管部门，1:按所在地
			int curSchSelMode = 1;
			if(schSelMode != null)
				curSchSelMode = Integer.parseInt(schSelMode);
			//所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
			int curSubLevel = -1;
			if(subLevel != null)
				curSubLevel = Integer.parseInt(subLevel);
			//主管部门，按主管部门有效
			int curCompDep = -1;
			if(compDep != null)
				curCompDep = Integer.parseInt(compDep);	
			//等级，0:良好，1:一般，2:较差
			int curGrade = -1;
			if(grade != null)
				curGrade = Integer.parseInt(grade);	
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				List<TEduDistrictDo> tedList = db1Service.getListByDs1IdName();
				if(tedList != null) {
					// 查找是否存在该区域和省市
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						if (curTdd.getId().compareTo(distName) == 0) {
							bfind = true;
							distIdorSCName = curTdd.getId();
							break;
						}
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					//获取用户数据权限信息
				  	UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();
					// 信息共享食品安全等级详情列表函数
					ifdDto = isFsgDets(distIdorSCName, tedList, curSchSelMode, curSubLevel, curCompDep, subDistName, ppName, licNo, curGrade, subLevels, compDeps, distNames, db1Service, db2Service, saasService);
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tedList = null;
				if (province.compareTo("上海市") == 0) {
					tedList = db1Service.getListByDs1IdName();
					if(tedList != null)
						bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					//获取用户数据权限信息
				  	UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();
					// 信息共享食品安全等级详情列表函数
					ifdDto = isFsgDets(distIdorSCName, tedList, curSchSelMode, curSubLevel, curCompDep, subDistName, ppName, licNo, curGrade, subLevels, compDeps, distNames, db1Service, db2Service, saasService);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			ifdDto = SimuDataFunc();
		}		

		return ifdDto;
	}
}