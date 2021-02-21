package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchTypeNumDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchTypeStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//各类型学校数量统计应用模型
public class SchTypeStatOamAppMod {
	private static final Logger logger = LogManager.getLogger(SchTypeStatOamAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] schType_Array = {"托儿所", "幼儿园", "小学", "初级中学", "高级中学", "完全中学", "九年一贯制", "十二年一贯制", "职业初中", "中等职业学校", "工读学校", "特殊教育学校"};
	int[] schNum_Array = {325, 2135, 1083, 735, 261, 108, 87, 72, 321, 128, 146, 89};
	
	//模拟数据函数
	private SchTypeStatDTO SimuDataFunc() {
		SchTypeStatDTO stsDto = new SchTypeStatDTO();
		//时戳
		stsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//各类型学校数量统计模拟数据
		List<SchTypeNumDTO> schTypeStat = new ArrayList<>();
		//赋值
		for (int i = 0; i < schType_Array.length; i++) {
			SchTypeNumDTO stn = new SchTypeNumDTO();
			stn.setSchType(schType_Array[i]);
			stn.setSchNum(schNum_Array[i]);
			schTypeStat.add(stn);
		}		
		//设置数据
		stsDto.setSchTypeStat(schTypeStat);
		//消息ID
		stsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return stsDto;
	}
	
	// 各类型学校数量统计函数
	SchTypeStatDTO schTypeStat(String distIdorSCName,String departmentId) {
		SchTypeStatDTO stsDto = new SchTypeStatDTO();
		List<SchTypeNumDTO> schTypeStat = new ArrayList<SchTypeNumDTO>();
		SchTypeNumDTO stnDto = null;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		
		String keyVal = "", field = "", fieldPrefix = "";
		Map<String, String> schoolDataMap = null;
		int schTypeCount = 18;
		if(distIdorSCName == null)
			fieldPrefix = "shanghai-";
		else
			fieldPrefix = "area_" + distIdorSCName + "_";
		//给类型（学校）数量
		schoolDataMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schoolDataMap != null) {
			for(int i = 0; i < schTypeCount; i++) {
				stnDto = new SchTypeNumDTO();
				field = fieldPrefix + "level" + "_" + i;
				keyVal = schoolDataMap.get(field);
				int schNum = 0;
				if(keyVal != null)
					schNum = Integer.parseInt(keyVal);
				stnDto.setSchNum(schNum);
				stnDto.setSchType(AppModConfig.schTypeIdToNameMap.get(i));
				schTypeStat.add(stnDto);
			}
		}
		//设置返回数据
		stsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		stsDto.setSchTypeStat(schTypeStat);
		stsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return stsDto;
	}
	
	// 各类型学校数量统计模型函数
	public SchTypeStatDTO appModFunc(String token, String distName, String prefCity, String province, int timeType, Db1Service db1Service, Db2Service db2Service) {
		SchTypeStatDTO stsDto = null;
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
						stsDto = schTypeStat(distIdorSCName,departmentId);
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
					stsDto = schTypeStat(distIdorSCName,departmentId);
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
			stsDto = SimuDataFunc();
		}		

		return stsDto;
	}
}
