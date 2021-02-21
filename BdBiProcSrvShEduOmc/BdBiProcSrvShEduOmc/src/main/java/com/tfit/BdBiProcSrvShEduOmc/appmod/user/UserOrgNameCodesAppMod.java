package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserDataPermInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserOrgNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//用户单位名称编码列表应用模型
public class UserOrgNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(UserOrgNameCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"上海市教委", "徐汇区教育局", "长宁区教育局"};
	String[] code_Array = {"0", "1", "2"};
	
	//模拟数据函数
	private UserOrgNameCodesDTO SimuDataFunc() {
		UserOrgNameCodesDTO uoncDto = new UserOrgNameCodesDTO();
		//时戳
		uoncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//用户单位名称编码列表模拟数据
		List<NameCode> dishNameCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode uonc = new NameCode();
			uonc.setName(name_Array[i]);
			uonc.setCode(code_Array[i]);
			dishNameCodes.add(uonc);
		}
		//设置数据
		uoncDto.setUserOrgNameCodes(dishNameCodes);
		//消息ID
		uoncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return uoncDto;
	}
	
	// 用户单位名称编码列表模型函数
	public UserOrgNameCodesDTO appModFunc(String token, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		UserOrgNameCodesDTO uoncDto = null;
		if(isRealData) {       //真实数据
			//按参数形式处理
	  		if(db2Service != null) {
	  			uoncDto = new UserOrgNameCodesDTO();
	  			//时戳
	  			uoncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	  			//用户单位名称数据
	  			List<NameCode> userOrgNameCodes = new ArrayList<>();
	  			//赋值
	  			UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);   //获取用户数据权限信息
	  			if(udpiDto.getUserName() != null && udpiDto.getRoleType() != null) {
	  				if(udpiDto.getRoleType() == 1) {   //监管部门
	  					if(udpiDto.getOrgName().equals("市教委")) {
	  						for(String curKey : AppModConfig.compDepNameToIdMap3.keySet()) {   //区属主管部门
	  							if(!curKey.equals("其他")) {
	  								NameCode uonc = new NameCode();
	  								uonc.setCode(curKey);
	  								uonc.setName(curKey);
	  								userOrgNameCodes.add(uonc);
	  							}
	  						}
	  						for(String curKey : AppModConfig.compDepNameToIdMap2.keySet()) {   //市属主管部门
	  							if(!curKey.equals("其他")) {
	  								NameCode uonc = new NameCode();
	  								uonc.setCode(curKey);
	  								uonc.setName(curKey);
	  								userOrgNameCodes.add(uonc);
	  							}
	  						}
	  						for(String curKey : AppModConfig.compDepNameToIdMap1.keySet()) {   //部署主管部门
	  							if(!curKey.equals("其他")) {
	  								NameCode uonc = new NameCode();
	  								uonc.setCode(curKey);
	  								uonc.setName(curKey);
	  								userOrgNameCodes.add(uonc);
	  							}
	  						}
	  					}
	  					else {
	  						NameCode uonc = new NameCode();
  							uonc.setCode(udpiDto.getOrgName());
  							uonc.setName(udpiDto.getOrgName());
  							userOrgNameCodes.add(uonc);
	  					}
	  				}
	  				else {       //学校
	  					
	  				}
	  			}
	  			//设置数据
	  			uoncDto.setUserOrgNameCodes(userOrgNameCodes);
	  			//消息ID
	  			uoncDto.setMsgId(AppModConfig.msgId);
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
			uoncDto = SimuDataFunc();
		}		

		return uoncDto;
	}
}
