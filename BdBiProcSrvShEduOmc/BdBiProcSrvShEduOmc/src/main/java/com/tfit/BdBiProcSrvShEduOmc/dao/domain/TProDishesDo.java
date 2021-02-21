package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TProDishesDo {
	String id;                         //主键ID
	String name;                       //菜肴名称
	String schoolId;                   //学校Id
	String classTypeId;                //班级类型ID
	String supplierId;                 //团餐公司Id
	String image;                      //菜品图片
	Timestamp supplyDate;              //供应时间
	String caterTypeId;                //
	String caterTypeName;              //餐别名
	String category;                   //菜品类别
	String colour;                     //菜品颜色
	String items;                      //菜品菜系
	String shape;                      //形状
	String taste;                      //口味
	String technology;                 //菜品工艺
	BigDecimal cost;                   //参考成本
	BigDecimal standardWeight;         //标准重量
	String cookingMethod;              //做法
	String creator;                    //创建者
	Timestamp createTime;              //
	String updater;                    //更新者
	Timestamp lastUpdateTime;          //
	Integer stat;                      //
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getClassTypeId() {
		return classTypeId;
	}
	public void setClassTypeId(String classTypeId) {
		this.classTypeId = classTypeId;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Timestamp getSupplyDate() {
		return supplyDate;
	}
	public void setSupplyDate(Timestamp supplyDate) {
		this.supplyDate = supplyDate;
	}
	public String getCaterTypeId() {
		return caterTypeId;
	}
	public void setCaterTypeId(String caterTypeId) {
		this.caterTypeId = caterTypeId;
	}
	public String getCaterTypeName() {
		return caterTypeName;
	}
	public void setCaterTypeName(String caterTypeName) {
		this.caterTypeName = caterTypeName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getTaste() {
		return taste;
	}
	public void setTaste(String taste) {
		this.taste = taste;
	}
	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public BigDecimal getStandardWeight() {
		return standardWeight;
	}
	public void setStandardWeight(BigDecimal standardWeight) {
		this.standardWeight = standardWeight;
	}
	public String getCookingMethod() {
		return cookingMethod;
	}
	public void setCookingMethod(String cookingMethod) {
		this.cookingMethod = cookingMethod;
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
