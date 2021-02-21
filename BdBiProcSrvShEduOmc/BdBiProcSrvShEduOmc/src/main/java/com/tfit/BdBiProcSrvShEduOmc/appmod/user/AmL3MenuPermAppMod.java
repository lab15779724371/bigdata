package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdMenuDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IdLabel;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmL3MenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//三级菜单权限（按钮权限）应用模型
public class AmL3MenuPermAppMod {
	private static final Logger logger = LogManager.getLogger(AmL3MenuPermAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化	
	String[] id_Array = {"506de487-8913-40c8-bcce-7a538be4ec29", "617de487-8913-40c8-bcce-7a538be4ec29"};
	String[] label_Array = {"查询", "导出"};
	
	//模拟数据函数
	private AmL3MenuPermDTO SimuDataFunc() {
		AmL3MenuPermDTO al3mpDto = new AmL3MenuPermDTO();
		//时戳
		al3mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//三级菜单权限（按钮权限）模拟数据
		List<IdLabel> amL1MenuPerm = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			IdLabel al3mp = new IdLabel();
			al3mp.setId(id_Array[i]);
			al3mp.setLabel(label_Array[i]);
			amL1MenuPerm.add(al3mp);
		}
		//设置数据
		al3mpDto.setAmL3MenuPerm(amL1MenuPerm);
		//消息ID
		al3mpDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return al3mpDto;
	}
	
	//所有三级菜单权限
	AmL3MenuPermDTO allL3MenuPerm(String l2MenuId, Db2Service db2Service, int[] codes) {
		AmL3MenuPermDTO al3mpDto = null;
		//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别和父菜单ID
	    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(3, l2MenuId);
	    if(tebmpDoList != null) {
	    	al3mpDto = new AmL3MenuPermDTO();
	    	//时戳
	    	al3mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	    	List<IdLabel> amL3MenuPerm = new ArrayList<>();
	    	for(int i = 0; i < tebmpDoList.size(); i++) {
	    		IdLabel al3mp = new IdLabel();
	    		al3mp.setId(tebmpDoList.get(i).getId());
	    		al3mp.setLabel(tebmpDoList.get(i).getMenuName());
	    		amL3MenuPerm.add(al3mp);
	    	}
	    	//设置数据
	    	al3mpDto.setAmL3MenuPerm(amL3MenuPerm);
			//消息ID
	    	al3mpDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
	    }
	    else {
			logger.info("获取三级菜单数据失败！");
		}
		
		return al3mpDto;
	}	
	
	//分配三级菜单权限
	AmL3MenuPermDTO assignL1MenuPerm(String userId, String l2MenuId, Db2Service db2Service, int[] codes) {
		AmL3MenuPermDTO al3mpDto = null;
		int i;
		//从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
	    List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(userId, 2);
	    if(tebupDoList != null && tebupDoList.size() > 0) {         //存在菜单权限
	    	Map<String, String> permIdToLabelMap = new HashMap<>();
	    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
		    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(3, l2MenuId);
		    if(tebmpDoList != null) {
		    	for(i = 0; i < tebmpDoList.size(); i++) {
		    		permIdToLabelMap.put(tebmpDoList.get(i).getId(), tebmpDoList.get(i).getMenuName());
		    	}
		    	al3mpDto = new AmL3MenuPermDTO();
		    	//时戳
		    	al3mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		    	List<IdLabel> amL3MenuPerm = new ArrayList<>();
		    	for(i = 0; i < tebupDoList.size(); i++)  {
		    		if(permIdToLabelMap.containsKey(tebupDoList.get(i).getPermId())) {
		    			IdLabel al3mp = new IdLabel();
		    			al3mp.setId(tebupDoList.get(i).getPermId());
		    			al3mp.setLabel(permIdToLabelMap.get(tebupDoList.get(i).getPermId()));
		    			amL3MenuPerm.add(al3mp);
		    		}
		    	}
		    	//设置数据
		    	al3mpDto.setAmL3MenuPerm(amL3MenuPerm);
				//消息ID
		    	al3mpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
		    }
	    }
	    else {       //不存在菜单权限，则获取全部权限
	    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
		    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(3, l2MenuId);
		    List<IdLabel> amL3MenuPerm = new ArrayList<>();
		    if(tebmpDoList != null) {
		    	for(i = 0; i < tebmpDoList.size(); i++) {
		    		IdLabel al3mp = new IdLabel();
	    			al3mp.setId(tebmpDoList.get(i).getId());
	    			al3mp.setLabel(tebmpDoList.get(i).getMenuName());
	    			amL3MenuPerm.add(al3mp);
		    	}
		    	al3mpDto = new AmL3MenuPermDTO();
		    	//时戳
		    	al3mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		    	//设置数据
		    	al3mpDto.setAmL3MenuPerm(amL3MenuPerm);
				//消息ID
		    	al3mpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
		    }
	    }
		
		return al3mpDto;
	}	
	
	// 三级菜单权限（按钮权限）模型函数
	public AmL3MenuPermDTO appModFunc(String token, String l2MenuId, String userName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmL3MenuPermDTO al3mpDto = null;
		if(isRealData) {       //真实数据
			if(userName != null) {    //当前用户权限
				//从数据源ds2的数据表t_edu_bd_user中查找用户信息
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
			    if(tebuDo != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配三级菜单权限
			    		al3mpDto = assignL1MenuPerm(tebuDo.getId(), l2MenuId, db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有三级菜单权限
			    		al3mpDto = allL3MenuPerm(l2MenuId, db2Service, codes);
			    	}
			    }
			}
			else {                    //当前token对应用户权限
				//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
			    if(tebuDo != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配三级菜单权限
			    		al3mpDto = assignL1MenuPerm(tebuDo.getId(), l2MenuId, db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有三级菜单权限
			    		al3mpDto = allL3MenuPerm(l2MenuId, db2Service, codes);
			    	}
			    }
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			al3mpDto = SimuDataFunc();
		}		

		return al3mpDto;
	}
}
