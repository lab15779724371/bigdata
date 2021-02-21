package com.tfit.BdBiProcSrvShEduOmc.appmod.bks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdBriKitStoveDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStoveSchsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.Node;

//3.7.3 - 明厨亮灶学校列表应用模型
public class BriKitStoveSchsAppMod {
	private static final Logger logger = LogManager.getLogger(BriKitStoveSchsAppMod.class.getName());
	
	@Autowired
    ObjectMapper objectMapper = new ObjectMapper();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	//测试邮件账号
	String[] testMails = {"185601074@qq.com", "zuoming_li@ssic.cn", "zheng_ji@ssic.cn"};
	
	//数组数据初始化
	String[] id_Array = {"徐汇区教育局", "杨浦区教育局", "黄浦区教育局", "长宁区教育局"};
	String[] label_Array = {"徐汇区教育局", "杨浦区教育局", "黄浦区教育局", "长宁区教育局"};	
	String[] id2_Array = {"admin", "shangwenmin", "admin001", "jizheng"};
	String[] label2_Array = {"admin", "shangwenmin", "admin001", "jizheng"};		
	//模拟数据函数
	private BriKitStoveSchsDTO SimuDataFunc() {
		BriKitStoveSchsDTO bkssDto = new BriKitStoveSchsDTO();
		//时戳
		bkssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//明厨亮灶学校列表模拟数据
		List<Node> briKitStoveSchs = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			Node node = new Node(id_Array[i], label_Array[i]);
			Node node2 = new Node(id2_Array[i], label2_Array[i]);
			node.add(node2);
			briKitStoveSchs.add(node);
		}
		//设置数据
		bkssDto.setBriKitStoveSchs(briKitStoveSchs);
		//消息ID
		bkssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bkssDto;
	}
	
	//自动加载明厨亮灶学校列表
	private BriKitStoveSchsDTO autoSchBriKitStoveSchs(String distIdorSCName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		BriKitStoveSchsDTO bkssDto = null;
		int i, l0, curL0, l1, curL1, l2, curL2 = 0;
		boolean bfindL0, bfindL1, bfindL2;
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 6);
		if(tesDoList != null) {
			bkssDto = new BriKitStoveSchsDTO();
			//时戳
			bkssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//数据权限模拟数据
			List<Node> briKitStoveSchs = new ArrayList<>();
			//赋值
			String curSchId = null, curSchName = null, curLocationAreaId = null, curSchType = null, curVal = null;
			//查询学校视频监控记录信息以学校id
		    List<TEduBdBriKitStoveDo> tebbksDoList = db2Service.getAllSchVidSurvInfos();
		    int tesDoListSize = tesDoList.size();
		    Map<String, String> schIdToVidIdxs = new HashMap<>();
		    for(i = 0; i < tebbksDoList.size(); i++) {
		    	TEduBdBriKitStoveDo tebbksDo = tebbksDoList.get(i);
		    	String schId = tebbksDo.getSchoolId();
		    	if(schIdToVidIdxs.containsKey(schId)) {
		    		curVal = schIdToVidIdxs.get(schId);
		    		curVal = curVal + "," + i;
		    		schIdToVidIdxs.put(schId, curVal);
		    	}
		    	else {
		    		curVal = String.valueOf(i);
		    		schIdToVidIdxs.put(schId, String.valueOf(i));
		    	}
		    }
		    //筛选有视频监控的学校列表
			for (i = 0; i < tesDoListSize; i++) {
				TEduSchoolDo tesDo = tesDoList.get(i);
				curSchId = tesDo.getId();
				curSchName = tesDo.getSchoolName();
				curLocationAreaId = tesDo.getArea();
				curSchType = String.valueOf(AppModConfig.schTypeNameToIdMap.get(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2())));
				if(curSchId == null)
					continue ;
				if(curSchName == null)
					curSchName = "-";
				if(curLocationAreaId == null)
					curLocationAreaId = "-";
				//查询学校视频监控记录信息以学校id
				if(schIdToVidIdxs.containsKey(curSchId)) {
					curVal = schIdToVidIdxs.get(curSchId);
					String[] curVals = curVal.split(",");
					curSchName = curSchName + "（" + curVals.length + "）";
				}
				else {
					continue ;
				}
				//区域名称
				bfindL0 = false;
				curL0 = 0;
				for(l0 = 0; l0 < briKitStoveSchs.size(); l0++) {     //查找区域名称
					if(briKitStoveSchs.get(l0).getId().equals(curLocationAreaId)) {
						curL0 = l0;
						bfindL0 = true;
						break;
					}
				}
				if(!bfindL0) {    //未查到则添加区域名称
					Node nodeL0 = new Node(curLocationAreaId, AppModConfig.distIdToNameMap.get(curLocationAreaId));
					briKitStoveSchs.add(nodeL0);
					curL0 = briKitStoveSchs.size() - 1;
				}
				//学校类型
				bfindL1 = false;
				curL1 = 0;
				for(l1 = 0; l1 < briKitStoveSchs.get(curL0).getList().size(); l1++) {     //查找学校类型
					if(briKitStoveSchs.get(curL0).getList().get(l1).getId().equals(curSchType)) {
						curL1 = l1;
						bfindL1 = true;
						break;
					}
				}
				if(!bfindL1) {    //未查到则添加学校类型
					Node nodeL0_L1 = new Node(curSchType, AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(curSchType)));
					briKitStoveSchs.get(curL0).add(nodeL0_L1);
					curL1 = briKitStoveSchs.get(curL0).getList().size() - 1;
				}
				//学校名称
				bfindL2 = false;
				curL2 = 0;
				for(l2 = 0; l2 < briKitStoveSchs.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找学校名称
					if(briKitStoveSchs.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curSchId)) {
						curL2 = l2;
						bfindL2 = true;
						break;
					}
				}
				if(!bfindL2) {    //未查到则添加学校名称
					Node nodeL0_L1_L2 = new Node(curSchId, curSchName);
					briKitStoveSchs.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
					curL2 = briKitStoveSchs.get(curL0).getList().get(curL1).getList().size() - 1;
				}
			}
			logger.info("最后索引值：" + curL2);
			//设置数据
			bkssDto.setBriKitStoveSchs(briKitStoveSchs);
			//消息ID
			bkssDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {
			logger.info("获取学校名称编码数据失败！");
		}
		
		return bkssDto;
	}
	
	// 明厨亮灶学校列表函数
	private BriKitStoveSchsDTO briKitStoveSchs(String distIdorSCName, String token, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		BriKitStoveSchsDTO bkssDto = null;
		//自动加载明厨亮灶学校列表
		bkssDto = autoSchBriKitStoveSchs(distIdorSCName, db1Service, db2Service, codes);

		return bkssDto;
	}		
	
	// 明厨亮灶学校列表模型函数
	public BriKitStoveSchsDTO appModFunc(String token, String distName, String prefCity, String province, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		BriKitStoveSchsDTO bkssDto = null;
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
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
					// 明厨亮灶学校列表函数
					bkssDto = briKitStoveSchs(distIdorSCName, token, db1Service, db2Service, codes);
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
					// 明厨亮灶学校列表函数
					bkssDto = briKitStoveSchs(distIdorSCName, token, db1Service, db2Service, codes);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}										
		}
		else {    //模拟数据
			//模拟数据函数
			bkssDto = SimuDataFunc();
		}		

		return bkssDto;
	}
}
