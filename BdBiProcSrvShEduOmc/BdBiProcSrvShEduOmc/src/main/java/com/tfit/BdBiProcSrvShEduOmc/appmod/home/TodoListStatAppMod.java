package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NoExeInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.TodoListStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.TodoListStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserInfo;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//待办事项统计应用模型
public class TodoListStatAppMod {
	private static final Logger logger = LogManager.getLogger(TodoListStatAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//变量数据初始化
	int complNoProcNum = 5;
	int warnNoProcNum = 2293;
	int sysNotiNum = 8;
	int[] noExeNum_Array = {2293, 675, 2501, 2205, 701, 605};
	int[] status_Array = {2, 1, 2, 1, 1, 2};	
	String userName = "admin";
	String orgName = "上海市教委";
	String mobPhone = "13559966295";
	String email = "admin@ssic.com";
	String fullName = "系统管理员";
	
	//模拟数据函数
	private TodoListStatDTO SimuDataFunc() {
		TodoListStatDTO tlsDto = new TodoListStatDTO();
		//时戳
		tlsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//待办事项统计模拟数据
		TodoListStat todoListStat = new TodoListStat();
		todoListStat.setComplNoProcNum(complNoProcNum);
		todoListStat.setWarnNoProcNum(warnNoProcNum);
		todoListStat.setSysNotiNum(sysNotiNum);		
		NoExeInfo warnNoProcInfo = new NoExeInfo();        //预警未消除信息
		NoExeInfo noDishSchInfo = new NoExeInfo();         //未排菜学校信息
		NoExeInfo noAcceptPlanInfo = new NoExeInfo();      //未验收计划信息
		NoExeInfo dishNoRetInfo = new NoExeInfo();         //未留样菜品信息		
		NoExeInfo noAcceptSchInfo = new NoExeInfo();       //未验收学校数		
		NoExeInfo dishNoRetSchInfo = new NoExeInfo();      //未留样学校数		
		warnNoProcInfo.setNoExeNum(noExeNum_Array[0]);
		warnNoProcInfo.setStatus(status_Array[0]);
		todoListStat.setWarnNoProcInfo(warnNoProcInfo);
		noDishSchInfo.setNoExeNum(noExeNum_Array[1]);
		noDishSchInfo.setStatus(status_Array[1]);
		todoListStat.setNoDishSchInfo(noDishSchInfo);
		noAcceptPlanInfo.setNoExeNum(noExeNum_Array[2]);
		noAcceptPlanInfo.setStatus(status_Array[2]);
		todoListStat.setNoAcceptPlanInfo(noAcceptPlanInfo);
		dishNoRetInfo.setNoExeNum(noExeNum_Array[3]);
		dishNoRetInfo.setStatus(status_Array[3]);
		todoListStat.setDishNoRetInfo(dishNoRetInfo);
		noAcceptSchInfo.setNoExeNum(noExeNum_Array[4]);
		noAcceptSchInfo.setStatus(status_Array[4]);
		todoListStat.setNoAcceptSchInfo(noAcceptSchInfo);
		dishNoRetSchInfo.setNoExeNum(noExeNum_Array[5]);
		dishNoRetSchInfo.setStatus(status_Array[5]);
		todoListStat.setDishNoRetSchInfo(dishNoRetSchInfo);		
		//用户信息
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName(userName);
		userInfo.setOrgName(orgName);
		userInfo.setMobPhone(mobPhone);
		userInfo.setEmail(email);
		userInfo.setFullName(fullName);
		todoListStat.setUserInfo(userInfo);
		tlsDto.setTodoListStat(todoListStat);
		//消息ID
		tlsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return tlsDto;
	}
	
	// 待办事项统计函数
	private TodoListStatDTO todoListStat_TT3(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, String token, 
			Db1Service db1Service, Db2Service db2Service,DbHiveWarnService dbHiveWarnService) {
		TodoListStatDTO tlsDto = new TodoListStatDTO();
		TodoListStat todoListStat = new TodoListStat();
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null, warnTotalMap = null, distributionTotalMap = null, gcRetentiondishtotalMap = null;
		int i, j, k, distCount = tedList.size(), dateCount = dates.length, curWarnNum = 0, status = 0;
		int[][] distNoDishSchNums = new int[dateCount][distCount], noProcWarnNums = new int[dateCount][distCount+1], distNoRsSchNums = new int[dateCount][distCount];
		int[][] totalGsPlanNums = new int[dateCount][distCount], acceptGsPlanNums = new int[dateCount][distCount], distNoRsDishNums = new int[dateCount][distCount], conSchNums = new int[dateCount][distCount], noAcceptSchNums = new int[dateCount][distCount], acceptSchNums = new int[dateCount][distCount];
		NoExeInfo warnNoProcInfo = new NoExeInfo();        //预警未消除信息
		NoExeInfo noDishSchInfo = new NoExeInfo();         //未排菜学校信息
		NoExeInfo noAcceptPlanInfo = new NoExeInfo();      //未验收计划信息
		NoExeInfo dishNoRetInfo = new NoExeInfo();         //未留样菜品信息
		NoExeInfo noAcceptSchInfo = new NoExeInfo();       //未验收学校信息
		NoExeInfo dishNoRetSchInfo = new NoExeInfo();      //未留样学校信息
		todoListStat.setComplNoProcNum(complNoProcNum);
		todoListStat.setWarnNoProcNum(warnNoProcNum);
		todoListStat.setSysNotiNum(sysNotiNum);
		
		//预警相关
		//2019-06-12注释，注释原因：预警信息从hive库中获取
		//getWarnInfoOne(distIdorSCName, dates, tedList, todoListStat, keyVal, distCount, noProcWarnNums,warnNoProcInfo);
		//target :2:区教育局,3:市教委
	  	Integer target = CommonUtil.getTarget(token,db1Service,db2Service);
		getWarnInfoThree(departmentId, distIdorSCName, dates, todoListStat, keyVal, warnNoProcInfo, target, dbHiveWarnService);
		
		// 当天、前一天各区未排菜学校数量
		for (k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k] + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
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
						int noDishSchNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("未排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										noDishSchNum = Integer.parseInt(keyVal);
									}
								}
							}
						}
						distNoDishSchNums[k][i] += noDishSchNum;
					}
				}
			}
			// 该日期各区学校排菜率
			for (i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				field = "area" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (distIdorSCName != null) {
					if (!curDistId.equals(distIdorSCName))
						continue;
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，未排菜学校数量：" + distNoDishSchNums[k][i] + "，field = " + field);
			}
		}		
		int distNoDishSchNum = 0, preDistNoDishSchNum = 0;
		for (i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}
			distNoDishSchNum += distNoDishSchNums[0][i];
			preDistNoDishSchNum += distNoDishSchNums[1][i];
		}
		status = 0;
		if(distNoDishSchNum > preDistNoDishSchNum)
			status = 1;
		else if(distNoDishSchNum < preDistNoDishSchNum)
			status = 2;
		logger.info("今日未排菜数：" + distNoDishSchNum + "，昨天未排菜数：" + preDistNoDishSchNum + "，状态：" + status);
		//设置未排菜学校信息
		noDishSchInfo.setNoExeNum(distNoDishSchNum);
		noDishSchInfo.setStatus(status);
		todoListStat.setNoDishSchInfo(noDishSchInfo);
		// 当天各区配货计划未验收数量
		for(k = 0; k < dates.length; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equalsIgnoreCase(distIdorSCName))
							continue ;
					}
					// 区域配货计划总数
					field = "area" + "_" + curDistId;
					totalGsPlanNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						totalGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(totalGsPlanNums[k][i] < 0)
							totalGsPlanNums[k][i] = 0;
					}
					// 已验收数
					field = "area" + "_" + curDistId + "_" + "status" + "_3";
					acceptGsPlanNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						acceptGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(acceptGsPlanNums[k][i] < 0)
							acceptGsPlanNums[k][i] = 0;
					}
				}
			}
		}
		int noAcceptPlanNum = 0, preNoAcceptPlanNum = 0;
		for(i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(distIdorSCName != null) {
				if(!curDistId.equalsIgnoreCase(distIdorSCName))
					continue ;
			}
			noAcceptPlanNum += (totalGsPlanNums[0][i]-acceptGsPlanNums[0][i]);
			preNoAcceptPlanNum += (totalGsPlanNums[1][i]-acceptGsPlanNums[1][i]);
		}		
		status = 0;
		if(noAcceptPlanNum > preNoAcceptPlanNum)
			status = 1;
		else if(noAcceptPlanNum < preNoAcceptPlanNum)
			status = 2;
		logger.info("今日未验收数：" + noAcceptPlanNum + "，昨天未验收数：" + preNoAcceptPlanNum + "，状态：" + status);
		//设置未验收信息
		noAcceptPlanInfo.setNoExeNum(noAcceptPlanNum);
		noAcceptPlanInfo.setStatus(status);
		todoListStat.setNoAcceptPlanInfo(noAcceptPlanInfo);		
		// 当天各区菜品未留样数量
		for (k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
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
								if(curKeys[1].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsDishNums[k][i] = Integer.parseInt(keyVal);
									}
								}
							}
						}
					}
				}
			}
		}
		int distNoRsDishNum = 0, preDistNoRsDishNum = 0;
		for (i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}
			distNoRsDishNum += distNoRsDishNums[0][i];
			preDistNoRsDishNum += distNoRsDishNums[1][i];
		}
		status = 0;
		if(distNoRsDishNum > preDistNoRsDishNum)
			status = 1;
		else if(distNoRsDishNum < preDistNoRsDishNum)
			status = 2;
		logger.info("今日未留样数：" + distNoRsDishNum + "，昨天未留样数：" + preDistNoRsDishNum + "，状态：" + status);
		//设置未留样信息
		dishNoRetInfo.setNoExeNum(distNoRsDishNum);
		dishNoRetInfo.setStatus(status);
		todoListStat.setDishNoRetInfo(dishNoRetInfo);
		// 当天各区未验收学校数量
		int [][] totalSchNums = new int[dateCount][distCount];
		for (k = 0; k < dates.length; k++) {
			//配货计划数量
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equalsIgnoreCase(distIdorSCName))
							continue ;
					}
					//已确认学校
					conSchNums[k][i] = 0;
					for(j = 2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k][i] += curConSchNum;
						}
					}
					
					//已确认学校
					totalSchNums[k][i] = 0;
					for(j = -2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							totalSchNums[k][i] += curConSchNum;
						}
					}
					
					//已验收学校
					field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
					acceptSchNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						acceptSchNums[k][i] = Integer.parseInt(keyVal);
						if(acceptSchNums[k][i] < 0)
							acceptSchNums[k][i] = 0;
					}
					//未验收学校
					noAcceptSchNums[k][i] = totalSchNums[k][i] - acceptSchNums[k][i];
				}
			}
		}
		int noAcceptSchNum = 0, preNoAcceptSchNum = 0;
		for (i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}
			noAcceptSchNum += noAcceptSchNums[0][i];
			preNoAcceptSchNum += noAcceptSchNums[1][i];
		}
		status = 0;
		if(noAcceptSchNum > preNoAcceptSchNum)
			status = 1;
		else if(noAcceptSchNum < preNoAcceptSchNum)
			status = 2;
		logger.info("今日未验收学校数：" + noAcceptSchNum + "，昨天未验收学校数：" + preNoAcceptSchNum + "，状态：" + status);
		//设置未验收学校信息
		noAcceptSchInfo.setNoExeNum(noAcceptSchNum);
		noAcceptSchInfo.setStatus(status);
		todoListStat.setNoAcceptSchInfo(noAcceptSchInfo);		
		// 当天各区未留样学校数量
		for (k = 0; k < dates.length; k++) {
			//留样菜品数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
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
						// 区域学校留样和未留样数
						fieldPrefix = "school-area" + "_" + curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
							}
						}
					}
				}
			}
		}
		int distNoRsSchNum = 0, preDistNoRsSchNum = 0;
		for (i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}
			distNoRsSchNum += distNoRsSchNums[0][i];
			preDistNoRsSchNum += distNoRsSchNums[1][i];
		}
		status = 0;
		if(distNoRsSchNum > preDistNoRsSchNum)
			status = 1;
		else if(distNoRsSchNum < preDistNoRsSchNum)
			status = 2;
		logger.info("今日未留样学校数：" + distNoRsSchNum + "，昨天未留样学校数：" + preDistNoRsSchNum + "，状态：" + status);
		//设置未留样学校信息
		dishNoRetSchInfo.setNoExeNum(distNoRsSchNum);
		dishNoRetSchInfo.setStatus(status);
		todoListStat.setDishNoRetSchInfo(dishNoRetSchInfo);		
		//用户信息
		UserInfo userInfo = new UserInfo();
		TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);   //从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
		if(tebuDo.getId() != null) {
			userInfo.setUserName(tebuDo.getUserAccount());                                            //用户名
			userInfo.setOrgName(tebuDo.getOrgName());                                                 //单位名称
			userInfo.setMobPhone(tebuDo.getMobilePhone());                                            //手机号码
			userInfo.setEmail(tebuDo.getEmail());                                                     //电子邮箱
			userInfo.setFullName(tebuDo.getName());                                                   //姓名
		}
		todoListStat.setUserInfo(userInfo);
		// 设置返回数据
		tlsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 设置数据
		tlsDto.setTodoListStat(todoListStat);
		// 消息ID
		tlsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return tlsDto;
	}

	/**
	 * 获取预警相关信息（从redis中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param todoListStat
	 * @param keyVal
	 * @param distCount
	 * @param noProcWarnNums
	 * @param warnNoProcInfo
	 */
	private void getWarnInfoOne(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			TodoListStat todoListStat, String keyVal, int distCount, int[][] noProcWarnNums, NoExeInfo warnNoProcInfo) {
		String key;
		Map<String, String> warnTotalMap;
		int i;
		int j;
		int k;
		int curWarnNum;
		int status;
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		// 当天、前一天各区未消除预警数量
		for (k = 0; k < dates.length; k++) {
			// 供应数量
			key = dates[k] + "_warn-total";
			warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(warnTotalMap != null) {
				for(String curKey : warnTotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length == 4) {
						i = AppModConfig.getVarValIndex(curKeys, "area");
						if(i != -1) {
							if(!curKeys[i].equalsIgnoreCase("null")) {
								if(distIdToIdxMap.containsKey(curKeys[i])) {
									int idx = distIdToIdxMap.get(curKeys[i]);
									keyVal = warnTotalMap.get(curKey);
									curWarnNum = 0;
									if(keyVal != null)
										curWarnNum = Integer.parseInt(keyVal);
									if(curWarnNum < 0)
										curWarnNum = 0;
									j = AppModConfig.getVarValIndex(curKeys, "status");
									if(j != -1) {
										if(curKeys[j].equalsIgnoreCase("1")) {         //未处理预警数
											noProcWarnNums[k][idx] += curWarnNum;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		int preNoProcWarnNum = 0, noProcWarnNum = 0;
		for (i = 0; i < distCount; i++) {
			String curDistId = "-";
			if(i < distCount) {
				TEduDistrictDo curTdd = tedList.get(i);
				curDistId = curTdd.getId();
			}
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equalsIgnoreCase(distIdorSCName))
					continue;
			}
			noProcWarnNum += noProcWarnNums[0][i];
			preNoProcWarnNum += noProcWarnNums[1][i];
		}
		status = 0;
		if(noProcWarnNum > preNoProcWarnNum)
			status = 1;
		else if(noProcWarnNum < preNoProcWarnNum)
			status = 2;
		//设置未处理预警信息
		warnNoProcInfo.setNoExeNum(noProcWarnNum);
		warnNoProcInfo.setStatus(status);
		todoListStat.setWarnNoProcInfo(warnNoProcInfo);
		logger.info("今日预警未处理数：" + noProcWarnNum + "，昨天预警未处理数：" + preNoProcWarnNum + "，状态：" + status);
	}
	
	/**
	 * 获取预警相关信息（从hive中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param todoListStat
	 * @param keyVal
	 * @param distCount
	 * @param noProcWarnNums
	 * @param warnNoProcInfo
	 */
	private void getWarnInfoTwo(String distIdorSCName, String[] dates,
			TodoListStat todoListStat, String keyVal,NoExeInfo warnNoProcInfo,Integer target,
			DbHiveWarnService dbHiveWarnService) {
		String startDate = dates[1];
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
		List<WarnCommonLics> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveWarnService.getWarnLicList(4, listYearMonth, startDate, endDateAddOne, distIdorSCName,target);
		
		/**
		 * 2.合并数据(按日期合并)
		 */
		int preNoProcWarnNum = 0;
		int noProcWarnNum = 0;
		for(WarnCommonLics warnCommonLics : warnCommonLicList) {
			if(dates[0].replaceAll("-", "/").equals(warnCommonLics.getWarnPeriod())) {
				noProcWarnNum += warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum();
			}else {
				preNoProcWarnNum += warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum();
			}
			
		}
		
		int status = 0;
		if(noProcWarnNum > preNoProcWarnNum)
			status = 1;
		else if(noProcWarnNum < preNoProcWarnNum)
			status = 2;
		//设置未处理预警信息
		warnNoProcInfo.setNoExeNum(noProcWarnNum);
		warnNoProcInfo.setStatus(status);
		todoListStat.setWarnNoProcInfo(warnNoProcInfo);
		logger.info("今日预警未处理数：" + noProcWarnNum + "，昨天预警未处理数：" + preNoProcWarnNum + "，状态：" + status);
	}
	
	/**
	 * 获取预警相关信息（修改表）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param todoListStat
	 * @param keyVal
	 * @param distCount
	 * @param noProcWarnNums
	 * @param warnNoProcInfo
	 */
	private void getWarnInfoThree(String departmentId, String distIdorSCName, String[] dates,
			TodoListStat todoListStat, String keyVal,NoExeInfo warnNoProcInfo,Integer target,
			DbHiveWarnService dbHiveWarnService) {
		String startDate = dates[1];
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				distIdorSCName, null, -1, -1, null, null, departmentId, null, 0);
		
		/**
		 * 2.合并数据(按日期合并)
		 */
		int preNoProcWarnNum = 0;
		int noProcWarnNum = 0;
		for(WarnCommon warnCommonLics : warnCommonList) {
			if(dates[0].replaceAll("-", "/").equals(warnCommonLics.getWarnPeriod())) {
				noProcWarnNum += warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum();
			}else {
				preNoProcWarnNum += warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum();
			}
			
		}
		
		int status = 0;
		if(noProcWarnNum > preNoProcWarnNum)
			status = 1;
		else if(noProcWarnNum < preNoProcWarnNum)
			status = 2;
		//设置未处理预警信息
		warnNoProcInfo.setNoExeNum(noProcWarnNum);
		warnNoProcInfo.setStatus(status);
		todoListStat.setWarnNoProcInfo(warnNoProcInfo);
		logger.info("今日预警未处理数：" + noProcWarnNum + "，昨天预警未处理数：" + preNoProcWarnNum + "，状态：" + status);
	}
	
	// 待办事项统计模型函数
	public TodoListStatDTO appModFunc(String token, String distName, String prefCity, String province, String timeType, String date, 
			Db1Service db1Service, Db2Service db2Service,DbHiveWarnService dbHiveWarnService) {
		TodoListStatDTO tlsDto = null;
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			int curTimeType = 3, days = 2;
			if(timeType != null)
				curTimeType = Integer.parseInt(timeType);
			if(curTimeType == 3) {
				dates = new String[days];
				if (date == null) { // 按照当天日期获取数据
					date = BCDTimeUtil.convertNormalDate(null);
					dates[0] = date;
					DateTime endDt = BCDTimeUtil.convertDateStrToDate(date);
					dates[1] = endDt.minusDays(1).toString("yyyy-MM-dd");
				} 
				else { // 按照开始日期和结束日期获取数据
					DateTime endDt = BCDTimeUtil.convertDateStrToDate(date);
					for (int i = 0; i < days; i++) {
						dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
					}
				}
				for (int i = 0; i < dates.length; i++) {
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}			
			// 省或直辖市
			if(province == null)
				province = "上海市";
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			String departmentId = null;
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
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 待办事项统计函数
					if(curTimeType == 3)
						tlsDto = todoListStat_TT3(departmentId,distIdorSCName, dates, tedList, token, db1Service, db2Service,dbHiveWarnService);
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
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 待办事项统计函数
					if(curTimeType == 3)
						tlsDto = todoListStat_TT3(departmentId,distIdorSCName, dates, tedList, token, db1Service, db2Service,dbHiveWarnService);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			tlsDto = SimuDataFunc();
		}		

		return tlsDto;
	}
}
