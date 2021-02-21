package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdMenuDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmMenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.Node;

//菜单权限应用模型
public class AmMenuPermAppMod {
	private static final Logger logger = LogManager.getLogger(AmMenuPermAppMod.class.getName());
	
	@Autowired
    ObjectMapper objectMapper = new ObjectMapper();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	//测试邮件账号
	String[] testMails = {"185601074@qq.com", "zuoming_li@ssic.cn", "zheng_ji@ssic.cn"};
	
	//数组数据初始化
	String[] id_Array = {"506de487-8913-40c8-bcce-7a538be4ec29", "617de487-8913-40c8-bcce-7a538be4ec29", "728de487-8913-40c8-bcce-7a538be4ec29", "839de487-8913-40c8-bcce-7a538be4ec29"};
	String[] label_Array = {"业务监督", "信息预警", "投诉举报", "基础数据"};	
	String[] id2_Array = {"3", "5", "7", "9"};
	String[] label2_Array = {"业务数据汇总", "排菜数据", "证照预警", "投诉举报"};		
	//模拟数据函数
	private AmMenuPermDTO SimuDataFunc() {
		AmMenuPermDTO ampDto = new AmMenuPermDTO();
		//时戳
		ampDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//菜单权限模拟数据
		List<Node> amMenuPerm = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			Node node = new Node(id_Array[i], label_Array[i]);
			Node node2 = new Node(id2_Array[i], label2_Array[i]);
			node.add(node2);
			amMenuPerm.add(node);
		}
		//设置数据
		ampDto.setAmMenuPerm(amMenuPerm);
		//消息ID
		ampDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ampDto;
	}
	
	//菜单权限函数
	private AmMenuPermDTO amMenuPerm(String userName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmMenuPermDTO ampDto = null;
		int i, l0, curL0, l1, curL1, l2, curL2, l3, curL3, l4, curL4, l5, curL5;
		boolean bfindL0, bfindL1, bfindL2, bfindL3, bfindL4, bfindL5;
		//获取用户信息
		TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
		//获取菜单权限  			
		List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(tebuDo.getId(), 2);   //从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
		if(tebupDoList != null && tebupDoList.size() > 0) {
			ampDto = new AmMenuPermDTO();
			//菜单权限数据
			List<Node> amMenuPerm = new ArrayList<>();
			//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
			Map<String, Integer> permIdToLevelMap = new HashMap<>(), permIdToIdxMap = new HashMap<>();
			List<TEduBdMenuDo> tebmpDoList1 = db2Service.getBdMenuInfoByLevel(1);
			//菜单ID与菜单级别映射，菜单ID与菜单索引级别映射
			for(i = 0; i < tebmpDoList1.size(); i++) {
				permIdToLevelMap.put(tebmpDoList1.get(i).getId(), 1);      
				permIdToIdxMap.put(tebmpDoList1.get(i).getId(), i);
			}
			List<TEduBdMenuDo> tebmpDoList2 = db2Service.getBdMenuInfoByLevel(2);
			for(i = 0; i < tebmpDoList2.size(); i++) {
				permIdToLevelMap.put(tebmpDoList2.get(i).getId(), 2);
				permIdToIdxMap.put(tebmpDoList2.get(i).getId(), i);
			}
			List<TEduBdMenuDo> tebmpDoList3 = db2Service.getBdMenuInfoByLevel(3);
			for(i = 0; i < tebmpDoList3.size(); i++) {
				permIdToLevelMap.put(tebmpDoList3.get(i).getId(), 3);
				permIdToIdxMap.put(tebmpDoList3.get(i).getId(), i);
			}
			//生成多级菜单数据
			for(i = 0; i < tebupDoList.size(); i++) {
				if(permIdToLevelMap.containsKey(tebupDoList.get(i).getPermId())) {
					int level = permIdToLevelMap.get(tebupDoList.get(i).getPermId());
					if(level == 1) {         //1级菜单
						bfindL0 = false;
						curL0 = 0;
						String curId = tebupDoList.get(i).getPermId();
						int curIdx = permIdToIdxMap.get(tebupDoList.get(i).getPermId());
						String curLabel = tebmpDoList1.get(curIdx).getMenuName();
						for(l0 = 0; l0 < amMenuPerm.size(); l0++) {     //查找1级菜单
							if(amMenuPerm.get(l0).getId().equals(curId)) {
								curL0 = l0;
								bfindL0 = true;
								break;
							}
						}
						if(!bfindL0) {    //未查到则添加1级菜单
							Node nodeL0 = new Node(curId, curLabel);
							amMenuPerm.add(nodeL0);
							curL0 = amMenuPerm.size() - 1;
						}
					}
					else if(level == 2) {         //2级菜单
						String curId = tebupDoList.get(i).getPermId();
						int curIdx = permIdToIdxMap.get(tebupDoList.get(i).getPermId());
						String curLabel = tebmpDoList2.get(curIdx).getMenuName();
						//查找1级菜单
						bfindL0 = false;
						curL0 = 0;
						String curParId = tebmpDoList2.get(curIdx).getParentId();
						int curParIdx = permIdToIdxMap.get(curParId);
						String curParLabel = tebmpDoList1.get(curParIdx).getMenuName();
						for(l0 = 0; l0 < amMenuPerm.size(); l0++) {     //查找1级菜单
							if(amMenuPerm.get(l0).getId().equals(curParId)) {
								curL0 = l0;
								bfindL0 = true;
								break;
							}
						}
						if(!bfindL0) {    //未查到则添加1级菜单
							Node nodeL0 = new Node(curParId, curParLabel);
							amMenuPerm.add(nodeL0);
							curL0 = amMenuPerm.size() - 1;
						}
						//添加2级菜单
						bfindL1 = false;
						curL1 = 0;
						for(l1 = 0; l1 < amMenuPerm.get(curL0).getList().size(); l1++) {     //查找2级菜单
							if(amMenuPerm.get(curL0).getList().get(l1).getId().equals(curId)) {
								curL1 = l1;
								bfindL1 = true;
								break;
							}
						}
						if(!bfindL1) {    //未查到则添加2级菜单
							Node nodeL0_L1 = new Node(curId, curLabel);
							amMenuPerm.get(curL0).add(nodeL0_L1);
							curL1 = amMenuPerm.get(curL0).getList().size() - 1;
						}
					}
					else if(level == 3) {         //3级菜单
						
					}
				}
			}
			//设置数据
			ampDto.setAmMenuPerm(amMenuPerm);
			//消息ID
			ampDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {
			ampDto = new AmMenuPermDTO();
			//菜单权限数据
			List<Node> amMenuPerm = new ArrayList<>();
			//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
			Map<String, Integer> permIdToLevelMap = new HashMap<>(), permIdToIdxMap = new HashMap<>();
			List<TEduBdMenuDo> tebmpDoList1 = db2Service.getBdMenuInfoByLevel(1);
			//菜单ID与菜单级别映射，菜单ID与菜单索引级别映射
			for(i = 0; i < tebmpDoList1.size(); i++) {
				permIdToLevelMap.put(tebmpDoList1.get(i).getId(), 1);      
				permIdToIdxMap.put(tebmpDoList1.get(i).getId(), i);
			}
			List<TEduBdMenuDo> tebmpDoList2 = db2Service.getBdMenuInfoByLevel(2);
			for(i = 0; i < tebmpDoList2.size(); i++) {
				permIdToLevelMap.put(tebmpDoList2.get(i).getId(), 2);
				permIdToIdxMap.put(tebmpDoList2.get(i).getId(), i);
			}
			List<TEduBdMenuDo> tebmpDoList3 = db2Service.getBdMenuInfoByLevel(3);
			for(i = 0; i < tebmpDoList3.size(); i++) {
				permIdToLevelMap.put(tebmpDoList3.get(i).getId(), 3);
				permIdToIdxMap.put(tebmpDoList3.get(i).getId(), i);
			}
			List<TEduBdMenuDo> tebmpDoList = new ArrayList<>();
			tebmpDoList.addAll(tebmpDoList1);
			tebmpDoList.addAll(tebmpDoList2);
			tebmpDoList.addAll(tebmpDoList3);
			//生成多级菜单数据
			for(i = 0; i < tebmpDoList.size(); i++) {
				if(permIdToLevelMap.containsKey(tebmpDoList.get(i).getId())) {
					int level = permIdToLevelMap.get(tebmpDoList.get(i).getId());
					if(level == 1) {         //1级菜单
						bfindL0 = false;
						curL0 = 0;
						String curId = tebmpDoList.get(i).getId();
						int curIdx = permIdToIdxMap.get(tebmpDoList.get(i).getId());
						String curLabel = tebmpDoList1.get(curIdx).getMenuName();
						for(l0 = 0; l0 < amMenuPerm.size(); l0++) {     //查找1级菜单
							if(amMenuPerm.get(l0).getId().equals(curId)) {
								curL0 = l0;
								bfindL0 = true;
								break;
							}
						}
						if(!bfindL0) {    //未查到则添加1级菜单
							Node nodeL0 = new Node(curId, curLabel);
							amMenuPerm.add(nodeL0);
							curL0 = amMenuPerm.size() - 1;
						}
					}
					else if(level == 2) {         //2级菜单
						String curId = tebmpDoList.get(i).getId();
						int curIdx = permIdToIdxMap.get(tebmpDoList.get(i).getId());
						String curLabel = tebmpDoList2.get(curIdx).getMenuName();
						//查找1级菜单
						bfindL0 = false;
						curL0 = 0;
						String curParId = tebmpDoList2.get(curIdx).getParentId();
						int curParIdx = permIdToIdxMap.get(curParId);
						String curParLabel = tebmpDoList1.get(curParIdx).getMenuName();
						for(l0 = 0; l0 < amMenuPerm.size(); l0++) {     //查找1级菜单
							if(amMenuPerm.get(l0).getId().equals(curParId)) {
								curL0 = l0;
								bfindL0 = true;
								break;
							}
						}
						if(!bfindL0) {    //未查到则添加1级菜单
							Node nodeL0 = new Node(curParId, curParLabel);
							amMenuPerm.add(nodeL0);
							curL0 = amMenuPerm.size() - 1;
						}
						//添加2级菜单
						bfindL1 = false;
						curL1 = 0;
						for(l1 = 0; l1 < amMenuPerm.get(curL0).getList().size(); l1++) {     //查找2级菜单
							if(amMenuPerm.get(curL0).getList().get(l1).getId().equals(curId)) {
								curL1 = l1;
								bfindL1 = true;
								break;
							}
						}
						if(!bfindL1) {    //未查到则添加2级菜单
							Node nodeL0_L1 = new Node(curId, curLabel);
							amMenuPerm.get(curL0).add(nodeL0_L1);
							curL1 = amMenuPerm.get(curL0).getList().size() - 1;
						}
					}
					else if(level == 3) {         //3级菜单
						
					}
				}
			}
			//设置数据
			ampDto.setAmMenuPerm(amMenuPerm);
			//消息ID
			ampDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		
		return ampDto;
	}
	
	// 菜单权限模型函数
	public AmMenuPermDTO appModFunc(String token, String userName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmMenuPermDTO ampDto = null;
		if(isRealData) {       //真实数据
			// 按不同参数形式处理
			if (userName != null) {
				// 菜单权限函数
				ampDto = amMenuPerm(userName, db1Service, db2Service, codes);
			} 
			else {
				logger.info("访问接口参数非法！");
			}							
		}
		else {    //模拟数据
			//模拟数据函数
			ampDto = SimuDataFunc();
		}		

		return ampDto;
	}
}
