package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountSec;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountSecDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//账号安全应用模型
public class AmAccountSecAppMod {
	private static final Logger logger = LogManager.getLogger(AmAccountSecAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	int safeGrade = 1;
	int isSetLoginPw = 1;
	
	//模拟数据函数
	private AmAccountSecDTO SimuDataFunc() {
		AmAccountSecDTO stsDto = new AmAccountSecDTO();
		//时戳
		stsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//账号安全模拟数据
		AmAccountSec amAccountSec = new AmAccountSec();
		//赋值
		amAccountSec.setSafeGrade(safeGrade);
		amAccountSec.setIsSetLoginPw(isSetLoginPw);
		//设置数据
		stsDto.setAmAccountSec(amAccountSec);
		//消息ID
		stsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return stsDto;
	}
	
	// 账号安全模型函数
	public AmAccountSecDTO appModFunc(String token, Db2Service db2Service) {
		AmAccountSecDTO stsDto = null;
		if(isRealData) {       //真实数据
			//按参数形式处理
	  		if(token != null && db2Service != null) {
	  			//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
	  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
	  			if(tebuDo != null) {
	  				stsDto = new AmAccountSecDTO();
	  				//时戳
	  				stsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	  				//账号安全模拟数据
	  				AmAccountSec amAccountSec = new AmAccountSec();
	  				//赋值
	  				amAccountSec.setSafeGrade(0);
	  				if(tebuDo.getSafeGrade() != null)
	  					amAccountSec.setSafeGrade(tebuDo.getSafeGrade());
	  				amAccountSec.setIsSetLoginPw(0);
	  				if(tebuDo.getPassword() != null)
	  					amAccountSec.setIsSetLoginPw(1);
	  				//设置数据
	  				stsDto.setAmAccountSec(amAccountSec);
	  				//消息ID
	  				stsDto.setMsgId(AppModConfig.msgId);
	  				AppModConfig.msgId++;
	  				// 消息id小于0判断
	  				AppModConfig.msgIdLessThan0Judge();
	  				
	  			}
	  		}
	  		else {
	  			logger.info("访问接口参数非法！");
	  		}
		}
		else {    //模拟数据
			//模拟数据函数
			stsDto = SimuDataFunc();
		}		

		return stsDto;
	}
}