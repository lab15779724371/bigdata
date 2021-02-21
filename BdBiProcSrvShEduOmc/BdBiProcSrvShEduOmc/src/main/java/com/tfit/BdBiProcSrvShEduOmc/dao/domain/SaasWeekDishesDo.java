package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import org.joda.time.DateTime;

public class SaasWeekDishesDo {
	private long id;
	private String supplier_id;
	private String proj_name;
	private String use_date;
	private String menu_group_name;
	private String cater_type_name;
	private String dishes_name;
	private int dishes_number;
	private int stat;
	private int is_history;
	private String creator;
    private DateTime createTime;
    private String updater;
    private DateTime last_update_time;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSupplier_id() {
		return supplier_id;
	}
	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}
	public String getProj_name() {
		return proj_name;
	}
	public void setProj_name(String proj_name) {
		this.proj_name = proj_name;
	}
	public String getUse_date() {
		return use_date;
	}
	public void setUse_date(String use_date) {
		this.use_date = use_date;
	}
	public String getMenu_group_name() {
		return menu_group_name;
	}
	public void setMenu_group_name(String menu_group_name) {
		this.menu_group_name = menu_group_name;
	}
	public String getCater_type_name() {
		return cater_type_name;
	}
	public void setCater_type_name(String cater_type_name) {
		this.cater_type_name = cater_type_name;
	}
	public String getDishes_name() {
		return dishes_name;
	}
	public void setDishes_name(String dishes_name) {
		this.dishes_name = dishes_name;
	}
	public int getDishes_number() {
		return dishes_number;
	}
	public void setDishes_number(int dishes_number) {
		this.dishes_number = dishes_number;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	public int getIs_history() {
		return is_history;
	}
	public void setIs_history(int is_history) {
		this.is_history = is_history;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public DateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public DateTime getLast_update_time() {
		return last_update_time;
	}
	public void setLast_update_time(DateTime last_update_time) {
		this.last_update_time = last_update_time;
	}
}
