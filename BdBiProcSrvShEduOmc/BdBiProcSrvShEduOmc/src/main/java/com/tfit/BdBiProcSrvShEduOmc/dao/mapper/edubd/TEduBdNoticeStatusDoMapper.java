package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;

public interface TEduBdNoticeStatusDoMapper {
	//插入消息通知状态记录
	int insertMsgNoticeStatus(TEduBdNoticeStatusDo tebnsDo);
	
	//查询消息通知状态记录列表以接收用户名
	List<TEduBdNoticeStatusDo> getMsgNoticeStatusByRcvUserName(@Param(value = "rcvUserName")String rcvUserName);
	
	//查询消息通知状态记录列表以接收用户名
	List<TEduBdNoticeStatusDo> getMsgNoticeStatusBySendUserName(@Param(value = "sendUserName")String sendUserName);
	
	//查询消息通知状态记录列表以通知ID和发布用户名
	List<TEduBdNoticeStatusDo> getMsgNoticeStatusBybIdSendUser(@Param(value = "bulletinId")String bulletinId, @Param(value = "sendUserName")String sendUserName);
	
	//查询消息通知状态记录列表以通知id和接收用户名
	TEduBdNoticeStatusDo getMsgNoticeStatusBybIdRcvUserName(@Param(value = "bulletinId")String bulletinId, @Param(value = "rcvUserName")String rcvUserName);
	
	//更新阅读次数
	int updateReadCountInMsgNotice(@Param(value = "bulletinId")String bulletinId, @Param(value = "rcvUserName")String rcvUserName, @Param(value = "readCount")int readCount);
	
	//更新签到标识
    int updateSignFlagByTEduBdNoticeStatusDo(TEduBdNoticeStatusDo tebnsDo);
}