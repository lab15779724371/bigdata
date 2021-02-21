package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleManage;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//角色管理应用模型
public class AmRoleManageAppMod {
	private static final Logger logger = LogManager.getLogger(AmRoleManageAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
			
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] roleId_Array = {"5389de487-8913-40c8-bcce-7a538be4ec29", "6499de487-8913-40c8-bcce-7a538be4ec29"};
	String[] roleName_Array = {"局长", "副局长"};
	String[] roleType_Array = {"监管部门", "监管部门"};
	String[] roleDiscrip_Array = {"", ""};
	String[] creator_Array = {"shsjw", "shsjw"};
	String[] createTime_Array = {"2018/10/11 18:02", "2018/10/11 18:12"};
	int[] isRelAccount_Array = {0, 1};
	
	//模拟数据函数
	private AmRoleManageDTO SimuDataFunc() {
		AmRoleManageDTO armDto = new AmRoleManageDTO();
		//时戳
		armDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//角色管理模拟数据
		List<AmRoleManage> amRoleManage = new ArrayList<>();
		//赋值
		for (int i = 0; i < roleId_Array.length; i++) {
			AmRoleManage arm = new AmRoleManage();
			arm.setRoleId(roleId_Array[i]);
			arm.setRoleName(roleName_Array[i]);
			arm.setRoleType(roleType_Array[i]);
			arm.setRoleDiscrip(roleDiscrip_Array[i]);
			arm.setCreator(creator_Array[i]);
			arm.setCreateTime(createTime_Array[i]);
			arm.setIsRelAccount(isRelAccount_Array[i]);
			amRoleManage.add(arm);
		}
		//设置数据
		armDto.setAmRoleManage(amRoleManage);
		//消息ID
		armDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return armDto;
	}
	
	// 角色管理函数
	private AmRoleManageDTO amRoleManage(String roleName, int roleType, Db2Service db2Service) {
		AmRoleManageDTO armDto = null;
		//从数据源ds2的数据表t_edu_bd_user中查找所有用户信息
		Map<String, Integer> roleIdToUserMap = new HashMap<>();
	    List<TEduBdUserDo> tebuDoList = db2Service.getAllBdUserInfo();
	    if(tebuDoList != null) {
	    	for(int i = 0; i < tebuDoList.size(); i++) {
	    		if(tebuDoList.get(i).getRoleId() != null) {
	    			roleIdToUserMap.put(tebuDoList.get(i).getRoleId(), 1);
	    		}
	    	}
	    }
		//从数据源ds2的数据表t_edu_bd_role中查找所有角色信息
		List<TEduBdRoleDo> tebrDoList = db2Service.getAllBdRoleInfo();
		if(tebrDoList != null) {
			armDto = new AmRoleManageDTO();
			List<AmRoleManage> amRoleManage = new ArrayList<>();
			for(int k = 0; k < tebrDoList.size(); k++) {
				AmRoleManage arm = new AmRoleManage();
				arm.setRoleId(tebrDoList.get(k).getId());
				arm.setRoleName(tebrDoList.get(k).getRoleName());                                                //角色名
				arm.setRoleType(AppModConfig.roleTypeIdToNameMap.get(tebrDoList.get(k).getRoleType()));          //类型
				arm.setRoleDiscrip(tebrDoList.get(k).getDiscrip());                                              //描述
				arm.setCreator(tebrDoList.get(k).getCreator());                                                  //创建人
				arm.setCreateTime(tebrDoList.get(k).getCreateTime());                                            //创建日期
				arm.setIsRelAccount(0);                                                                          //是否关联账号，0:未关联账号，1:已关联账号
				if(roleIdToUserMap.containsKey(tebrDoList.get(k).getId())) {
					arm.setIsRelAccount(1);
				}
				//条件判断
				boolean isAdd = true;
				int[] flIdxs = new int[2];
				//判断角色名称（判断索引0）
				if(roleName != null) {
					if(!arm.getRoleName().equals(roleName))
						flIdxs[0] = -1;
				}
				//判断角色类型（判断索引1）
				if(roleType != -1) {
					if(AppModConfig.roleTypeNameToIdMap.containsKey(arm.getRoleType())) {
						int curRoleType = AppModConfig.roleTypeNameToIdMap.get(arm.getRoleType());
						if(curRoleType != roleType)
							flIdxs[1] = -1;
					}
				}
				//总体条件判断
				for(int i = 0; i < flIdxs.length; i++) {
					if(flIdxs[i] == -1) {
						isAdd = false;
						break;
					}
				}
				//是否满足条件
				if(isAdd)
					amRoleManage.add(arm);
			}
			// 设置返回数据
			armDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 分页
			PageBean<AmRoleManage> pageBean = new PageBean<AmRoleManage>(amRoleManage, curPageNum, pageSize);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageTotal(pageBean.getTotalCount());
			pageInfo.setCurPageNum(curPageNum);
			armDto.setPageInfo(pageInfo);
			// 设置数据
			armDto.setAmRoleManage(pageBean.getCurPageData());
			// 消息ID
			armDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		
		return armDto;
	}
	
	// 角色管理模型函数
	public AmRoleManageDTO appModFunc(String roleName, String roleType, String page, String pageSize, Db2Service db2Service, int[] codes) {
		AmRoleManageDTO armDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//角色类型，1:监管部门，2:学校
			int curRoleType = -1;
			if(roleType != null)
				curRoleType = Integer.parseInt(roleType);
			// 判断参数形式
			if (db2Service != null) {    
				// 角色管理函数
				armDto = amRoleManage(roleName, curRoleType, db2Service);
			}
			else {
				logger.info("访问接口参数非法！");
			}						
		}
		else {    //模拟数据
			//模拟数据函数
			armDto = SimuDataFunc();
		}		

		return armDto;
	}
}
