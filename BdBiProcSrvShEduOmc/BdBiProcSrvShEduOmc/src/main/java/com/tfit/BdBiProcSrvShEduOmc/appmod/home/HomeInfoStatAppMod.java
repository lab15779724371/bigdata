package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.home.HomeInfoStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.HomeInfoStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//首页信息统计应用模型
public class HomeInfoStatAppMod {
	private static final Logger logger = LogManager.getLogger(HomeInfoStatAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	// 是否为真实数据标识
	private static boolean isRealData = true;
	
	//方法类型索引
	int methodIndex = 1;
	
	//变量数据初始化
	int regSchNum = 4070;
	int dishSchNum = 3459;
	int dinnerCount = 1256000; //800*4070;
	int warnCount = 9647;
	int dishSampleNum = 8*3459;
	int kwRecBuckNum = 124;
	int kwoRecNum = 65;
	int complaintNum = 0;	

	// 模拟数据函数
	private HomeInfoStatDTO SimuDataFunc() {
		HomeInfoStatDTO hisDto = new HomeInfoStatDTO();
		// 时戳
		hisDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 待办事项统计模拟数据
		HomeInfoStat homeInfoStat = new HomeInfoStat();
		homeInfoStat.setRegSchNum(regSchNum);
		homeInfoStat.setDishSchNum(dishSchNum);
		homeInfoStat.setDinnerCount(dinnerCount);
		homeInfoStat.setWarnCount(warnCount);
		homeInfoStat.setDishSampleNum(dishSampleNum);
		homeInfoStat.setKwRecBuckNum(kwRecBuckNum);
		homeInfoStat.setKwoRecNum(kwoRecNum);
		homeInfoStat.setComplaintNum(complaintNum);
		hisDto.setHomeInfoStat(homeInfoStat);
		// 消息ID
		hisDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return hisDto;
	}
	
	// 首页信息统计函数（方案1）
	private HomeInfoStatDTO homeInfoStat_Method0(String distIdorSCName, String date) {
		HomeInfoStatDTO hisDto = new HomeInfoStatDTO();
		HomeInfoStat homeInfoStat = new HomeInfoStat();
		String key = distIdorSCName, keyVal = "", fieldPrefix = distIdorSCName + "_", field = "";
		// 省或直辖市/区学校总数
		keyVal = redisService.getHashByKeyField(SpringConfig.RedisRunEnvIdx, SpringConfig.RedisDBIdx, key, "school_total");
		int regSchNum = 1;
		if (keyVal != null)
			regSchNum = Integer.parseInt(keyVal);
		homeInfoStat.setRegSchNum(regSchNum);
		//排菜学校数量
		key = date;
		Map<String, String> schIdToPlatoonMap = new HashMap<>();
		int dishSchNum = 0;
		key += "_platoon";
		schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisRunEnvIdx, SpringConfig.RedisDBIdx, key);
		if (schIdToPlatoonMap != null) {
			for (String curKey : schIdToPlatoonMap.keySet()) {
				if (curKey.indexOf(fieldPrefix) == 0)
					dishSchNum++;				
			}
		}
		homeInfoStat.setDishSchNum(dishSchNum);
		//用餐人次
		int dinnerCount = dishSchNum;   //待定
		homeInfoStat.setDinnerCount(dinnerCount);
		//预警次数
		int i, j, maxWarTypeNum = 4, maxWarStatusNum = 4, totalWarnNum = 0, curWarnNum = 0;
		if(SpringConfig.spring_profiles_active.compareTo("prod") != 0)    //生产环境时戳格式为xxxx-xx-xx，其他环境为x/x/xxxx
			key = BCDTimeUtil.cvtDateTimeFormat2(date, true);
		for(i = 0; i < maxWarTypeNum; i++) {    //扫描各个预警信息
			for(j = 0; j < maxWarStatusNum; j++) {
				field = distIdorSCName + "-" + (j+1) + "-" + (i+1) + "-warn";
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisRunEnvIdx, SpringConfig.RedisDBIdx, key, field);
				curWarnNum = 0;
				if(keyVal != null)
					curWarnNum = Integer.parseInt(keyVal);
				//预警总数
				totalWarnNum += curWarnNum;
			}
		}
		homeInfoStat.setWarnCount(totalWarnNum);
		// 设置返回数据
		hisDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		hisDto.setHomeInfoStat(homeInfoStat);
		hisDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return hisDto;
	}
	
	// 首页信息统计函数（方案2）
	private HomeInfoStatDTO homeInfoStat_Method1(String departmentId,String distIdorSCName, String date, List<TEduDistrictDo> tedList) {
		HomeInfoStatDTO hisDto = new HomeInfoStatDTO();
		HomeInfoStat homeInfoStat = new HomeInfoStat();
		int i, j;
		String key = distIdorSCName, keyVal = "", field = "", fieldPrefix = "";
		Map<String, String> schoolDataMap = null;
		//域名设置
		if(distIdorSCName == null)
			field = "shanghai";
		else
			field = "area_" + distIdorSCName;
		// 学校数据映射表
		key = "schoolData";
		schoolDataMap =  redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		// 省或直辖市/区学校总数
		int regSchNum = 1;
		if(schoolDataMap != null) {
			if(schoolDataMap.containsKey(field)) {
				keyVal = schoolDataMap.get(field);
				if (keyVal != null)
					regSchNum = Integer.parseInt(keyVal);
			}
		}		
		homeInfoStat.setRegSchNum(regSchNum);
		//排菜学校数量		
		Map<String, String> platoonFeedTotalMap = null;
		int dishSchNum = 0;
		key = date + DataKeyConfig.platoonfeedTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = date + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
		}
		
		
		platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
			
		}
		if(platoonFeedTotalMap != null) {
			for(String curKey : platoonFeedTotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}
					// 区域排菜学校供餐数
					fieldPrefix = curDistId + "_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 3)
						{
							if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("已排菜")) {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									dishSchNum += Integer.parseInt(keyVal);
								}
							}
						}
					}
				}
			}
		}		
		homeInfoStat.setDishSchNum(dishSchNum);
		//用餐人次
		Map<String, String> dishTotalMap = null;
		int dinnerCount = 0;
		key = date + "_dish-total";
		dishTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		field = "dishnumber";
		if(dishTotalMap != null) {
			if(dishTotalMap.containsKey(field)) {
				keyVal = dishTotalMap.get(field);
				if(keyVal != null)
					dinnerCount = Integer.parseInt(keyVal);
			}
		}
		if(distIdorSCName != null)    //区
			dinnerCount = dinnerCount/16;
		homeInfoStat.setDinnerCount(dinnerCount);
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		int distCount = tedList.size();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		//今日预警次数
		Map<String, String> warnTotalMap = null;
		int totalWarnNum = 0, curWarnNum = 0;
		key =date + "_warn-total";
		warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(warnTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
			
		}
		if(warnTotalMap != null) {
			for(String curKey : warnTotalMap.keySet()) {
				String[] curKeys = curKey.split("_");
				if(curKeys.length == 4) {
					i = AppModConfig.getVarValIndex(curKeys, "area");
					if(i != -1) {
						if(!curKeys[i].equalsIgnoreCase("null")) {
							if(distIdorSCName != null) {           //区
								if(curKeys[i].compareTo(distIdorSCName) != 0)
									continue ;
							}
							if(distIdToIdxMap.containsKey(curKeys[i])) {
								keyVal = warnTotalMap.get(curKey);
								curWarnNum = 0;
								if(keyVal != null)
									curWarnNum = Integer.parseInt(keyVal);
								if(curWarnNum < 0)
									curWarnNum = 0;
								j = AppModConfig.getVarValIndex(curKeys, "status");
								if(j != -1) {
									if(curKeys[j].equalsIgnoreCase("1")) {         //未处理预警数
										totalWarnNum += curWarnNum;
									}
									else if(curKeys[j].equalsIgnoreCase("2")) {    //审核中预警数
										totalWarnNum += curWarnNum;
									}
									else if(curKeys[j].equalsIgnoreCase("3")) {    //已驳回预警数
										totalWarnNum += curWarnNum;
									}
									else if(curKeys[j].equalsIgnoreCase("4")) {    //已消除预警数
										totalWarnNum += curWarnNum;
									}
								}
							}
						}
					}
				}
			}
		}
		homeInfoStat.setWarnCount(totalWarnNum);
		//今日菜品留样数
		Map<String, String> gcRetentiondishtotalMap = null;
		int totalDishSampleNum = 0;
		key = date   + DataKeyConfig.gcRetentiondishtotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = date + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
		}
		gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(gcRetentiondishtotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
			
		}
		if(gcRetentiondishtotalMap != null) {
			for(String curKey : gcRetentiondishtotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}
					// 区域菜品留样和未留样数
					fieldPrefix = curDistId + "_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 2)
						{
							if(curKeys[1].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									totalDishSampleNum += Integer.parseInt(keyVal);
								}
							}
						}
					}
				}
			}
		}
		homeInfoStat.setDishSampleNum(totalDishSampleNum);
		//今日餐厨垃圾回收数
		int totalRcNum = 0, totalSchRcNum = 0, totalRmcRcNum = 0;
		//学校垃圾回收数
		Map<String, String> schoolwastetotalMap = null;
		key = date + "_schoolwastetotal";
		schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schoolwastetotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
			
		}
		if(schoolwastetotalMap != null) {
			for(String curKey : schoolwastetotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}
					// 区域回收垃圾桶数
					fieldPrefix = curDistId;
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = schoolwastetotalMap.get(curKey);
						if(keyVal != null) {
							totalSchRcNum += Float.parseFloat(keyVal);
						}
					}
				}
			}
		}
		//团餐公司回收数
		Map<String, String> supplierwastetotalMap = null;
		key = date + "_supplierwastetotal";
		supplierwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(supplierwastetotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
			
		}
		if(supplierwastetotalMap != null) {
			for(String curKey : supplierwastetotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}
					// 区域回收垃圾桶数
					fieldPrefix = curDistId;
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = supplierwastetotalMap.get(curKey);
						if(keyVal != null) {
							totalRmcRcNum += Float.parseFloat(keyVal);
						}
					}
				}
			}
		}
		totalRcNum = totalSchRcNum + totalRmcRcNum;
		homeInfoStat.setKwRecBuckNum(totalRcNum);
		//今日废弃油脂回收数据
		if(distIdorSCName != null)    //区
			homeInfoStat.setKwoRecNum(kwoRecNum/16);
		else
			homeInfoStat.setKwoRecNum(kwoRecNum);             //待定
		//今日投诉举报
		homeInfoStat.setComplaintNum(complaintNum);       //待定
		// 设置返回数据
		hisDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		hisDto.setHomeInfoStat(homeInfoStat);
		hisDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return hisDto;
	}
	
	// 首页信息统计模型函数
	public HomeInfoStatDTO appModFunc(String token, String distName, String prefCity, String province, String date, Db1Service db1Service, Db2Service db2Service) {
		HomeInfoStatDTO hisDto = null;
		if (isRealData) { // 真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null; 
			String departmentId = null;
			if (date == null)
				date = BCDTimeUtil.convertNormalDate(null);
			List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				// 查找是否存在该区域和省市
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					if (curTdd.getName().compareTo(distName) == 0) {
						bfind = true;
						distIdorSCName = curTdd.getId();
						break;
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					if(methodIndex == 0) {      //方案1
						hisDto = homeInfoStat_Method0(distIdorSCName, date);
					}
					else if(methodIndex == 1) { //方案2
						hisDto = homeInfoStat_Method1(departmentId,distIdorSCName, date, tddList);
					}
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					hisDto = homeInfoStat_Method1(departmentId,distIdorSCName, date, tddList);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		} else { // 模拟数据
			// 模拟数据函数
			hisDto = SimuDataFunc();
		}

		return hisDto;
	}
}
