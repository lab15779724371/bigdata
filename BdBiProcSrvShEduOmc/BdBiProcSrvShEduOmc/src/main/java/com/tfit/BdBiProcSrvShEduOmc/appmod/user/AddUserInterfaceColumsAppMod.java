package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdInterfaceColumnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColums;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColumsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//保存个人资料应用模型
public class AddUserInterfaceColumsAppMod {
	private static final Logger logger = LogManager.getLogger(AddUserInterfaceColumsAppMod.class.getName());
	
	//保存个人资料应用模型函数
  	public AddUserInterfaceColumsDTO appModFunc(String token,AddUserInterfaceColums userColumsInput, Db2Service db2Service) {
  		
  		AddUserInterfaceColumsDTO asuiDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			
  			TEduBdUserDo loginUser =  AppModConfig.getUserByToken(token, db2Service);
  			if(loginUser==null) {
  				return null;
  			}
  			
  			int ret = -1;
  			EduBdInterfaceColumnsDo interfaceColums = db2Service.getByInterfaceName(loginUser.getId(),userColumsInput.getInterfaceName());
  			if(interfaceColums==null || interfaceColums.getId()==null) {
  				//新增
  				interfaceColums = new EduBdInterfaceColumnsDo();
  				//取号
  				Integer maxId = db2Service.getInterfaceColumnsMaxId();
  				if(maxId==null) {
  					maxId = 0;
  				}
  				interfaceColums.setId(maxId+1);
  				interfaceColums.setInterfaceName(userColumsInput.getInterfaceName());
  				interfaceColums.setColumns(userColumsInput.getColumns());
  				interfaceColums.setCreateTime(new Date());
  				interfaceColums.setCreator(loginUser.getId());
  				interfaceColums.setLastUpdateTime(new Date());
  				ret = db2Service.addUserInterfaceColums(interfaceColums);
  			}else {
  			    //修改
  				interfaceColums.setColumns(userColumsInput.getColumns());
  				interfaceColums.setLastUpdateTime(new Date());
  				ret = db2Service.updateUserInterfaceColumsByPrimaryKey(interfaceColums);
  			}
			//更新记录到数据源ds1的数据表t_edu_bd_user中
			if(ret > 0) {
				interfaceColums = db2Service.getByInterfaceName(loginUser.getId(),userColumsInput.getInterfaceName());
				asuiDto = new AddUserInterfaceColumsDTO();
				//时戳
				asuiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				if(interfaceColums!=null) {
			    	//数据
					AddUserInterfaceColums userColumsOut = new AddUserInterfaceColums();
					userColumsOut.setColumns(interfaceColums.getColumns());
					userColumsOut.setInterfaceName(interfaceColums.getInterfaceName());
			    	//设置数据
			    	asuiDto.setUserColums(userColumsOut);
				}
				//消息ID
		    	asuiDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return asuiDto;
  	}
}
