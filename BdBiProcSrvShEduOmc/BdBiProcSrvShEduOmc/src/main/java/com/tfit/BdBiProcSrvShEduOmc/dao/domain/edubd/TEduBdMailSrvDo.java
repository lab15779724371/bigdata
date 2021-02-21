package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdMailSrvDo {
	String id;                          //主键id
	String userName;                    //用户名
	String email;                       //邮件用户名（包含@后面内容）
	String password;                    //邮件用户密码
	String rcvServer;                   //接收服务器
	Integer rcvSrvPort;                 //接收端口
	Integer rcvSrvPortNo;               //接收端口号
	String sendServer;                  //发送服务器
	Integer sendSrvPort;                //发送服务端口
	Integer sendSrvPortNo;              //发送服务端口号
	Integer stat;                       //0:无效，1:有效
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRcvServer() {
		return rcvServer;
	}
	public void setRcvServer(String rcvServer) {
		this.rcvServer = rcvServer;
	}
	public Integer getRcvSrvPort() {
		return rcvSrvPort;
	}
	public void setRcvSrvPort(Integer rcvSrvPort) {
		this.rcvSrvPort = rcvSrvPort;
	}
	public Integer getRcvSrvPortNo() {
		return rcvSrvPortNo;
	}
	public void setRcvSrvPortNo(Integer rcvSrvPortNo) {
		this.rcvSrvPortNo = rcvSrvPortNo;
	}
	public String getSendServer() {
		return sendServer;
	}
	public void setSendServer(String sendServer) {
		this.sendServer = sendServer;
	}
	public Integer getSendSrvPort() {
		return sendSrvPort;
	}
	public void setSendSrvPort(Integer sendSrvPort) {
		this.sendSrvPort = sendSrvPort;
	}
	public Integer getSendSrvPortNo() {
		return sendSrvPortNo;
	}
	public void setSendSrvPortNo(Integer sendSrvPortNo) {
		this.sendSrvPortNo = sendSrvPortNo;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
