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
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmL2MenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//二级菜单权限应用模型
public class AmL2MenuPermAppMod {
	private static final Logger logger = LogManager.getLogger(AmL2MenuPermAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化	
	String[] id_Array = {"fa0de487-8913-40c8-bcce-7a538be4ec29", "0b1de487-8913-40c8-bcce-7a538be4ec29", "1c2de487-8913-40c8-bcce-7a538be4ec29", "2d3de487-8913-40c8-bcce-7a538be4ec29", "3e4de487-8913-40c8-bcce-7a538be4ec29", "4f5de487-8913-40c8-bcce-7a538be4ec29"};
	String[] label_Array = {"排菜数据", "用料计划确认", "配货计划操作", "菜品留样", "餐厨垃圾", "废弃油脂"};
	
	//模拟数据函数
	private AmL2MenuPermDTO SimuDataFunc() {
		AmL2MenuPermDTO al2mpDto = new AmL2MenuPermDTO();
		//时戳
		al2mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//二级菜单权限模拟数据
		List<IdLabel> amL2MenuPerm = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			IdLabel al2mp = new IdLabel();
			al2mp.setId(id_Array[i]);
			al2mp.setLabel(label_Array[i]);
			amL2MenuPerm.add(al2mp);
		}
		//设置数据
		al2mpDto.setAmL2MenuPerm(amL2MenuPerm);
		//消息ID
		al2mpDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return al2mpDto;
	}
	
	//所有二级菜单权限
	AmL2MenuPermDTO allL2MenuPerm(String l1MenuId, Db2Service db2Service, int[] codes) {
		AmL2MenuPermDTO al2mpDto = null;
		//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别和父菜单ID
	    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(2, l1MenuId);
	    if(tebmpDoList != null) {
	    	al2mpDto = new AmL2MenuPermDTO();
	    	//时戳
	    	al2mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	    	List<IdLabel> amL2MenuPerm = new ArrayList<>();
	    	for(int i = 0; i < tebmpDoList.size(); i++) {
	    		IdLabel al2mp = new IdLabel();
	    		al2mp.setId(tebmpDoList.get(i).getId());
	    		al2mp.setLabel(tebmpDoList.get(i).getMenuName());
	    		amL2MenuPerm.add(al2mp);
	    	}
	    	//设置数据
	    	al2mpDto.setAmL2MenuPerm(amL2MenuPerm);
			//消息ID
	    	al2mpDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
	    }
	    else {
			logger.info("获取二级菜单数据失败！");
		}
		
		return al2mpDto;
	}	
	
	//分配二级菜单权限
	AmL2MenuPermDTO assignL1MenuPerm(String userId, String l1MenuId, Db2Service db2Service, int[] codes) {
		AmL2MenuPermDTO al2mpDto = null;
		int i;
		//从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
	    List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(userId, 2);
	    if(tebupDoList != null && tebupDoList.size() > 0) {         //存在菜单权限
	    	Map<String, String> permIdToLabelMap = new HashMap<>();
	    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
		    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(2, l1MenuId);
		    if(tebmpDoList != null) {    //二级菜单权限
		    	for(i = 0; i < tebmpDoList.size(); i++) {
		    		permIdToLabelMap.put(tebmpDoList.get(i).getId(), tebmpDoList.get(i).getMenuName());
		    	}
		    	al2mpDto = new AmL2MenuPermDTO();
		    	//时戳
		    	al2mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		    	List<IdLabel> amL2MenuPerm = new ArrayList<>();
		    	for(i = 0; i < tebupDoList.size(); i++)  {
		    		if(permIdToLabelMap.containsKey(tebupDoList.get(i).getPermId())) {
		    			IdLabel al2mp = new IdLabel();
		    			al2mp.setId(tebupDoList.get(i).getPermId());
		    			al2mp.setLabel(permIdToLabelMap.get(tebupDoList.get(i).getPermId()));
		    			amL2MenuPerm.add(al2mp);
		    		}
		    	}
		    	//设置数据
		    	al2mpDto.setAmL2MenuPerm(amL2MenuPerm);
				//消息ID
		    	al2mpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
		    }		    
	    }
	    else {       //不存在菜单权限，则获取全部权限
	    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
		    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(2, l1MenuId);
		    if(tebmpDoList != null) {    //二级菜单权限
		    	List<IdLabel> amL2MenuPerm = new ArrayList<>();
		    	for(i = 0; i < tebmpDoList.size(); i++) {
		    		IdLabel al2mp = new IdLabel();
	    			al2mp.setId(tebmpDoList.get(i).getId());
	    			al2mp.setLabel(tebmpDoList.get(i).getMenuName());
	    			amL2MenuPerm.add(al2mp);
		    	}
		    	al2mpDto = new AmL2MenuPermDTO();
		    	//时戳
		    	al2mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		    	//设置数据
		    	al2mpDto.setAmL2MenuPerm(amL2MenuPerm);
				//消息ID
		    	al2mpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
		    }
	    }
		
		return al2mpDto;
	}	
	
	// 二级菜单权限模型函数
	public AmL2MenuPermDTO appModFunc(String token, String l1MenuId, String userName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmL2MenuPermDTO al2mpDto = null;
		if(isRealData) {       //真实数据
			if(userName != null) {    //当前用户权限
				//从数据源ds2的数据表t_edu_bd_user中查找用户信息
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
			    if(tebuDo != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配二级菜单权限
			    		al2mpDto = assignL1MenuPerm(tebuDo.getId(), l1MenuId, db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有二级菜单权限
			    		al2mpDto = allL2MenuPerm(l1MenuId, db2Service, codes);
			    	}
			    }
			}
			else {                    //当前token对应用户权限
				//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
			    if(tebuDo != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配二级菜单权限
			    		al2mpDto = assignL1MenuPerm(tebuDo.getId(), l1MenuId, db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有二级菜单权限
			    		al2mpDto = allL2MenuPerm(l1MenuId, db2Service, codes);
			    	}
			    }
			}
		}
		else {    //模拟数据
			//模拟数据函数
			al2mpDto = SimuDataFunc();
		}		

		return al2mpDto;
	}
}
