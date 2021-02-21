package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmDataPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.Node;

//数据权限应用模型
public class AmDataPermAppMod {
	private static final Logger logger = LogManager.getLogger(AmDataPermAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] id_Array = {"3", "2", "1", "0"};
	String[] label_Array = {"区属", "市属", "部属", "其他"};
		
	//模拟数据函数
	private AmDataPermDTO SimuDataFunc() {
		AmDataPermDTO adpDto = new AmDataPermDTO();
		//时戳
		adpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//数据权限模拟数据
		List<Node> amDataPerm = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			Node node = new Node(id_Array[i], label_Array[i]);
			amDataPerm.add(node);
		}
		//设置数据
		adpDto.setAmDataPerm(amDataPerm);
		//消息ID
		adpDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return adpDto;
	}
	
	//所有数据权限
	AmDataPermDTO allDataPerm(Db1Service db1Service, int[] codes) {
		AmDataPermDTO adpDto = null;
		String distIdorSCName = null;
		int i, j, k, l;
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 4);
		if(tesDoList != null) {
			adpDto = new AmDataPermDTO();
			//时戳
			adpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//数据权限模拟数据
			List<Node> amDataPerm = new ArrayList<>();
			//所属级别节点
			Node nodeL1_1 = new Node("3", "区属");
			Node nodeL1_2 = new Node("2", "市属");
			Node nodeL1_3 = new Node("1", "部属");
			Node nodeL1_4 = new Node("0", "其他");
			amDataPerm.add(nodeL1_1);
			amDataPerm.add(nodeL1_2);
			amDataPerm.add(nodeL1_3);
			amDataPerm.add(nodeL1_4);
			//区属级别下主管部门
			List<Node> nodeL1_1_L2 = new ArrayList<>();
			for(String curKey : AppModConfig.compDepIdToNameMap3.keySet()) {
				Node nodeL1_1_L2_n = new Node(curKey, AppModConfig.compDepIdToNameMap3.get(curKey));
				nodeL1_1_L2.add(nodeL1_1_L2_n);
			}
			nodeL1_1.setList(nodeL1_1_L2);
			//市属级别下主管部门
			List<Node> nodeL1_2_L2 = new ArrayList<>();
			for(String curKey : AppModConfig.compDepIdToNameMap2.keySet()) {
				Node nodeL1_2_L2_n = new Node(curKey, AppModConfig.compDepIdToNameMap2.get(curKey));
				nodeL1_2_L2.add(nodeL1_2_L2_n);
			}
			nodeL1_2.setList(nodeL1_2_L2);
			//部属级别下主管部门
			List<Node> nodeL1_3_L2 = new ArrayList<>();
			for(String curKey : AppModConfig.compDepIdToNameMap1.keySet()) {
				Node nodeL1_3_L2_n = new Node(curKey, AppModConfig.compDepIdToNameMap1.get(curKey));
				nodeL1_3_L2.add(nodeL1_3_L2_n);
			}
			nodeL1_3.setList(nodeL1_3_L2);
			//其他级别下主管部门
			List<Node> nodeL1_4_L2 = new ArrayList<>();
			Node nodeL1_4_L2_n = new Node("0", "其他");
			nodeL1_4_L2.add(nodeL1_4_L2_n);
			nodeL1_4.setList(nodeL1_4_L2);
			//区属级别下主管部门下学校类型（学制）
			for(i = 0; i < nodeL1_1_L2.size(); i++) {
				List<Node> nodeL1_1_L2_i_L3 = new ArrayList<>();
				for(Integer curKey : AppModConfig.schTypeIdToNameMap.keySet()) {
					Node nodeL1_1_L2_i_L3_n = new Node(String.valueOf(curKey), AppModConfig.schTypeIdToNameMap.get(curKey));
					nodeL1_1_L2_i_L3.add(nodeL1_1_L2_i_L3_n);
				}
				Node nodeL1_1_L2_i = nodeL1_1_L2.get(i);
				nodeL1_1_L2_i.setList(nodeL1_1_L2_i_L3);
			}
			//市属级别下主管部门下直接为学校ID和名称
			//部属级别下主管部门下所在区
			for(i = 0; i < nodeL1_3_L2.size(); i++) {
				List<Node> nodeL1_3_L2_i_L3 = new ArrayList<>();
				for(String curKey : AppModConfig.distIdToNameMap.keySet()) {
					Node nodeL1_3_L2_i_L3_n = new Node(curKey, AppModConfig.distIdToNameMap.get(curKey));
					nodeL1_3_L2_i_L3.add(nodeL1_3_L2_i_L3_n);
				}
				Node nodeL1_3_L2_i = nodeL1_3_L2.get(i);
				nodeL1_3_L2_i.setList(nodeL1_3_L2_i_L3);
			}
			//其他级别下主管部门下所在区
			for(i = 0; i < nodeL1_4_L2.size(); i++) {
				List<Node> nodeL1_4_L2_i_L3 = new ArrayList<>();
				for(String curKey : AppModConfig.distIdToNameMap.keySet()) {
					Node nodeL1_4_L2_i_L3_n = new Node(curKey, AppModConfig.distIdToNameMap.get(curKey));
					nodeL1_4_L2_i_L3.add(nodeL1_4_L2_i_L3_n);
				}
				Node nodeL1_4_L2_i = nodeL1_4_L2.get(i);
				nodeL1_4_L2_i.setList(nodeL1_4_L2_i_L3);
			}
			//赋值
			String curDmId = null, curDsId = null, curLocationAreaId = null, curSchType = null;
			Map<String, Integer> schIdToFlagMap = new HashMap<>();
			for (i = 0; i < tesDoList.size(); i++) {
				TEduSchoolDo tesDo = tesDoList.get(i);
				curDmId = "0";
				curDsId = "0";
				curLocationAreaId = null;
				curSchType = null;
				if(tesDo.getDepartmentMasterId() != null) {
					curDmId = tesDo.getDepartmentMasterId();
				}
				if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
					curDsId = tesDo.getDepartmentSlaveId();
				}
				if(tesDo.getArea() != null) {
					curLocationAreaId = tesDo.getArea();
				}
				curSchType = String.valueOf(AppModConfig.schTypeNameToIdMap.get(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2())));
				for(j = 0; j < amDataPerm.size(); j++) {
					if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("3")) {          //区属
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {       
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {  //主管部门  
								for(l = 0; l < amDataPerm.get(j).getList().get(k).getList().size(); l++) {
									if(amDataPerm.get(j).getList().get(k).getList().get(l).getId().equals(curSchType)) {   //学校类型（学制）
										if(!schIdToFlagMap.containsKey(tesDo.getId())) {
											Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
											amDataPerm.get(j).getList().get(k).getList().get(l).add(node);
											schIdToFlagMap.put(tesDo.getId(), 1);
										}										
									}
								}
							}
						}
					}
					else if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("2")) {    //市属
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {     //主管部门
								if(!schIdToFlagMap.containsKey(tesDo.getId())) {
									Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
									amDataPerm.get(j).getList().get(k).add(node);
									schIdToFlagMap.put(tesDo.getId(), 1);
								}
							}
						}
					}
					else if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("1")) {    //部属
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {     //主管部门
								for(l = 0; l < amDataPerm.get(j).getList().get(k).getList().size(); l++) {
									if(amDataPerm.get(j).getList().get(k).getList().get(l).getId().equals(curLocationAreaId)) {   //所在区
										if(!schIdToFlagMap.containsKey(tesDo.getId())) {
											Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
											amDataPerm.get(j).getList().get(k).getList().get(l).add(node);
											schIdToFlagMap.put(tesDo.getId(), 1);
										}
									}
								}
							}
						}
					}
					else if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("0"))   { //其他
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {     //主管部门
								for(l = 0; l < amDataPerm.get(j).getList().get(k).getList().size(); l++) {
									if(amDataPerm.get(j).getList().get(k).getList().get(l).getId().equals(curLocationAreaId)) {   //所在区
										if(!schIdToFlagMap.containsKey(tesDo.getId())) {
											Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
											amDataPerm.get(j).getList().get(k).getList().get(l).add(node);
											schIdToFlagMap.put(tesDo.getId(), 1);
										}
									}
								}
							}
						}
					}
				}
			}
			//修建树形冗余层次数据
			for(i = 0; i < amDataPerm.size(); i++) {
				if(amDataPerm.get(i).getId().equals("3")) {        //区属
					for(j = 0; j < amDataPerm.get(i).getList().size(); j++) {   //区属主管部门
						Iterator<Node> it_i_j = amDataPerm.get(i).getList().get(j).getList().iterator();
				        while(it_i_j.hasNext()){
				        	Node n_i_j_k = (Node)it_i_j.next();
				            if(n_i_j_k.getList().size() == 0 ){
				            	it_i_j.remove();
				            }        
				        }
					}
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
				else if(amDataPerm.get(i).getId().equals("2")) {   //市属
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
				else if(amDataPerm.get(i).getId().equals("1")) {   //部署
					for(j = 0; j < amDataPerm.get(i).getList().size(); j++) {   //部属主管部门
						Iterator<Node> it_i_j = amDataPerm.get(i).getList().get(j).getList().iterator();
				        while(it_i_j.hasNext()){
				        	Node n_i_j_k = (Node)it_i_j.next();
				            if(n_i_j_k.getList().size() == 0 ){
				            	it_i_j.remove();
				            }        
				        }
					}
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
				else if(amDataPerm.get(i).getId().equals("0")) {   //其他
					for(j = 0; j < amDataPerm.get(i).getList().size(); j++) {   //其他
						Iterator<Node> it_i_j = amDataPerm.get(i).getList().get(j).getList().iterator();
				        while(it_i_j.hasNext()){
				        	Node n_i_j_k = (Node)it_i_j.next();
				            if(n_i_j_k.getList().size() == 0 ){
				            	it_i_j.remove();
				            }        
				        }
					}
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
			}
			Iterator<Node> it = amDataPerm.iterator();
	        while(it.hasNext()){
	        	Node n = (Node)it.next();
	            if(n.getList().size() == 0 ){
	            	it.remove();
	            }        
	        }
			//设置数据
			adpDto.setAmDataPerm(amDataPerm);
			//消息ID
			adpDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {
			logger.info("获取学校名称编码数据失败！");
		}
		
		return adpDto;
	}
	
	//分配数据权限
	AmDataPermDTO assignDataPerm(String userId, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmDataPermDTO adpDto = null;
		String distIdorSCName = null;
		int i, j, k, l;
		//从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
	    List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(userId, 1);	   
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 4);
		if(tebupDoList != null && tesDoList != null) {			
			//所有学校id到索引映射
			Map<String, Integer> schIdToIdxMap = new HashMap<>();
			for(i = 0; i < tesDoList.size(); i++) {
				schIdToIdxMap.put(tesDoList.get(i).getId(), i);
			}
			adpDto = new AmDataPermDTO();
			//时戳
			adpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//数据权限模拟数据
			List<Node> amDataPerm = new ArrayList<>();
			//所属级别节点
			Node nodeL1_1 = new Node("3", "区属");
			Node nodeL1_2 = new Node("2", "市属");
			Node nodeL1_3 = new Node("1", "部属");
			Node nodeL1_4 = new Node("0", "其他");
			amDataPerm.add(nodeL1_1);
			amDataPerm.add(nodeL1_2);
			amDataPerm.add(nodeL1_3);
			amDataPerm.add(nodeL1_4);
			//区属级别下主管部门
			List<Node> nodeL1_1_L2 = new ArrayList<>();
			for(String curKey : AppModConfig.compDepIdToNameMap3.keySet()) {
				Node nodeL1_1_L2_n = new Node(curKey, AppModConfig.compDepIdToNameMap3.get(curKey));
				nodeL1_1_L2.add(nodeL1_1_L2_n);
			}
			nodeL1_1.setList(nodeL1_1_L2);
			//市属级别下主管部门
			List<Node> nodeL1_2_L2 = new ArrayList<>();
			for(String curKey : AppModConfig.compDepIdToNameMap2.keySet()) {
				Node nodeL1_2_L2_n = new Node(curKey, AppModConfig.compDepIdToNameMap2.get(curKey));
				nodeL1_2_L2.add(nodeL1_2_L2_n);
			}
			nodeL1_2.setList(nodeL1_2_L2);
			//部属级别下主管部门
			List<Node> nodeL1_3_L2 = new ArrayList<>();
			for(String curKey : AppModConfig.compDepIdToNameMap1.keySet()) {
				Node nodeL1_3_L2_n = new Node(curKey, AppModConfig.compDepIdToNameMap1.get(curKey));
				nodeL1_3_L2.add(nodeL1_3_L2_n);
			}
			nodeL1_3.setList(nodeL1_3_L2);
			//其他级别下主管部门
			List<Node> nodeL1_4_L2 = new ArrayList<>();
			Node nodeL1_4_L2_n = new Node("0", "其他");
			nodeL1_4_L2.add(nodeL1_4_L2_n);
			nodeL1_4.setList(nodeL1_4_L2);
			//区属级别下主管部门下学校类型（学制）
			for(i = 0; i < nodeL1_1_L2.size(); i++) {
				List<Node> nodeL1_1_L2_i_L3 = new ArrayList<>();
				for(Integer curKey : AppModConfig.schTypeIdToNameMap.keySet()) {
					Node nodeL1_1_L2_i_L3_n = new Node(String.valueOf(curKey), AppModConfig.schTypeIdToNameMap.get(curKey));
					nodeL1_1_L2_i_L3.add(nodeL1_1_L2_i_L3_n);
				}
				Node nodeL1_1_L2_i = nodeL1_1_L2.get(i);
				nodeL1_1_L2_i.setList(nodeL1_1_L2_i_L3);
			}
			//市属级别下主管部门下直接为学校ID和名称
			//部属级别下主管部门下所在区
			for(i = 0; i < nodeL1_3_L2.size(); i++) {
				List<Node> nodeL1_3_L2_i_L3 = new ArrayList<>();
				for(String curKey : AppModConfig.distIdToNameMap.keySet()) {
					Node nodeL1_3_L2_i_L3_n = new Node(curKey, AppModConfig.distIdToNameMap.get(curKey));
					nodeL1_3_L2_i_L3.add(nodeL1_3_L2_i_L3_n);
				}
				Node nodeL1_3_L2_i = nodeL1_3_L2.get(i);
				nodeL1_3_L2_i.setList(nodeL1_3_L2_i_L3);
			}
			//其他级别下主管部门下所在区
			for(i = 0; i < nodeL1_4_L2.size(); i++) {
				List<Node> nodeL1_4_L2_i_L3 = new ArrayList<>();
				for(String curKey : AppModConfig.distIdToNameMap.keySet()) {
					Node nodeL1_4_L2_i_L3_n = new Node(curKey, AppModConfig.distIdToNameMap.get(curKey));
					nodeL1_4_L2_i_L3.add(nodeL1_4_L2_i_L3_n);
				}
				Node nodeL1_4_L2_i = nodeL1_4_L2.get(i);
				nodeL1_4_L2_i.setList(nodeL1_4_L2_i_L3);
			}
			//赋值
			String curDmId = null, curDsId = null, curLocationAreaId = null, curSchType = null;
			Map<String, Integer> schIdToFlagMap = new HashMap<>();
			for (i = 0; i < tebupDoList.size(); i++) {
				TEduSchoolDo tesDo = null;
				String schId = tebupDoList.get(i).getPermId();
				if(schIdToIdxMap.containsKey(schId)) {
					j = schIdToIdxMap.get(schId);
					tesDo = tesDoList.get(j);
				}
				if(tesDo == null) {
					continue ;
				}
				curDmId = "0";
				curDsId = "0";
				curLocationAreaId = null;
				curSchType = null;
				if(tesDo.getDepartmentMasterId() != null) {
					curDmId = tesDo.getDepartmentMasterId();
				}
				if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
					curDsId = tesDo.getDepartmentSlaveId();
				}
				if(tesDo.getArea() != null) {
					curLocationAreaId = tesDo.getArea();
				}
				curSchType = String.valueOf(AppModConfig.schTypeNameToIdMap.get(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2())));
				for(j = 0; j < amDataPerm.size(); j++) {
					if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("3")) {          //区属
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {       
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {  //主管部门  
								for(l = 0; l < amDataPerm.get(j).getList().get(k).getList().size(); l++) {
									if(amDataPerm.get(j).getList().get(k).getList().get(l).getId().equals(curSchType)) {   //学校类型（学制）
										if(!schIdToFlagMap.containsKey(tesDo.getId())) {
											Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
											amDataPerm.get(j).getList().get(k).getList().get(l).add(node);
											schIdToFlagMap.put(tesDo.getId(), 1);
										}
									}
								}
							}
						}
					}
					else if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("2")) {    //市属
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {     //主管部门
								if(!schIdToFlagMap.containsKey(tesDo.getId())) {
									Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
									amDataPerm.get(j).getList().get(k).add(node);
									schIdToFlagMap.put(tesDo.getId(), 1);
								}
							}
						}
					}
					else if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("1")) {    //部属
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {     //主管部门
								for(l = 0; l < amDataPerm.get(j).getList().get(k).getList().size(); l++) {
									if(amDataPerm.get(j).getList().get(k).getList().get(l).getId().equals(curLocationAreaId)) {   //所在区
										if(!schIdToFlagMap.containsKey(tesDo.getId())) {
											Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
											amDataPerm.get(j).getList().get(k).getList().get(l).add(node);
											schIdToFlagMap.put(tesDo.getId(), 1);
										}
									}
								}
							}
						}
					}
					else if(curDmId != null && amDataPerm.get(j).getId().equals(curDmId) && curDmId.equals("0"))   {    //其他
						for(k = 0; k < amDataPerm.get(j).getList().size(); k++) {
							if(amDataPerm.get(j).getList().get(k).getId().equals(curDsId)) {     //主管部门
								for(l = 0; l < amDataPerm.get(j).getList().get(k).getList().size(); l++) {
									if(amDataPerm.get(j).getList().get(k).getList().get(l).getId().equals(curLocationAreaId)) {   //所在区
										if(!schIdToFlagMap.containsKey(tesDo.getId())) {
											Node node = new Node(tesDo.getId(), tesDo.getSchoolName());
											amDataPerm.get(j).getList().get(k).getList().get(l).add(node);
											schIdToFlagMap.put(tesDo.getId(), 1);
										}
									}
								}
							}
						}
					}
				}
			}
			//修建树形冗余层次数据
			for(i = 0; i < amDataPerm.size(); i++) {
				if(amDataPerm.get(i).getId().equals("3")) {        //区属
					for(j = 0; j < amDataPerm.get(i).getList().size(); j++) {   //区属主管部门
						Iterator<Node> it_i_j = amDataPerm.get(i).getList().get(j).getList().iterator();
				        while(it_i_j.hasNext()){
				        	Node n_i_j_k = (Node)it_i_j.next();
				            if(n_i_j_k.getList().size() == 0 ){
				            	it_i_j.remove();
				            }        
				        }
					}
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
				else if(amDataPerm.get(i).getId().equals("2")) {   //市属
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
				else if(amDataPerm.get(i).getId().equals("1")) {   //部署
					for(j = 0; j < amDataPerm.get(i).getList().size(); j++) {   //部属主管部门
						Iterator<Node> it_i_j = amDataPerm.get(i).getList().get(j).getList().iterator();
				        while(it_i_j.hasNext()){
				        	Node n_i_j_k = (Node)it_i_j.next();
				            if(n_i_j_k.getList().size() == 0 ){
				            	it_i_j.remove();
				            }        
				        }
					}
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
				else if(amDataPerm.get(i).getId().equals("0")) {   //其他
					for(j = 0; j < amDataPerm.get(i).getList().size(); j++) {   //其他
						Iterator<Node> it_i_j = amDataPerm.get(i).getList().get(j).getList().iterator();
				        while(it_i_j.hasNext()){
				        	Node n_i_j_k = (Node)it_i_j.next();
				            if(n_i_j_k.getList().size() == 0 ){
				            	it_i_j.remove();
				            }        
				        }
					}
					Iterator<Node> it_i = amDataPerm.get(i).getList().iterator();
			        while(it_i.hasNext()){
			        	Node n_i_j = (Node)it_i.next();
			            if(n_i_j.getList().size() == 0 ){
			            	it_i.remove();
			            }        
			        }
				}
			}
			Iterator<Node> it = amDataPerm.iterator();
	        while(it.hasNext()){
	        	Node n = (Node)it.next();
	            if(n.getList().size() == 0 ){
	            	it.remove();
	            }        
	        }
			//设置数据
			adpDto.setAmDataPerm(amDataPerm);
			//消息ID
			adpDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {
			logger.info("获取学校名称编码数据失败！");
		}
		
		return adpDto;
	}
	
	// 数据权限模型函数
	public AmDataPermDTO appModFunc(String token, String userName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmDataPermDTO adpDto = null;
		if(isRealData) {       //真实数据
			if(userName != null) {    //当前用户权限
				//从数据源ds2的数据表t_edu_bd_user中查找用户信息
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
			    if(tebuDo.getUserAccount() != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配数据权限
			    		adpDto = assignDataPerm(tebuDo.getId(), db1Service, db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有数据权限
			    		adpDto = allDataPerm(db1Service, codes);
			    	}
			    }
			}
			else {                    //当前token对应用户权限
				//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
			    if(tebuDo.getUserAccount() != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配数据权限
			    		adpDto = assignDataPerm(tebuDo.getId(), db1Service, db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有数据权限
			    		adpDto = allDataPerm(db1Service, codes);
			    	}
			    }
			}
		}
		else {    //模拟数据
			//模拟数据函数
			adpDto = SimuDataFunc();
		}		

		return adpDto;
	}
}
