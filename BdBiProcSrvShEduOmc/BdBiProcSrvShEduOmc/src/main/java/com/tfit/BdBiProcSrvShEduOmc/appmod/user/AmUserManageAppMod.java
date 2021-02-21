package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserManage;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//用户管理应用模型
public class AmUserManageAppMod {
	private static final Logger logger = LogManager.getLogger(AmUserManageAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
			
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] userName_Array = {"xhqjyj", "xhqjyj001", "xhqjyj002"};
	String[] fullName_Array = {"admin", "admin001", "admin002"};
	String[] roleName_Array = {"局长", "校长", "科员"};
	String[] accountType_Array = {"普通账号", "管理员账号", "管理员账号"};
	String[] userOrg_Array = {"徐汇区教育局", "徐汇区教育局", "徐汇区教育局"};
	String[] mobPhone_Array = {"13666666666", "13666666667", "13666666668"};
	String[] userStatus_Array = {"启用", "启用", "启用"};
	String[] creator_Array = {"shsjw", "shsjw", "shsjw"};
	String[] createTime_Array = {"2018/10/11 18:02", "2018/10/11 18:02", "2018/10/11 18:02"};
	String[] lastLoginTime_Array = {"2018/10/11 18:02", "2018/10/11 18:02", "2018/10/11 18:02"};
	
	//模拟数据函数
	private AmUserManageDTO SimuDataFunc() {
		AmUserManageDTO aumDto = new AmUserManageDTO();
		//时戳
		aumDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//用户管理模拟数据
		List<AmUserManage> amUserManage = new ArrayList<>();
		//赋值
		for (int i = 0; i < userName_Array.length; i++) {
			AmUserManage aum = new AmUserManage();
			aum.setUserName(userName_Array[i]);
			aum.setFullName(fullName_Array[i]);
			aum.setRoleName(roleName_Array[i]);
			aum.setAccountType(accountType_Array[i]);
			aum.setUserOrg(userOrg_Array[i]);
			aum.setMobPhone(mobPhone_Array[i]);
			aum.setUserStatus(userStatus_Array[i]);
			aum.setCreator(creator_Array[i]);
			aum.setCreateTime(createTime_Array[i]);
			aum.setLastLoginTime(lastLoginTime_Array[i]);
			amUserManage.add(aum);
		}
		//设置数据
		aumDto.setAmUserManage(amUserManage);
		//消息ID
		aumDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return aumDto;
	}
	
