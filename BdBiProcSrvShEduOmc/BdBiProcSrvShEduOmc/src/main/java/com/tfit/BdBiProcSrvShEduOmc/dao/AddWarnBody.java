package com.tfit.BdBiProcSrvShEduOmc.dao;

import java.util.List;

public class AddWarnBody {
	String id;
	String userAccount;
	String category;
	String unscheduled;
	String week;
	String time;
	String frequency;
	String interval;
	List<PushRecipientInfo> pushRecipient;
	List emailRecipient;
	String dishesWarn;
	String deliveryWarn;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getUnscheduled() {
		return unscheduled;
	}
	public void setUnscheduled(String unscheduled) {
		this.unscheduled = unscheduled;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public List<PushRecipientInfo> getPushRecipient() {
		return pushRecipient;
	}
	public void setPushRecipient(List<PushRecipientInfo> pushRecipient) {
		this.pushRecipient = pushRecipient;
	}
	public List getEmailRecipient() {
		return emailRecipient;
	}
	public void setEmailRecipient(List emailRecipient) {
		this.emailRecipient = emailRecipient;
	}
	public String getDishesWarn() {
		return dishesWarn;
	}
	public void setDishesWarn(String dishesWarn) {
		this.dishesWarn = dishesWarn;
	}
	public String getDeliveryWarn() {
		return deliveryWarn;
	}
	public void setDeliveryWarn(String deliveryWarn) {
		this.deliveryWarn = deliveryWarn;
	}
	
}
