package com.tfit.BdBiProcSrvShEduOmc.appmod.is;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdFoodSafetyGradeDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgList;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserDataPermInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//信息共享食品安全等级列表应用模型
public class IsFsgListAppMod {
	private static final Logger logger = LogManager.getLogger(IsFsgListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] distName_Array = {"徐汇区", "黄浦区", "静安区", "长宁区", "杨浦区", "普陀区", "虹口区", "浦东新区", "闵行区", "宝山区", "嘉定区", "青浦区", "松江区", "奉贤区"};
	int[] ppNum_Array = {268, 151, 243, 140, 282, 200, 238, 237, 465, 365, 212, 179, 282, 200};
	String[] ppGoodNum_Array = {"250（94%）", "141（93%）", "231（95%）", "135（92%）", "275（96%）", "191（95%）", "233（91%）", "201（88%）", "452（95%）", "355（96%）", "204（92%）", "170（92%）", "275（96%）", "191（95%）"};
	String[] ppGeneralNum_Array = {"15（5.7%）", "8（4.1%）", "13（4.3%）", "4（7.3%）", "5（2.9%）", "6（3.9%）", "4（7.9%）", "34（11%）", "10（3.9%）", "8（4%）", "6（3.9%）", "6（3.9%）", "5（2.9%）", "6（3.9%）"};
	String[] ppLessNum_Array = {"3（0.3%）", "2（2.9%）", "1（0.7%）", "1（0.7%）", "2（1.1%）", "3（1.1%）", "11（1.1%）", "3（1%）", "3（1.1%）", "2（1%）", "2（4.1%）", "3（4.1%）", "2（1.1%）", "3（1.1%）"};
	
