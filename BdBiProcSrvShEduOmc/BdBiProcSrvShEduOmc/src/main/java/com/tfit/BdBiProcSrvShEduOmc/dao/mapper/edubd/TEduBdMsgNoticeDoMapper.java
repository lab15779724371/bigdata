package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMsgNoticeDo;

public interface TEduBdMsgNoticeDoMapper {
	//插入消息通知记录
	int insertMsgNotice(TEduBdMsgNoticeDo tebmnDo);
	
	//查询消息通知记录以通知id
	TEduBdMsgNoticeDo getMsgNoticeById(@Param(value = "id")String id);
	
	//查询消息通知当前上一条记录以当前通知id
	TEduBdMsgNoticeDo getPreMsgNoticeById(@Param(value = "id")String id);
	
	//查询消息通知当前下一条记录以当前通知id
	TEduBdMsgNoticeDo getNextMsgNoticeById(@Param(value = "id")String id);
	
	//查询消息通知当前上一条记录以当前通知id和接收用户名（接收用户名字串前后添加%）
	TEduBdMsgNoticeDo getPreMsgNoticeByIdRcvUserName(@Param(value = "id")String id, @Param(value = "rcvUserName")String rcvUserName);
		
	//查询消息通知当前下一条记录以当前通知id和接收用户名（接收用户名字串前后添加%）
	TEduBdMsgNoticeDo getNextMsgNoticeByIdRcvUserName(@Param(value = "id")String id, @Param(value = "rcvUserName")String rcvUserName);
		
	//查询消息通知当前上一条记录以当前通知id和接收用户名（发送用户名）
	TEduBdMsgNoticeDo getPreMsgNoticeByIdSendUserName(@Param(value = "id")String id, @Param(value = "sendUserName")String sendUserName);
			
	//查询消息通知当前下一条记录以当前通知id和接收用户名（发送用户名）
	TEduBdMsgNoticeDo getNextMsgNoticeByIdSendUserName(@Param(value = "id")String id, @Param(value = "sendUserName")String sendUserName);
}