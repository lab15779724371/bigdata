package  com.tfit.BdBiProcSrvShEduOmc.dto.pn;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class RbBulletinsDTO {
	String time;
	PageInfo pageInfo;
	List<RbBulletins> rbBulletins;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<RbBulletins> getRbBulletins() {
		return rbBulletins;
	}
	public void setRbBulletins(List<RbBulletins> rbBulletins) {
		this.rbBulletins = rbBulletins;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
