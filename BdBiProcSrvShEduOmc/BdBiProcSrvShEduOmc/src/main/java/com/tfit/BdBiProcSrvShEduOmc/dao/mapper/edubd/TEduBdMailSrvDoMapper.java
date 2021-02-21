package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;

public interface TEduBdMailSrvDoMapper {
	//查询邮件服务记录以用户名
	TEduBdMailSrvDo getMailSrvInfoByUserName(@Param(value = "userName")String userName);
	
	//插入邮件服务记录
	int insertMailSrv(TEduBdMailSrvDo tebmsDo);
	
	//更新邮件名称以用户名
	int updateEmail(@Param(value = "userName")String userName, @Param(value = "email")String email);
	
	//更新密码以用户名
	int updatePassword(@Param(value = "userName")String userName, @Param(value = "password")String password);
	
	//更新接收服务器以用户名
	int updateRcvServer(@Param(value = "userName")String userName, @Param(value = "rcvServer")String rcvServer);
	
	//更新接收服务端口以用户名
	int updateRcvSrvPort(@Param(value = "userName")String userName, @Param(value = "rcvSrvPort")Integer rcvSrvPort);
	
	//更新接收服务端口号以用户名
	int updateRcvSrvPortNo(@Param(value = "userName")String userName, @Param(value = "rcvSrvPortNo")Integer rcvSrvPortNo);
	
	//更新发送服务器以用户名
	int updateSendServer(@Param(value = "userName")String userName, @Param(value = "sendServer")String sendServer);
	
	//更新发送服务端口以用户名
	int updateSendSrvPort(@Param(value = "userName")String userName, @Param(value = "sendSrvPort")Integer sendSrvPort);
	
	//更新发送服务端口号以用户名
	int updateSendSrvPortNo(@Param(value = "userName")String userName, @Param(value = "sendSrvPortNo")Integer sendSrvPortNo);
	
	//更新有效标识以用户名
	int updateStat(@Param(value = "userName")String userName, @Param(value = "stat")Integer stat);
}
