package com.tfit.BdBiProcSrvShEduOmc.dao;

import lombok.Data;

import java.util.List;

@Data
public class WarnLevelBody {
	private String id;
	private String userAccount;
	private Integer warnType;
	private Integer warnAlertType;
	private Integer scheduledStatus;
	private String warnPushContent;
	private String warnDataTime;
	private String warnPushTime;
	private List<PushReceiverInfo> pushReceiverMsg;
	private String pushReceiverMsgStr;
	private Integer emailStatus;
	private String createTime;

}
