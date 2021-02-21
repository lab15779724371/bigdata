package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//角色详情应用模型
public class AmRoleDetAppMod {
	private static final Logger logger = LogManager.getLogger(AmRoleDetAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//变量数据初始化
	String roleName = "局长";
	int roleType = 1;
	String roleDiscrip = null;
	String creator = "jizheng";
	String createTime = "2016-07-14 09:51:35";
	
	//模拟数据函数
	private AmRoleDetDTO SimuDataFunc() {
		AmRoleDetDTO aadDto = new AmRoleDetDTO();
		//时戳
		aadDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//角色详情模拟数据
		AmRoleDet amAccountDet = new AmRoleDet();
		//赋值
		amAccountDet.setRoleName(roleName);
		amAccountDet.setRoleType(roleType);
		amAccountDet.setRoleDiscrip(roleDiscrip);
		amAccountDet.setCreator(creator);
		amAccountDet.setCreateTime(createTime);
		//设置数据
		aadDto.setAmRoleDet(amAccountDet);
		//消息ID
		aadDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return aadDto;
	}
	
	// 角色详情模型函数
	public AmRoleDetDTO appModFunc(String roleId, Db2Service db2Service, int[] codes) {
		AmRoleDetDTO aadDto = null;
		if(isRealData) {       //真实数据				
  			if(roleId != null) {      //角色ID非空
  				 //从数据源ds2的数据表t_edu_bd_role中查找角色信息以id
  			    TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleId(roleId);
  			    if(tebrDo.getId() != null) {
  			    	aadDto = new AmRoleDetDTO();
  			    	//时戳
  			    	aadDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    	//角色详情模拟数据
  			    	AmRoleDet amAccountDet = new AmRoleDet();
  			    	//赋值
  			    	amAccountDet.setRoleName(tebrDo.getRoleName());
  			    	amAccountDet.setRoleType(tebrDo.getRoleType());
  			    	amAccountDet.setRoleDiscrip(tebrDo.getDiscrip());
  			    	amAccountDet.setCreator(tebrDo.getCreator());
  			    	amAccountDet.setCreateTime(tebrDo.getCreateTime());		
  			    	//设置数据
  			    	aadDto.setAmRoleDet(amAccountDet);
  			    	//消息ID
  			    	aadDto.setMsgId(AppModConfig.msgId);
  			    	AppModConfig.msgId++;
  			    	// 消息id小于0判断
  			    	AppModConfig.msgIdLessThan0Judge();
  			    }
  			    else {
  			    	codes[0] = 2025;
  			    	logger.info("数据库操作异常");
  			    }
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
