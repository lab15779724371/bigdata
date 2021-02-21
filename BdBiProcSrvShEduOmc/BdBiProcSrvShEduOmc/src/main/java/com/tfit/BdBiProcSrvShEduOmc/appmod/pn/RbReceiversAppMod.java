package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbReceiversDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.Node;

//通知接收人列表应用模型
public class RbReceiversAppMod {
	private static final Logger logger = LogManager.getLogger(RbReceiversAppMod.class.getName());
	
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
	private RbReceiversDTO SimuDataFunc() {
		RbReceiversDTO rrsDto = new RbReceiversDTO();
		//时戳
		rrsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//通知接收人列表模拟数据
		List<Node> rbReceivers = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			Node node = new Node(id_Array[i], label_Array[i]);
			Node node2 = new Node(id2_Array[i], label2_Array[i]);
			node.add(node2);
			rbReceivers.add(node);
		}
		//设置数据
		rrsDto.setRbReceivers(rbReceivers);
		//消息ID
		rrsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rrsDto;
	}
	
	//数组数据初始化
	public String schSimJson = "{\"time\":\"2016-07-14 09:51:35\",\"rbReceivers\":[{\"id\":\"3\",\"label\":\"区属\",\"list\":[{\"id\":\"11\",\"label\":\"徐汇区教育局\",\"list\":[{\"id\":\"3\",\"label\":\"幼儿园\",\"list\":[{\"id\":\"001\",\"label\":\"上海市徐汇区益思幼儿园（西园）\",\"list\":[{\"id\":\"tl4y_hey@qq.com\",\"label\":\"廖京萍/13764265712/tl4y_hey@qq.com\",\"list\":[]}]}]}]}]},{\"id\":\"2\",\"label\":\"市属\",\"list\":[]},{\"id\":\"1\",\"label\":\"部属\",\"list\":[]},{\"id\":\"0\",\"label\":\"其他\",\"list\":[]}],\"msgId\":1}";
	
	//监管部门通知接收人列表
	private RbReceiversDTO supDepRbReceivers(String token, String userName, Db2Service db2Service, int[] codes) {
		RbReceiversDTO rrsDto = null;
		List<Node> rbReceivers = null;
	    TEduBdUserDo tebuDo = null;
	    List<EduBdUserDo> tebuDoList = null;
	    if(userName != null) {
	    	//从数据源ds2的数据表t_edu_bd_user中查找用户信息
	    	tebuDo = db2Service.getBdUserInfoByUserName(userName);
	    }
	    else {
	    	//从数据源ds2的数据表t_edu_bd_user中查找用户信息以授权码token
		    tebuDo = db2Service.getBdUserInfoByToken(token);
	    }
		if(tebuDo.getUserAccount() != null) {                    //用户账号
			rrsDto = new RbReceiversDTO();
			rbReceivers = new ArrayList<>();
			// && tebuDo.getParentId() == null
			if(tebuDo.getOrgName().equals("市教委")) {   //市教委账号且没有父账号（即无上级），则获取所有用户名
				//获取所有用户名、单位ID、单位名称记录信息
				tebuDoList = db2Service.getAllUserInfos(tebuDo.getId(),tebuDo.getUserType());
			}
			else {       //获取父账号（上级）及其所有子账号（所有下级） ：改为获取组织内的所有数据（2019.11.1需求变更）
				//查询所有子用户记录信息以父用户id
			    tebuDoList = db2Service.getAllSubUserInfosByParentId(tebuDo.getOrgId(),null,tebuDo.getUserType());
			    //查询用户记录信息以用户id
		    	EduBdUserDo ebuDo = db2Service.getUserInfoByUserId(tebuDo.getParentId());
		    	//当前用户和父用户不在统一组织，则获取父账号
		    	if(ebuDo !=null && tebuDo!=null && ebuDo.getOrgId()!=null && tebuDo.getOrgId()!=null && 
		    			!ebuDo.getOrgId().equals(tebuDo.getOrgId())) {
		    		if(tebuDoList != null) {
				    	if(ebuDo != null)
				    		tebuDoList.add(ebuDo);
				    }
				    else {
				    	tebuDoList = new ArrayList<>();
				    	if(ebuDo != null)
				    		tebuDoList.add(ebuDo);
				    }
		    	}
			    
			}
			//获取接收用户列表
			Map<String, String> orgNameMap = new LinkedHashMap<>();
			//根据教委给出的顺序，排序
			String arrDepartment[] = {"市教委","浦东新区教育局","黄浦区教育局","静安区教育局","徐汇区教育局","长宁区教育局",
					"普陀区教育局","虹口区教育局","杨浦区教育局","宝山区教育局","闵行区教育局","嘉定区教育局","金山区教育局","松江区教育局","青浦区教育局","奉贤区教育局","崇明区教育局"};
			if(tebuDoList !=null && tebuDoList.size()>0) {
				 Map<String,EduBdUserDo> userOrgMap = tebuDoList.stream().collect(Collectors.toMap(EduBdUserDo::getOrgName,(b)->b,(v1,v2)->v1));
				 for(String departmentName : arrDepartment) {
					 if(userOrgMap.get(departmentName) != null) {
						 orgNameMap.put(departmentName, departmentName);
					 }
				 }
			}
			
			for(String curKey : orgNameMap.keySet()) {
				Node node = new Node(curKey, curKey);
				boolean isHaveLeaf = false;
				for(int i = 0; i < tebuDoList.size(); i++) {
					if(curKey.equals(tebuDoList.get(i).getOrgName()) && !tebuDoList.get(i).getUserAccount().equals(tebuDo.getUserAccount())) {
						isHaveLeaf = true;
						String label = tebuDoList.get(i).getUserAccount();
						if(tebuDoList.get(i).getName() != null)
							label = tebuDoList.get(i).getUserAccount() + "(" + tebuDoList.get(i).getName()+ "/"+tebuDoList.get(i).getMobilePhone()+"/"+tebuDoList.get(i).getEmail()+")";
						Node node2 = new Node(tebuDoList.get(i).getUserAccount(), label);
						node.add(node2);
					}
				}
				if(isHaveLeaf) {
					rbReceivers.add(node);
				}
			}
		}
		else {
			codes[0] = 2013;
			logger.info("查询数据记录失败");
		}
		if(rrsDto != null) {
			//时戳
			rrsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//设置数据
			rrsDto.setRbReceivers(rbReceivers);
			//消息ID
			rrsDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}		
		
		return rrsDto;
	}
	
	//自动加载消息通知学校接收人列表
	private RbReceiversDTO autoSchRbReceivers(String distIdorSCName, Db1Service db1Service, int[] codes) {
		RbReceiversDTO rrsDto = null;
		int i, l0, curL0, l1, curL1, l2, curL2, l3, curL3, l4, curL4, l5, curL5;
		boolean bfindL0, bfindL1, bfindL2, bfindL3, bfindL4, bfindL5;
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 6);
		if(tesDoList != null) {
			rrsDto = new RbReceiversDTO();
			//时戳
			rrsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//数据权限模拟数据
			List<Node> rbReceivers = new ArrayList<>();
			//赋值
			String curSchId = null, curSchName = null, curDmId = null, curDsId = null, curLocationAreaId = null, curSchType = null, curDmHead = null, curDmMob = null, curDmEmail = null;
			for (i = 0; i < tesDoList.size(); i++) {
				TEduSchoolDo tesDo = tesDoList.get(i);
				curDmId = tesDo.getDepartmentMasterId();
				curSchId = tesDo.getId();
				curSchName = tesDo.getSchoolName();
				if(curDmId == null)
					curDmId = "0";
				curDsId = tesDo.getDepartmentSlaveId();
				if(curDsId == null)
					curDsId = "0";
				curLocationAreaId = tesDo.getArea();
				curSchType = String.valueOf(AppModConfig.schTypeNameToIdMap.get(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2())));
				curDmHead = tesDo.getDepartmentHead();
				curDmMob = tesDo.getDepartmentMobilephone();
				curDmEmail = tesDo.getDepartmentEmail();
				if(SpringConfig.isUseTestMail) {    //使用测试邮件
					int idx = i % testMails.length;
					curDmEmail = testMails[idx];
				}
				if(curDmEmail == null)
					continue ;
				if(curDmHead == null)
					curDmHead = "-";
				if(curDmMob == null)
					curDmMob = "-";
				//所属
				bfindL0 = false;
				curL0 = 0;
				for(l0 = 0; l0 < rbReceivers.size(); l0++) {     //查找所属
					if(rbReceivers.get(l0).getId().equals(curDmId)) {
						curL0 = l0;
						bfindL0 = true;
						break;
					}
				}
				if(!bfindL0) {    //未查到则添加所属
					Node nodeL0 = new Node(curDmId, AppModConfig.subLevelIdToNameMap.get(Integer.parseInt(curDmId)));
					rbReceivers.add(nodeL0);
					curL0 = rbReceivers.size() - 1;
				}
				//所属判断
				if(curDmId.equals("3")) {    //区属
					//主管部门
					bfindL1 = false;
					curL1 = 0;
					for(l1 = 0; l1 < rbReceivers.get(curL0).getList().size(); l1++) {     //查找主管部门
						if(rbReceivers.get(curL0).getList().get(l1).getId().equals(curDsId)) {
							curL1 = l1;
							bfindL1 = true;
							break;
						}
					}
					if(!bfindL1) {    //未查到则添加主管部门
						Node nodeL0_L1 = new Node(curDsId, AppModConfig.compDepIdToNameMap3bd.get(curDsId));
						rbReceivers.get(curL0).add(nodeL0_L1);
						curL1 = rbReceivers.get(curL0).getList().size() - 1;
					}
					if(!curDsId.equals("0")) {   //非其他主管部门
						//学校类型
						bfindL2 = false;
						curL2 = 0;
						for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找学校类型
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curSchType)) {
								curL2 = l2;
								bfindL2 = true;
								break;
							}
						}
						if(!bfindL2) {    //未查到则添加学校类型
							Node nodeL0_L1_L2 = new Node(curSchType, AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(curSchType)));
							rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
							curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
						}
						//学校名称
						bfindL3 = false;
						curL3 = 0;
						for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找学校名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curSchId)) {
								curL3 = l3;
								bfindL3 = true;
								break;
							}
						}
						if(!bfindL3) {    //未查到则添加学校名称
							Node nodeL0_L1_L2_L3 = new Node(curSchId, curSchName);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
							curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
						}
						//部门负责人
						bfindL4 = false;
						curL4 = 0;
						for(l4 = 0; l4 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size(); l4++) {     //查找部门负责人
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(l4).getId().equals(curDmEmail)) {
								curL4 = l4;
								bfindL4 = true;
								break;
							}
						}
						if(!bfindL4) {    //未查到则添加部门负责人
							Node nodeL0_L1_L2_L3_L4 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).add(nodeL0_L1_L2_L3_L4);
							curL4 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size() - 1;
						}
					}
					else {   //其他主管部门
						//区域名称
						bfindL2 = false;
						curL2 = 0;
						for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找区域名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curLocationAreaId)) {
								curL2 = l2;
								bfindL2 = true;
								break;
							}
						}
						if(!bfindL2) {    //未查到则添加区域名称
							Node nodeL0_L1_L2 = new Node(curLocationAreaId, AppModConfig.distIdToNameMap.get(curLocationAreaId));
							rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
							curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
						}
						//学校类型
						bfindL3 = false;
						curL3 = 0;
						for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找学校类型
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curSchType)) {
								curL3 = l3;
								bfindL3 = true;
								break;
							}
						}
						if(!bfindL3) {    //未查到则添加学校类型
							Node nodeL0_L1_L2_L3 = new Node(curSchType, AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(curSchType)));
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
							curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
						}
						//学校名称
						bfindL4 = false;
						curL4 = 0;
						for(l4 = 0; l4 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size(); l4++) {     //查找学校名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(l4).getId().equals(curSchId)) {
								curL4 = l4;
								bfindL4 = true;
								break;
							}
						}
						if(!bfindL4) {    //未查到则添加学校名称
							Node nodeL0_L1_L2_L3_L4 = new Node(curSchId, curSchName);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).add(nodeL0_L1_L2_L3_L4);
							curL4 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size() - 1;
						}
						//部门负责人
						bfindL5 = false;
						curL5 = 0;
						for(l5 = 0; l5 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size(); l5++) {     //查找部门负责人
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().get(l5).getId().equals(curDmEmail)) {
								curL5 = l5;
								bfindL5 = true;
								break;
							}
						}
						if(!bfindL5) {    //未查到则添加部门负责人
							Node nodeL0_L1_L2_L3_L4_L5 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).add(nodeL0_L1_L2_L3_L4_L5);
							curL5 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size() - 1;
						}
						logger.info("学校部门负责人列表索引：" + curL5);
					}
				}
				else if(curDmId.equals("2")) {  //市属
					//主管部门
					bfindL1 = false;
					curL1 = 0;
					for(l1 = 0; l1 < rbReceivers.get(curL0).getList().size(); l1++) {     //查找主管部门
						if(rbReceivers.get(curL0).getList().get(l1).getId().equals(curDsId)) {
							curL1 = l1;
							bfindL1 = true;
							break;
						}
					}
					if(!bfindL1) {    //未查到则添加主管部门
						Node nodeL0_L1 = new Node(curDsId, AppModConfig.compDepIdToNameMap2.get(curDsId));
						rbReceivers.get(curL0).add(nodeL0_L1);
						curL1 = rbReceivers.get(curL0).getList().size() - 1;
					}
					if(!curDsId.equals("0")) {   //非其他主管部门
						//学校名称
						bfindL2 = false;
						curL2 = 0;
						for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找学校名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curSchId)) {
								curL2 = l2;
								bfindL2 = true;
								break;
							}
						}
						if(!bfindL2) {    //未查到则添加学校名称
							Node nodeL0_L1_L2 = new Node(curSchId, curSchName);
							rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
							curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
						}
						//部门负责人
						bfindL3 = false;
						curL3 = 0;
						for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找部门负责人
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curDmEmail)) {
								curL3 = l3;
								bfindL3 = true;
								break;
							}
						}
						if(!bfindL3) {    //未查到则添加部门负责人
							Node nodeL0_L1_L2_L3 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
							curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
						}
					}
					else {   //其他主管部门
						//区域名称
						bfindL2 = false;
						curL2 = 0;
						for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找区域名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curLocationAreaId)) {
								curL2 = l2;
								bfindL2 = true;
								break;
							}
						}
						if(!bfindL2) {    //未查到则添加区域名称
							Node nodeL0_L1_L2 = new Node(curLocationAreaId, AppModConfig.distIdToNameMap.get(curLocationAreaId));
							rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
							curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
						}
						//学校类型
						bfindL3 = false;
						curL3 = 0;
						for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找学校类型
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curSchType)) {
								curL3 = l3;
								bfindL3 = true;
								break;
							}
						}
						if(!bfindL3) {    //未查到则添加学校类型
							Node nodeL0_L1_L2_L3 = new Node(curSchType, AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(curSchType)));
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
							curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
						}
						//学校名称
						bfindL4 = false;
						curL4 = 0;
						for(l4 = 0; l4 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size(); l4++) {     //查找学校名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(l4).getId().equals(curSchId)) {
								curL4 = l4;
								bfindL4 = true;
								break;
							}
						}
						if(!bfindL4) {    //未查到则添加学校名称
							Node nodeL0_L1_L2_L3_L4 = new Node(curSchId, curSchName);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).add(nodeL0_L1_L2_L3_L4);
							curL4 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size() - 1;
						}
						//部门负责人
						bfindL5 = false;
						curL5 = 0;
						for(l5 = 0; l5 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size(); l5++) {     //查找部门负责人
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().get(l5).getId().equals(curDmEmail)) {
								curL5 = l5;
								bfindL5 = true;
								break;
							}
						}
						if(!bfindL5) {    //未查到则添加部门负责人
							Node nodeL0_L1_L2_L3_L4_L5 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).add(nodeL0_L1_L2_L3_L4_L5);
							curL5 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size() - 1;
						}
					}
				}
				else if(curDmId.equals("1")) {  //部属
					//主管部门
					bfindL1 = false;
					curL1 = 0;
					for(l1 = 0; l1 < rbReceivers.get(curL0).getList().size(); l1++) {     //查找主管部门
						if(rbReceivers.get(curL0).getList().get(l1).getId().equals(curDsId)) {
							curL1 = l1;
							bfindL1 = true;
							break;
						}
					}
					if(!bfindL1) {    //未查到则添加主管部门
						Node nodeL0_L1 = new Node(curDsId, AppModConfig.compDepNameToIdMap1.get(curDsId));
						rbReceivers.get(curL0).add(nodeL0_L1);
						curL1 = rbReceivers.get(curL0).getList().size() - 1;
					}
					if(!curDsId.equals("0")) {   //非其他主管部门
						//学校名称
						bfindL2 = false;
						curL2 = 0;
						for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找学校名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curSchId)) {
								curL2 = l2;
								bfindL2 = true;
								break;
							}
						}
						if(!bfindL2) {    //未查到则添加学校名称
							Node nodeL0_L1_L2 = new Node(curSchId, curSchName);
							rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
							curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
						}
						//部门负责人
						bfindL3 = false;
						curL3 = 0;
						for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找部门负责人
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curDmEmail)) {
								curL3 = l3;
								bfindL3 = true;
								break;
							}
						}
						if(!bfindL3) {    //未查到则添加部门负责人
							Node nodeL0_L1_L2_L3 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
							curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
						}
					}
					else {   //其他主管部门
						//区域名称
						bfindL2 = false;
						curL2 = 0;
						for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找区域名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curLocationAreaId)) {
								curL2 = l2;
								bfindL2 = true;
								break;
							}
						}
						if(!bfindL2) {    //未查到则添加区域名称
							Node nodeL0_L1_L2 = new Node(curLocationAreaId, AppModConfig.distIdToNameMap.get(curLocationAreaId));
							rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
							curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
						}
						//学校类型
						bfindL3 = false;
						curL3 = 0;
						for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找学校类型
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curSchType)) {
								curL3 = l3;
								bfindL3 = true;
								break;
							}
						}
						if(!bfindL3) {    //未查到则添加学校类型
							Node nodeL0_L1_L2_L3 = new Node(curSchType, AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(curSchType)));
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
							curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
						}
						//学校名称
						bfindL4 = false;
						curL4 = 0;
						for(l4 = 0; l4 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size(); l4++) {     //查找学校名称
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(l4).getId().equals(curSchId)) {
								curL4 = l4;
								bfindL4 = true;
								break;
							}
						}
						if(!bfindL4) {    //未查到则添加学校名称
							Node nodeL0_L1_L2_L3_L4 = new Node(curSchId, curSchName);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).add(nodeL0_L1_L2_L3_L4);
							curL4 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size() - 1;
						}
						//部门负责人
						bfindL5 = false;
						curL5 = 0;
						for(l5 = 0; l5 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size(); l5++) {     //查找部门负责人
							if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().get(l5).getId().equals(curDmEmail)) {
								curL5 = l5;
								bfindL5 = true;
								break;
							}
						}
						if(!bfindL5) {    //未查到则添加部门负责人
							Node nodeL0_L1_L2_L3_L4_L5 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
							rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).add(nodeL0_L1_L2_L3_L4_L5);
							curL5 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size() - 1;
						}
					}
				}
				else if(curDmId.equals("0")) {  //其他
					//主管部门
					bfindL1 = false;
					curL1 = 0;
					for(l1 = 0; l1 < rbReceivers.get(curL0).getList().size(); l1++) {     //查找主管部门
						if(rbReceivers.get(curL0).getList().get(l1).getId().equals(curDsId)) {
							curL1 = l1;
							bfindL1 = true;
							break;
						}
					}
					if(!bfindL1) {    //未查到则添加主管部门
						Node nodeL0_L1 = new Node(curDsId, AppModConfig.compDepIdToNameMap0.get(curDsId));
						rbReceivers.get(curL0).add(nodeL0_L1);
						curL1 = rbReceivers.get(curL0).getList().size() - 1;
					}	
					//区域名称
					bfindL2 = false;
					curL2 = 0;
					for(l2 = 0; l2 < rbReceivers.get(curL0).getList().get(curL1).getList().size(); l2++) {     //查找区域名称
						if(rbReceivers.get(curL0).getList().get(curL1).getList().get(l2).getId().equals(curLocationAreaId)) {
							curL2 = l2;
							bfindL2 = true;
							break;
						}
					}
					if(!bfindL2) {    //未查到则添加区域名称
						Node nodeL0_L1_L2 = new Node(curLocationAreaId, AppModConfig.distIdToNameMap.get(curLocationAreaId));
						rbReceivers.get(curL0).getList().get(curL1).add(nodeL0_L1_L2);
						curL2 = rbReceivers.get(curL0).getList().get(curL1).getList().size() - 1;
					}
					//学校类型
					bfindL3 = false;
					curL3 = 0;
					for(l3 = 0; l3 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size(); l3++) {     //查找学校类型
						if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(l3).getId().equals(curSchType)) {
							curL3 = l3;
							bfindL3 = true;
							break;
						}
					}
					if(!bfindL3) {    //未查到则添加学校类型
						Node nodeL0_L1_L2_L3 = new Node(curSchType, AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(curSchType)));
						rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).add(nodeL0_L1_L2_L3);
						curL3 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().size() - 1;
					}
					//学校名称
					bfindL4 = false;
					curL4 = 0;
					for(l4 = 0; l4 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size(); l4++) {     //查找学校名称
						if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(l4).getId().equals(curSchId)) {
							curL4 = l4;
							bfindL4 = true;
							break;
						}
					}
					if(!bfindL4) {    //未查到则添加学校名称
						Node nodeL0_L1_L2_L3_L4 = new Node(curSchId, curSchName);
						rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).add(nodeL0_L1_L2_L3_L4);
						curL4 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().size() - 1;
					}
					//部门负责人
					bfindL5 = false;
					curL5 = 0;
					for(l5 = 0; l5 < rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size(); l5++) {     //查找部门负责人
						if(rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().get(l5).getId().equals(curDmEmail)) {
							curL5 = l5;
							bfindL5 = true;
							break;
						}
					}
					if(!bfindL5) {    //未查到则添加部门负责人
						Node nodeL0_L1_L2_L3_L4_L5 = new Node(curDmEmail, curDmHead + "/" + curDmMob + "/" + curDmEmail);
						rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).add(nodeL0_L1_L2_L3_L4_L5);
						curL5 = rbReceivers.get(curL0).getList().get(curL1).getList().get(curL2).getList().get(curL3).getList().get(curL4).getList().size() - 1;
					}				
				}
			}

			//设置数据
			rrsDto.setRbReceivers(rbReceivers);
			//消息ID
			rrsDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {
			logger.info("获取学校名称编码数据失败！");
		}
		
		return rrsDto;
	}
	
	// 证照预警全部证件列表函数
	private RbReceiversDTO rbReceivers(String distIdorSCName, String token, String userName, int roleType, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		RbReceiversDTO rrsDto = null;
		if(roleType == -1 || roleType == 1) {  //监管部门通知接收人列表
			rrsDto = supDepRbReceivers(token, userName, db2Service, codes);
		}
		else if(roleType == 2) {   //学校通知接收人列表
			//自动加载消息通知学校接收人列表
			rrsDto = autoSchRbReceivers(distIdorSCName, db1Service, codes);
		}			

		return rrsDto;
	}		
	
	// 通知接收人列表模型函数
	public RbReceiversDTO appModFunc(String token, String userName, String roleType, String distName, String prefCity, String province, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		RbReceiversDTO rrsDto = null;
		if(isRealData) {       //真实数据
			int curRoleType = -1;
			if(roleType != null)
				curRoleType = Integer.parseInt(roleType);
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
					// 通知接收人列表函数
					rrsDto = rbReceivers(distIdorSCName, token, userName, curRoleType, db1Service, db2Service, codes);
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
					// 通知接收人列表函数
					rrsDto = rbReceivers(distIdorSCName, token, userName, curRoleType, db1Service, db2Service, codes);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}										
		}
		else {    //模拟数据
			//模拟数据函数
			rrsDto = SimuDataFunc();
		}		

		return rrsDto;
	}
}