package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdMenuDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//账号详情应用模型
public class AmAccountDetAppMod {
	private static final Logger logger = LogManager.getLogger(AmAccountDetAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//变量数据初始化
	String userName = "xhqjyj";
	String fullName = "admin";
	String roleName = "局长";
	String accountType = "管理员账号";
	String userOrg = "徐汇区教育局";
	String mobPhone = "13666666666";
	String email = "xhqjyj@ssic.com";
	String userSrc = "阳光午餐";
	String userStatus = "可用";
	String creator = "shsjw";
	String createTime = "2018/10/11 18:02";
	String lastLoginTime = "2018/10/11 19:25";
	String password = "dsfsdfsdf";
	String fixPhone = "021-12458697";
	String fax = "02145879687";
	
	//模拟数据函数
	private AmAccountDetDTO SimuDataFunc() {
		AmAccountDetDTO aadDto = new AmAccountDetDTO();
		//时戳
		aadDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//账号详情模拟数据
		AmAccountDet amAccountDet = new AmAccountDet();
		//赋值
		amAccountDet.setUserName(userName);
		amAccountDet.setFullName(fullName);
		amAccountDet.setRoleName(roleName);
		amAccountDet.setAccountType(accountType);
		amAccountDet.setUserOrg(userOrg);
		amAccountDet.setMobPhone(mobPhone);
		amAccountDet.setEmail(email);
		amAccountDet.setUserSrc(userSrc);
		amAccountDet.setUserStatus(userStatus);
		amAccountDet.setCreator(creator);
		amAccountDet.setCreateTime(createTime);
		amAccountDet.setLastLoginTime(lastLoginTime);
		amAccountDet.setPassword(password);
		amAccountDet.setFixPhone(fixPhone);
		amAccountDet.setFax(fax);
		//设置数据
		aadDto.setAmAccountDet(amAccountDet);
		//消息ID
		aadDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return aadDto;
	}
	
	// 账号详情模型函数
	public AmAccountDetDTO appModFunc(String userName, Db2Service db2Service, int[] codes) {
		AmAccountDetDTO aadDto = null;
		if(isRealData) {       //真实数据
			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);  			
  			if(tebuDo.getUserAccount() != null) {      //用户名（账号）
  				//从数据源ds2的数据表t_edu_bd_role中查找角色信息以id
  				TEduBdRoleDo tebrDo = null;
  				if(tebuDo.getRoleId() != null)
  					tebrDo = db2Service.getBdRoleInfoByRoleId(tebuDo.getRoleId());
  				aadDto = new AmAccountDetDTO();
  				//时戳
  				aadDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  				//账号详情模拟数据
  				AmAccountDet amAccountDet = new AmAccountDet();
  				//赋值
  				amAccountDet.setIsAdmin(tebuDo.getIsAdmin());
  				amAccountDet.setUserName(tebuDo.getUserAccount());
  				amAccountDet.setFullName(tebuDo.getName());
  				if(tebrDo != null) {
  					amAccountDet.setRoleName(tebrDo.getRoleName());
  					amAccountDet.setRoleType(tebrDo.getRoleType());
  				}
  				else {
  					amAccountDet.setRoleName(null);
  					amAccountDet.setRoleType(1);
  				}
  				amAccountDet.setAccountType(AppModConfig.accountTypeIdToNameMap.get(tebuDo.getIsAdmin()));
  				amAccountDet.setUserOrg(tebuDo.getOrgId());
  				amAccountDet.setUserOrgId(tebuDo.getOrgId());
  				amAccountDet.setMobPhone(tebuDo.getMobilePhone());
  				amAccountDet.setEmail(tebuDo.getEmail());
  				amAccountDet.setUserSrc("大数据平台");
  				amAccountDet.setUserStatus(AppModConfig.userStatusIdToNameMap.get(tebuDo.getForbid()));
  				amAccountDet.setCreator(tebuDo.getCreator());
  				amAccountDet.setCreateTime(tebuDo.getCreateTime());
  				amAccountDet.setLastLoginTime(tebuDo.getLastLoginTime());
  				amAccountDet.setPassword(tebuDo.getPassword());
  				amAccountDet.setFixPhone(tebuDo.getFixPhone());
  				amAccountDet.setFax(tebuDo.getFax());
  				//获取数据权限
  				List<String> amDataPerm = new ArrayList<>();               //数据权限,元素为学校ID
  				List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(tebuDo.getId(), 1);    //从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
  				if(tebupDoList != null) {
  					for(int i = 0; i < tebupDoList.size(); i++) {
  						amDataPerm.add(tebupDoList.get(i).getPermId());
  					}
  				}
  				amAccountDet.setAmDataPerm(amDataPerm);
  				//获取菜单权限  			
  			    List<TEduBdUserPermDo> tebupDoList2 = db2Service.getAllBdUserPermInfo(tebuDo.getId(), 2);   //从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
  			    List<String> amL1MenuPerm = new ArrayList<>();             //一级菜单权限ID集合
  			    List<String> amL2MenuPerm = new ArrayList<>();             //二级菜单权限ID集合
  			    List<String> amL3MenuPerm = new ArrayList<>();             //三级菜单权限ID（按钮）集合
  			    if(tebupDoList2 != null && tebupDoList2.size() > 0) {         //存在菜单权限
  			    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
  			    	Map<String, Integer> permIdToLevelMap = new HashMap<>();
  				    List<TEduBdMenuDo> tebmpDoList1 = db2Service.getBdMenuInfoByLevel(1);
  				    for(int i = 0; i < tebmpDoList1.size(); i++) {
  				    	permIdToLevelMap.put(tebmpDoList1.get(i).getId(), 1);
  				    }
  				    List<TEduBdMenuDo> tebmpDoList2 = db2Service.getBdMenuInfoByLevel(2);
  				    for(int i = 0; i < tebmpDoList2.size(); i++) {
				    	permIdToLevelMap.put(tebmpDoList2.get(i).getId(), 2);
				    }
  				    List<TEduBdMenuDo> tebmpDoList3 = db2Service.getBdMenuInfoByLevel(3);
  				    for(int i = 0; i < tebmpDoList3.size(); i++) {
				    	permIdToLevelMap.put(tebmpDoList3.get(i).getId(), 3);
				    }
  			    	for(int i = 0; i < tebupDoList2.size(); i++) {
  			    		if(permIdToLevelMap.containsKey(tebupDoList2.get(i).getPermId())) {
  			    			int level = permIdToLevelMap.get(tebupDoList2.get(i).getPermId());
  			    			if(level == 1) {
  			    				amL1MenuPerm.add(tebupDoList2.get(i).getPermId());
  			    			}
  			    			else if(level == 2) {
  			    				amL2MenuPerm.add(tebupDoList2.get(i).getPermId());
  			    			}
  			    			else if(level == 3) {
  			    				amL3MenuPerm.add(tebupDoList2.get(i).getPermId());
  			    			}
  			    		}
  			    	}
  			    }
  			    else {       //不存在菜单权限，则获取全部权限
  			    	//从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
  				    List<TEduBdMenuDo> tebmpDoList1 = db2Service.getBdMenuInfoByLevel(1);
  				    for(int i = 0; i < tebmpDoList1.size(); i++) {
  				    	amL1MenuPerm.add(tebmpDoList1.get(i).getId());
  				    }
  				    List<TEduBdMenuDo> tebmpDoList2 = db2Service.getBdMenuInfoByLevel(2);
  				    for(int i = 0; i < tebmpDoList2.size(); i++) {
  				    	amL2MenuPerm.add(tebmpDoList2.get(i).getId());
				    }
  				    List<TEduBdMenuDo> tebmpDoList3 = db2Service.getBdMenuInfoByLevel(3);
  				    for(int i = 0; i < tebmpDoList3.size(); i++) {
  				    	amL3MenuPerm.add(tebmpDoList3.get(i).getId());
				    }
  			    }
  				amAccountDet.setAmL1MenuPerm(amL1MenuPerm);  				
  				amAccountDet.setAmL2MenuPerm(amL2MenuPerm);  				
  				amAccountDet.setAmL3MenuPerm(amL3MenuPerm);
  				//设置数据
  				aadDto.setAmAccountDet(amAccountDet);
  				//消息ID
  				aadDto.setMsgId(AppModConfig.msgId);
  				AppModConfig.msgId++;
  				// 消息id小于0判断
  				AppModConfig.msgIdLessThan0Judge();
  			}
  			else {
  				logger.info("访问接口参数非法！");
  			}
		}
		else {    //模拟数据
			//模拟数据函数
			aadDto = SimuDataFunc();
		}		

		return aadDto;
	}
}
