package  com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class RbBulletinDetDTO {
	String time;
	RbBulletinDet rbBulletinDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public RbBulletinDet getRbBulletinDet() {
		return rbBulletinDet;
	}
	public void setRbBulletinDet(RbBulletinDet rbBulletinDet) {
		this.rbBulletinDet = rbBulletinDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
