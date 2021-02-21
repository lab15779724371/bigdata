package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TEduMenuTemplateDishesDo {
	String id;                            //
	String menuTemplatePackageId;         //排菜规则模板套餐信息id	
	String category;                      //菜品类别
	Integer number;                       //菜品数
	Integer dishesNum;                    //大荤
	String fixedDish1;                    //固定菜ID1
	String fixedDish2;                    //固定菜ID2
	String fixedDish3;                    //固定菜ID3
	String fixedDish4;                    //固定菜ID4
	String fixedDish5;                    //固定菜ID5
	String supplierId;                    //供应商id
	String creator;                       //创建者
	Timestamp createTime;                 //
	String updater;                       //更新人
	Timestamp lastUpdateTime;             //
	Integer stat;                         //
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMenuTemplatePackageId() {
		return menuTemplatePackageId;
	}
	public void setMenuTemplatePackageId(String menuTemplatePackageId) {
		this.menuTemplatePackageId = menuTemplatePackageId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getDishesNum() {
		return dishesNum;
	}
	public void setDishesNum(Integer dishesNum) {
		this.dishesNum = dishesNum;
	}
	public String getFixedDish1() {
		return fixedDish1;
	}
	public void setFixedDish1(String fixedDish1) {
		this.fixedDish1 = fixedDish1;
	}
	public String getFixedDish2() {
		return fixedDish2;
	}
	public void setFixedDish2(String fixedDish2) {
		this.fixedDish2 = fixedDish2;
	}
	public String getFixedDish3() {
		return fixedDish3;
	}
	public void setFixedDish3(String fixedDish3) {
		this.fixedDish3 = fixedDish3;
	}
	public String getFixedDish4() {
		return fixedDish4;
	}
	public void setFixedDish4(String fixedDish4) {
		this.fixedDish4 = fixedDish4;
	}
	public String getFixedDish5() {
		return fixedDish5;
	}
	public void setFixedDish5(String fixedDish5) {
		this.fixedDish5 = fixedDish5;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
