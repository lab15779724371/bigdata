package com.tfit.BdBiProcSrvShEduOmc.dto.ms;

public class SaveMailSrvInfo {
	String userName;
	String mailUserName;
	String password;
	String rcvMailServer;
	Integer rcvMailPort;
	Integer rcvMailPortNo;
	String sendMailServer;
	Integer sendMailPort;
	Integer sendMailPortNo;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMailUserName() {
		return mailUserName;
	}
	public void setMailUserName(String mailUserName) {
		this.mailUserName = mailUserName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRcvMailServer() {
		return rcvMailServer;
	}
	public void setRcvMailServer(String rcvMailServer) {
		this.rcvMailServer = rcvMailServer;
	}
	public Integer getRcvMailPort() {
		return rcvMailPort;
	}
	public void setRcvMailPort(Integer rcvMailPort) {
		this.rcvMailPort = rcvMailPort;
	}
	public Integer getRcvMailPortNo() {
		return rcvMailPortNo;
	}
	public void setRcvMailPortNo(Integer rcvMailPortNo) {
		this.rcvMailPortNo = rcvMailPortNo;
	}
	public String getSendMailServer() {
		return sendMailServer;
	}
	public void setSendMailServer(String sendMailServer) {
		this.sendMailServer = sendMailServer;
	}
	public Integer getSendMailPort() {
		return sendMailPort;
	}
	public void setSendMailPort(Integer sendMailPort) {
		this.sendMailPort = sendMailPort;
	}
	public Integer getSendMailPortNo() {
		return sendMailPortNo;
	}
	public void setSendMailPortNo(Integer sendMailPortNo) {
		this.sendMailPortNo = sendMailPortNo;
	}
}
