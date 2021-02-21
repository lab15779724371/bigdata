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
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmL1MenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//一级菜单权限应用模型
public class AmL1MenuPermAppMod {
	private static final Logger logger = LogManager.getLogger(AmL1MenuPermAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化	
	String[] id_Array = {"506de487-8913-40c8-bcce-7a538be4ec29", "617de487-8913-40c8-bcce-7a538be4ec29", "728de487-8913-40c8-bcce-7a538be4ec29", "839de487-8913-40c8-bcce-7a538be4ec29", "94ade487-8913-40c8-bcce-7a538be4ec29", "a5bde487-8913-40c8-bcce-7a538be4ec29", "b6cde487-8913-40c8-bcce-7a538be4ec29", "c7dde487-8913-40c8-bcce-7a538be4ec29", "d8ede487-8913-40c8-bcce-7a538be4ec29", "e9fde487-8913-40c8-bcce-7a538be4ec29"};
	String[] label_Array = {"信息管理", "信息预警", "投诉举报", "基础数据", "信息共享", "应急指挥", "发布通报", "综合分析", "教育培训", "账号管理"};
	
	//模拟数据函数
	private AmL1MenuPermDTO SimuDataFunc() {
		AmL1MenuPermDTO al1mpDto = new AmL1MenuPermDTO();
		//时戳
		al1mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//一级菜单权限模拟数据
		List<IdLabel> amL1MenuPerm = new ArrayList<>();
		//赋值
		for (int i = 0; i < id_Array.length; i++) {
			IdLabel al1mp = new IdLabel();
			al1mp.setId(id_Array[i]);
			al1mp.setLabel(label_Array[i]);
			amL1MenuPerm.add(al1mp);
		}
		//设置数据
		al1mpDto.setAmL1MenuPerm(amL1MenuPerm);
		//消息ID
		al1mpDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return al1mpDto;
	}
	
	//所有一级菜单权限
	AmL1MenuPermDTO allL1MenuPerm(Db2Service db2Service, int[] codes) {
		AmL1MenuPermDTO al1mpDto = null;
		//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
	    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(1);
	    if(tebmpDoList != null) {
	    	al1mpDto = new AmL1MenuPermDTO();
	    	//时戳
	    	al1mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	    	List<IdLabel> amL1MenuPerm = new ArrayList<>();
	    	for(int i = 0; i < tebmpDoList.size(); i++) {
	    		IdLabel al1mp = new IdLabel();
	    		al1mp.setId(tebmpDoList.get(i).getId());
	    		al1mp.setLabel(tebmpDoList.get(i).getMenuName());
	    		amL1MenuPerm.add(al1mp);
	    	}
	    	//设置数据
	    	al1mpDto.setAmL1MenuPerm(amL1MenuPerm);
			//消息ID
	    	al1mpDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
	    }
	    else {
			logger.info("获取一级菜单数据失败！");
		}
		
		return al1mpDto;
	}
	
	//分配一级菜单权限
	AmL1MenuPermDTO assignL1MenuPerm(String userId, Db2Service db2Service, int[] codes) {
		AmL1MenuPermDTO al1mpDto = null;
		int i;
		//从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
	    List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(userId, 2);
	    if(tebupDoList != null && tebupDoList.size() > 0) {       //存在菜单权限
	    	Map<String, String> permIdToLabelMap = new HashMap<>();
	    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
		    List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(1);
		    if(tebmpDoList != null) {
		    	for(i = 0; i < tebmpDoList.size(); i++) {
		    		permIdToLabelMap.put(tebmpDoList.get(i).getId(), tebmpDoList.get(i).getMenuName());
		    	}
		    	al1mpDto = new AmL1MenuPermDTO();
		    	//时戳
		    	al1mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		    	List<IdLabel> amL1MenuPerm = new ArrayList<>();
		    	for(i = 0; i < tebupDoList.size(); i++)  {
		    		if(permIdToLabelMap.containsKey(tebupDoList.get(i).getPermId())) {
		    			IdLabel al1mp = new IdLabel();
		    			al1mp.setId(tebupDoList.get(i).getPermId());
		    			al1mp.setLabel(permIdToLabelMap.get(tebupDoList.get(i).getPermId()));
		    			amL1MenuPerm.add(al1mp);
		    		}
		    	}
		    	//设置数据
		    	al1mpDto.setAmL1MenuPerm(amL1MenuPerm);
				//消息ID
		    	al1mpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
		    }
	    }
	    else {       //不存在菜单权限，则获取全部权限
	    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
	    	List<TEduBdMenuDo> tebmpDoList = db2Service.getBdMenuInfoByLevel(1);
	    	List<IdLabel> amL1MenuPerm = new ArrayList<>();
	    	if(tebmpDoList != null) {
	    		for(i = 0; i < tebmpDoList.size(); i++) {
		    		IdLabel al1mp = new IdLabel();
	    			al1mp.setId(tebmpDoList.get(i).getId());
	    			al1mp.setLabel(tebmpDoList.get(i).getMenuName());
	    			amL1MenuPerm.add(al1mp);
		    	}
		    	al1mpDto = new AmL1MenuPermDTO();
		    	//时戳
		    	al1mpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		    	//设置数据
		    	al1mpDto.setAmL1MenuPerm(amL1MenuPerm);
				//消息ID
		    	al1mpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
		    }
	    }
		
		return al1mpDto;
	}
	
	// 一级菜单权限模型函数
	public AmL1MenuPermDTO appModFunc(String token, String userName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		AmL1MenuPermDTO al1mpDto = null;
		if(isRealData) {       //真实数据
			if(userName != null) {    //当前用户权限
				//从数据源ds2的数据表t_edu_bd_user中查找用户信息
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
			    if(tebuDo != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配一级菜单权限
			    		al1mpDto = assignL1MenuPerm(tebuDo.getId(), db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有一级菜单权限
			    		al1mpDto = allL1MenuPerm(db2Service, codes);
			    	}
			    }
			}
			else {                    //当前token对应用户权限
				//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
			    if(tebuDo != null) {
			    	if(tebuDo.getParentId() != null) {   //子账号权限
			    		//分配一级菜单权限
			    		al1mpDto = assignL1MenuPerm(tebuDo.getId(), db2Service, codes);
			    	}
			    	else {              //最高级别账号权限
			    		//所有一级菜单权限
			    		al1mpDto = allL1MenuPerm(db2Service, codes);
			    	}
			    }
			}
		}
		else {    //模拟数据
			//模拟数据函数
			al1mpDto = SimuDataFunc();
		}		

		return al1mpDto;
	}
}
