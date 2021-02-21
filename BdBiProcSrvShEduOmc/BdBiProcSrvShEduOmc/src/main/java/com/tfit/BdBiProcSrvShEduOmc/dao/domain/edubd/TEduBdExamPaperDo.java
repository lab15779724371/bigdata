package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdExamPaperDo {
	String id;                     //试卷ID
	String createTime;            //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String name;                   //试卷名称
	String title;                  //试卷标题
	String remark;                 //试卷备注
	Integer category;              //试卷分类，1:系统操作，2:食品安全，3:政策法规
	String lastUpdateTime;         //最后更新时间
	Integer stat;                  //有效标识，0:无效，1:有效
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