	// 用户管理函数
	private AmUserManageDTO amUserManage(String token, String userName, String fullName, String roleName, String userOrg, int accountType, int userStatus, Db2Service db2Service) {
		AmUserManageDTO aumDto = null;
		//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
		TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
		List<TEduBdUserDo> tebuDoList = null;
		int k, i;
		if(tebuDo != null) {
			//显示该账户的所有子账号
			//从数据源ds2的数据表t_edu_bd_user中查找所有用户信息以父账户
			if(CommonUtil.isEmpty(tebuDo.getParentId())){
				tebuDoList = db2Service.getAllBdUserInfoByParentId(tebuDo.getId(),null,tebuDo.getUserType());
			}else {
				tebuDoList = db2Service.getAllBdUserInfoByParentId(null,tebuDo.getId(),tebuDo.getUserType());
			}
			if(tebuDoList != null) {
				aumDto = new AmUserManageDTO();
				List<AmUserManage> amUserManage = new ArrayList<>();
				for(k = 0; k < tebuDoList.size(); k++) {
					
					if("shsjw".equalsIgnoreCase(tebuDoList.get(k).getUserAccount()))
						continue;
					
					//从数据源ds1的数据表t_edu_bd_role中查找角色信息以id
				    TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleId(tebuDoList.get(k).getRoleId());
					AmUserManage aum = new AmUserManage();
					aum.setUserName(tebuDoList.get(k).getUserAccount());                                              //用户名
					aum.setFullName(tebuDoList.get(k).getName());                                                     //姓名
					if(tebrDo != null)  {                                                                             //角色
						aum.setRoleName(tebrDo.getRoleName());
					}
					aum.setAccountType(AppModConfig.accountTypeIdToNameMap.get(tebuDoList.get(k).getIsAdmin()));      //账号类型
					aum.setUserOrg(tebuDoList.get(k).getOrgName());                                                   //单位
					aum.setMobPhone(tebuDoList.get(k).getMobilePhone());                                              //手机
					aum.setUserStatus(AppModConfig.userStatusIdToNameMap.get(tebuDoList.get(k).getForbid()));         //状态
					aum.setCreator(tebuDoList.get(k).getCreator());                                                   //创建人
					aum.setCreateTime(tebuDoList.get(k).getCreateTime());                                             //创建日期
					aum.setLastLoginTime(tebuDoList.get(k).getLastLoginTime());                                       //最后登录时间
					//条件判断
					boolean isAdd = true;
					int[] flIdxs = new int[6];
					//判断用户名（判断索引0）
					if(userName != null) {
						if(aum.getUserName().indexOf(userName) == -1)
							flIdxs[0] = -1;
					}
					//判断姓名（判断索引1）
					if(fullName != null) {
						if(aum.getFullName().indexOf(fullName) == -1)
							flIdxs[1] = -1;
					}
					//判断角色（判断索引2）
					if(roleName != null) {
						if(aum.getRoleName() != null) {
							if(!aum.getRoleName().equals(roleName))
								flIdxs[2] = -1;
						}
						else
							flIdxs[2] = -1;
					}
					//判断单位（判断索引3）
					if(userOrg != null) {
						if(!aum.getUserOrg().equals(userOrg))
							flIdxs[3] = -1;
					}
					//判断账号类型（判断索引4）
					if(accountType != -1) {
						if(aum.getAccountType() != null) {
							if(AppModConfig.accountTypeNameToIdMap.containsKey(aum.getAccountType())) {
								int curAccountType = AppModConfig.accountTypeNameToIdMap.get(aum.getAccountType());
								if(curAccountType != accountType)
									flIdxs[4] = -1;
							}
							else
								flIdxs[4] = -1;
						}
						else
							flIdxs[4] = -1;
					}
					//判断状态（判断索引5）
					if(userStatus != -1) {
						if(AppModConfig.userStatusNameToIdMap.containsKey(aum.getUserStatus())) {
							int curUserStatus = AppModConfig.userStatusNameToIdMap.get(aum.getUserStatus());
							if(curUserStatus != userStatus)
								flIdxs[5] = -1;
						}
						else
							flIdxs[5] = -1;
					}
					//总体条件判断
					for(i = 0; i < flIdxs.length; i++) {
						if(flIdxs[i] == -1) {
							isAdd = false;
							break;
						}
					}
					//是否满足条件
					if(isAdd)
						amUserManage.add(aum);					
				}
				// 设置返回数据
				aumDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				// 分页
				PageBean<AmUserManage> pageBean = new PageBean<AmUserManage>(amUserManage, curPageNum, pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				aumDto.setPageInfo(pageInfo);
				// 设置数据
				aumDto.setAmUserManage(pageBean.getCurPageData());
				// 消息ID
				aumDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();

			}
		}
		
		return aumDto;
	}
	
	// 用户管理模型函数
	public AmUserManageDTO appModFunc(String token, String userName, String fullName, String roleName, String userOrg, String accountType, String userStatus, String page, String pageSize, Db2Service db2Service, int[] codes) {
		AmUserManageDTO aumDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//账号类型
			int curAccountType = -1;
			if(accountType != null)
				curAccountType = Integer.parseInt(accountType);
			//状态
			int curUserStatus = -1;
			if(userStatus != null)
				curUserStatus = Integer.parseInt(userStatus);
			// 判断参数形式
			if (token != null && db2Service != null) {    
				// 用户管理函数
				aumDto = amUserManage(token, userName, fullName, roleName, userOrg, curAccountType, curUserStatus, db2Service);
			}
			else {
				logger.info("访问接口参数非法！");
			}						
		}
		else {    //模拟数据
			//模拟数据函数
			aumDto = SimuDataFunc();
		}		

		return aumDto;
	}
}