	//模拟数据函数
	private IsFsgListDTO SimuDataFunc() {
		IsFsgListDTO iflDto = new IsFsgListDTO();
		//时戳
		iflDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//信息共享食品安全等级列表模拟数据
		List<IsFsgList> isFsgList = new ArrayList<>();
		//赋值
		for (int i = 0; i < distName_Array.length; i++) {
			IsFsgList ifl = new IsFsgList();
			ifl.setDistName(distName_Array[i]);
			ifl.setPpNum(ppNum_Array[i]);
			ifl.setPpGoodNum(ppGoodNum_Array[i]);
			ifl.setPpGeneralNum(ppGeneralNum_Array[i]);
			ifl.setPpLessNum(ppLessNum_Array[i]);
			isFsgList.add(ifl);
		}
		//设置数据
		iflDto.setIsFsgList(isFsgList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		iflDto.setPageInfo(pageInfo);
		//消息ID
		iflDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return iflDto;
	}
	
	// 信息共享食品安全等级列表函数按所在区
	private IsFsgListDTO isFsgListByLocality(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String distNames, Db1Service db1Service, Db2Service db2Service) {
		IsFsgListDTO iflDto = new IsFsgListDTO();
		List<IsFsgList> isFsgList = new ArrayList<>();
		List<TEduBdFoodSafetyGradeDo> tebfsgDoList = null;
		Map<String, String> distIdToPpNumMap = new HashMap<>(), distIdToGood = new HashMap<>(), distIdToComm = new HashMap<>(), distIdToSubStd = new HashMap<>();
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
		if(tebfsgDoList != null) {
			for(int i = 0; i < tebfsgDoList.size(); i++) {
				TEduBdFoodSafetyGradeDo tebfsgDo = tebfsgDoList.get(i);
				String distId = AppModConfig.distNameToIdMap.get(tebfsgDo.getDistName());
				if(distId != null) {
					//各区项目点数量
					if(distIdToPpNumMap.containsKey(distId)) {
						int ppNum = Integer.parseInt(distIdToPpNumMap.get(distId));
						ppNum++;
						distIdToPpNumMap.put(distId, String.valueOf(ppNum));
					}
					else {
						distIdToPpNumMap.put(distId, "1");
					}
					if(tebfsgDo.getLevelName().equalsIgnoreCase("良好")) {   //各区良好评价数量
						if(distIdToGood.containsKey(distId)) {
							int goodNum = Integer.parseInt(distIdToGood.get(distId));
							goodNum++;
							distIdToGood.put(distId, String.valueOf(goodNum));
						}
						else {
							distIdToGood.put(distId, "1");
						}
					}
					else if(tebfsgDo.getLevelName().equalsIgnoreCase("一般")) {  //各区一般评价数量
						if(distIdToComm.containsKey(distId)) {
							int commNum = Integer.parseInt(distIdToComm.get(distId));
							commNum++;
							distIdToComm.put(distId, String.valueOf(commNum));
						}
						else {
							distIdToComm.put(distId, "1");
						}
					}					
					else if(tebfsgDo.getLevelName().equalsIgnoreCase("较差")) {  //各区较差评价数量
						if(distIdToSubStd.containsKey(distId)) {
							int subStdNum = Integer.parseInt(distIdToSubStd.get(distId));
							subStdNum++;
							distIdToSubStd.put(distId, String.valueOf(subStdNum));
						}
						else {
							distIdToSubStd.put(distId, "1");
						}
					}
				}
			}
		}
		for(String key : distIdToPpNumMap.keySet()) {
			IsFsgList ifl = new IsFsgList();
			//区域ID
			ifl.setDistName(key);
			//各域项目点个数
			int ppNum = Integer.parseInt(distIdToPpNumMap.get(key));
			ifl.setPpNum(ppNum);
			//各区良好评价数量
			int goodNum = 0;
			if(distIdToGood.containsKey(key)) {
				goodNum = Integer.parseInt(distIdToGood.get(key));
			}
			float goodRate = 100 * ((float) goodNum / (float) ppNum);
			BigDecimal bd = new BigDecimal(goodRate);
			goodRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			String ppGoodNum = goodNum + "(" + goodRate + "%)";
			ifl.setPpGoodNum(ppGoodNum);
			//各区一般评价数量
			int commNum = 0;
			if(distIdToComm.containsKey(key)) {
				commNum = Integer.parseInt(distIdToComm.get(key));
			}
			float commRate = 100 * ((float) commNum / (float) ppNum);
			bd = new BigDecimal(commRate);
			commRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			String ppGeneralNum = commNum + "(" + commRate + "%)";
			ifl.setPpGeneralNum(ppGeneralNum);
			//各区较差评价数量
			int subStdNum = 0;
			if(distIdToSubStd.containsKey(key)) {
				subStdNum = Integer.parseInt(distIdToSubStd.get(key));
			}
			float subStdRate = 100 * ((float) subStdNum / (float) ppNum);
			bd = new BigDecimal(subStdRate);
			subStdRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			String ppLessNum = subStdNum + "(" + subStdRate + "%)";
			ifl.setPpLessNum(ppLessNum);
			isFsgList.add(ifl);
		}
		//时戳
    	iflDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<IsFsgList> pageBean = new PageBean<IsFsgList>(isFsgList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	iflDto.setPageInfo(pageInfo);
    	// 设置数据
    	iflDto.setIsFsgList(pageBean.getCurPageData());
    	// 消息ID
    	iflDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();

		return iflDto;
	}
	
	// 信息共享食品安全等级列表函数
	private IsFsgListDTO isFsgList(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
			int schSelMode, int subLevel, int compDep, String subDistName,
			String subLevels,String compDeps,String distNames, Db1Service db1Service, Db2Service db2Service) {
		IsFsgListDTO iflDto = null;
		//筛选学校模式
		if(schSelMode == 0) {    //按主管部门
			iflDto = isFsgListByLocality(distIdorSCName, dates, tedList, distNames, db1Service, db2Service);
		}
		else if(schSelMode == 1) {  //按所在地
			iflDto = isFsgListByLocality(distIdorSCName, dates, tedList, distNames, db1Service, db2Service);			
		}    	

		return iflDto;
	}
	
	// 信息共享食品安全等级列表模型函数
	public IsFsgListDTO appModFunc(String token, String startDate, String endDate, String schSelMode, String subLevel, String compDep, String subDistName, String distName, String prefCity, String province, String subLevels,String compDeps,String distNames, String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		IsFsgListDTO iflDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startDate == null || endDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDate);
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
				}
			}
			for (int i = 0; i < dates.length; i++) {
				logger.info("dates[" + i + "] = " + dates[i]);
			}
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
					// 信息共享食品安全等级列表函数
					iflDto = isFsgList(distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, curCompDep, subDistName,	subLevels, compDeps, distNames, db1Service, db2Service);
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
					// 信息共享食品安全等级列表函数
					iflDto = isFsgList(distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, curCompDep, subDistName,	subLevels, compDeps, distNames, db1Service, db2Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			iflDto = SimuDataFunc();
		}		

		return iflDto;
	}
}
