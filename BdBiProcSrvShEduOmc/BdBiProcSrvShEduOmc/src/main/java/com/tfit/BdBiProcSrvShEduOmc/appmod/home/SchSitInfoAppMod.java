package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchPropClass;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchSitInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchSitInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//学校情况信息应用模型
public class SchSitInfoAppMod {
	private static final Logger logger = LogManager.getLogger(SchSitInfoAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//变量数据初始化
	int supSchNum = 3756;
	//数组数据初始化
	String[] schType_Array = {"公办学校", "民办学校", "外籍人员子女学校", "其他"};
	int[] schNum_Array = {325, 182, 51, 63};
	
	//模拟数据函数
	private SchSitInfoDTO SimuDataFunc() {
		SchSitInfoDTO ssiDto = new SchSitInfoDTO();
		//时戳
		ssiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//学校情况信息模拟数据
		SchSitInfo schSitInf = new SchSitInfo();
		List<SchPropClass> schPropClass = new ArrayList<>();
		//赋值
		for (int i = 0; i < schType_Array.length; i++) {
			SchPropClass spc = new SchPropClass();
			spc.setSchType(schType_Array[i]);
			spc.setSchNum(schNum_Array[i]);
			schPropClass.add(spc);
		}
		schSitInf.setSupSchNum(supSchNum);
		schSitInf.setSchPropClass(schPropClass);
		//设置数据
		ssiDto.setSchSitInfo(schSitInf);
		//消息ID
		ssiDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ssiDto;
	}
	
	// 学校情况信息数量统计函数
	SchSitInfoDTO schSitInfo(String distIdorSCName,String departmentId) {
		SchSitInfoDTO ssiDto = new SchSitInfoDTO();
		SchSitInfo schSitInfo = new SchSitInfo();
		List<SchPropClass> schPropClass = new ArrayList<SchPropClass>();
		SchPropClass spc = null;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		
		String keyVal = "", field = "", fieldPrefix = "", supSchField = "";
		Map<String, String> schoolDataMap = null;
		Map<Integer, Integer> schPropIdxToNumMap = new HashMap<>();
		int i, j = 0, schPropCount = 4, supSchNum = 0;
		if(distIdorSCName == null)
			fieldPrefix = "shanghai-";
		else
			fieldPrefix = "area_" + distIdorSCName + "_";		
		for(i = 0; i < schPropCount; i++) {
			schPropIdxToNumMap.put(i, 0);
		}
		if(distIdorSCName == null)
			supSchField = "shanghai";
		else
			supSchField = "area_" + distIdorSCName;
		//给类型（学校）数量
		schoolDataMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schoolDataMap != null) {
			for(String curKey : schoolDataMap.keySet()) {
				if(curKey.equals(supSchField)) {
					keyVal = schoolDataMap.get(curKey);
					supSchNum = Integer.parseInt(keyVal);
				}
				for(i = 0; i < schPropCount; i++) {
					if(i == 0) {        //公办
						j = 0;
					}
					else                //民办、外籍人员子女学校、其他
						j = i+1;
					field = fieldPrefix + "nature" + "_" + j;
					if(curKey.indexOf(field) == 0) {
						Integer curSchNum = schPropIdxToNumMap.get(i);
						keyVal = schoolDataMap.get(curKey);
						int schNum = Integer.parseInt(keyVal);
						curSchNum += schNum;
						schPropIdxToNumMap.put(i, curSchNum);
					}					
				}
			}
		}
		for(Integer I : schPropIdxToNumMap.keySet()) {
			spc = new SchPropClass();
			spc.setSchNum(schPropIdxToNumMap.get(I));
			spc.setSchType(AppModConfig.schPropIdToNameMap.get(I));
			schPropClass.add(spc);
		}
		schSitInfo.setSupSchNum(supSchNum);
		schSitInfo.setSchPropClass(schPropClass);		
		//设置返回数据
		ssiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		ssiDto.setSchSitInfo(schSitInfo);
		ssiDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ssiDto;
	}
	
	// 学校情况信息模型函数
	public SchSitInfoDTO appModFunc(String token, String distName, String prefCity, String province, int timeType, String date, Db1Service db1Service, Db2Service db2Service) {
		SchSitInfoDTO ssiDto = null;
		if(isRealData) {       //真实数据
			// 省或直辖市
			if (province == null)
				province = "上海市";
			//参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			String departmentId = null;
			//按不同参数形式处理
			if(distName != null && prefCity == null && province != null) {  //按区域，省或直辖市处理
				if(timeType == -1) {  //按与时间无关处理	
					List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
					//查找是否存在该区域和省市
					for(int i = 0; i < tddList.size(); i++) {
						TEduDistrictDo curTdd = tddList.get(i);
						if(curTdd.getName().compareTo(distName) == 0) {
							bfind = true;
							distIdorSCName = curTdd.getId();
							break;
						}
					}
					//存在则获取数据
					if(bfind) {
						if(departmentId == null)
							departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
						ssiDto = schSitInfo(distIdorSCName,departmentId);
					}
				}
			}
			else if(distName == null && prefCity == null && province != null) {  //按省或直辖市处理
				if(province.compareTo("上海市") == 0) {
					bfind = true;
					distIdorSCName = null;
				}
				if(bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					ssiDto = schSitInfo(distIdorSCName,departmentId);
				}
			}
			else if(distName != null && prefCity != null && province != null) {  //按区域，地级市，省或直辖市处理
						
			}
			else if(distName == null && prefCity != null && province != null) {  //地级市，省或直辖市处理
						
			}
			else {
				logger.info("访问接口参数非法！");
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			ssiDto = SimuDataFunc();
		}		

		return ssiDto;
	}
}
