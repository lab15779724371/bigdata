package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.util.Date;

public class CreatorDo {
	private String creator;
	private String use_date;
	private Date create_time;
	private Date last_update_time;
	private int is_history;
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}	
	public String getUse_date() {
		return use_date;
	}
	public void setUse_date(String use_date) {
		this.use_date = use_date;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getLast_update_time() {
		return last_update_time;
	}
	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}
	public long getIs_history() {
		return is_history;
	}
	public void setIs_history(int is_history) {
		this.is_history = is_history;
	}
}
