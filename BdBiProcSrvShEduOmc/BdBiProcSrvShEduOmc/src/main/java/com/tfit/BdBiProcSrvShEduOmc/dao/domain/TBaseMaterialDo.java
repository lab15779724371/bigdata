package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TBaseMaterialDo {
	String id;                         //
	String materialName;               //原料名称
	String parentId;                   //'0', 0 主要名称 其他parent_id对应的别名
	String otherNames;                 //别名
	String typeId;                     //分类ID
	String typeName;                   //分类名称
	BigDecimal calorie;                //热量
	BigDecimal carbohydrate;           //碳水化合物
	BigDecimal fat;                    //脂肪
	BigDecimal protein;                //蛋白质
	BigDecimal dietaryFiber;           //膳食纤维
	BigDecimal fibre;                  //纤维素
	BigDecimal vitamineA;              //维生素A
	BigDecimal vitamineC;              //维生素C
	BigDecimal vitamineE;              //维生素E
	BigDecimal carotene;               //胡萝卜素
	BigDecimal oryzanin;               //硫胺素
	BigDecimal lactochrome;            //核黄素
	BigDecimal niacin;                 //烟酸
	BigDecimal cholesterol;            //胆固醇
	BigDecimal magnesium;              //镁
	BigDecimal calcium;                //钙
	BigDecimal iron;                   //铁
	BigDecimal zinc;                   //锌
	BigDecimal copper;                 //铜
	BigDecimal manganese;              //锰
	BigDecimal potassium;              //钾
	BigDecimal phosphorus;             //磷
	BigDecimal sodium;                 //钠
	BigDecimal selenium;               //硒
	Integer reviewed;                  //是否通过审核(启用禁用) 1:通过审核／启用 2:拒绝／禁用
	String refuse_reason;              //拒绝理由
	String creator;                    //创建者
	Timestamp create_time;             //
	String updater;                    //更新人
	Timestamp last_update_time;        //
	Integer stat;                      //
	Integer source;                    //数据来源 0导入，1手动添加
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getOtherNames() {
		return otherNames;
	}
	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public BigDecimal getCalorie() {
		return calorie;
	}
	public void setCalorie(BigDecimal calorie) {
		this.calorie = calorie;
	}
	public BigDecimal getCarbohydrate() {
		return carbohydrate;
	}
	public void setCarbohydrate(BigDecimal carbohydrate) {
		this.carbohydrate = carbohydrate;
	}
	public BigDecimal getFat() {
		return fat;
	}
	public void setFat(BigDecimal fat) {
		this.fat = fat;
	}
	public BigDecimal getProtein() {
		return protein;
	}
	public void setProtein(BigDecimal protein) {
		this.protein = protein;
	}
	public BigDecimal getDietaryFiber() {
		return dietaryFiber;
	}
	public void setDietaryFiber(BigDecimal dietaryFiber) {
		this.dietaryFiber = dietaryFiber;
	}
	public BigDecimal getFibre() {
		return fibre;
	}
	public void setFibre(BigDecimal fibre) {
		this.fibre = fibre;
	}
	public BigDecimal getVitamineA() {
		return vitamineA;
	}
	public void setVitamineA(BigDecimal vitamineA) {
		this.vitamineA = vitamineA;
	}
	public BigDecimal getVitamineC() {
		return vitamineC;
	}
	public void setVitamineC(BigDecimal vitamineC) {
		this.vitamineC = vitamineC;
	}
	public BigDecimal getVitamineE() {
		return vitamineE;
	}
	public void setVitamineE(BigDecimal vitamineE) {
		this.vitamineE = vitamineE;
	}
	public BigDecimal getCarotene() {
		return carotene;
	}
	public void setCarotene(BigDecimal carotene) {
		this.carotene = carotene;
	}
	public BigDecimal getOryzanin() {
		return oryzanin;
	}
	public void setOryzanin(BigDecimal oryzanin) {
		this.oryzanin = oryzanin;
	}
	public BigDecimal getLactochrome() {
		return lactochrome;
	}
	public void setLactochrome(BigDecimal lactochrome) {
		this.lactochrome = lactochrome;
	}
	public BigDecimal getNiacin() {
		return niacin;
	}
	public void setNiacin(BigDecimal niacin) {
		this.niacin = niacin;
	}
	public BigDecimal getCholesterol() {
		return cholesterol;
	}
	public void setCholesterol(BigDecimal cholesterol) {
		this.cholesterol = cholesterol;
	}
	public BigDecimal getMagnesium() {
		return magnesium;
	}
	public void setMagnesium(BigDecimal magnesium) {
		this.magnesium = magnesium;
	}
	public BigDecimal getCalcium() {
		return calcium;
	}
	public void setCalcium(BigDecimal calcium) {
		this.calcium = calcium;
	}
	public BigDecimal getIron() {
		return iron;
	}
	public void setIron(BigDecimal iron) {
		this.iron = iron;
	}
	public BigDecimal getZinc() {
		return zinc;
	}
	public void setZinc(BigDecimal zinc) {
		this.zinc = zinc;
	}
	public BigDecimal getCopper() {
		return copper;
	}
	public void setCopper(BigDecimal copper) {
		this.copper = copper;
	}
	public BigDecimal getManganese() {
		return manganese;
	}
	public void setManganese(BigDecimal manganese) {
		this.manganese = manganese;
	}
	public BigDecimal getPotassium() {
		return potassium;
	}
	public void setPotassium(BigDecimal potassium) {
		this.potassium = potassium;
	}
	public BigDecimal getPhosphorus() {
		return phosphorus;
	}
	public void setPhosphorus(BigDecimal phosphorus) {
		this.phosphorus = phosphorus;
	}
	public BigDecimal getSodium() {
		return sodium;
	}
	public void setSodium(BigDecimal sodium) {
		this.sodium = sodium;
	}
	public BigDecimal getSelenium() {
		return selenium;
	}
	public void setSelenium(BigDecimal selenium) {
		this.selenium = selenium;
	}
	public Integer getReviewed() {
		return reviewed;
	}
	public void setReviewed(Integer reviewed) {
		this.reviewed = reviewed;
	}
	public String getRefuse_reason() {
		return refuse_reason;
	}
	public void setRefuse_reason(String refuse_reason) {
		this.refuse_reason = refuse_reason;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Timestamp getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public Timestamp getLast_update_time() {
		return last_update_time;
	}
	public void setLast_update_time(Timestamp last_update_time) {
		this.last_update_time = last_update_time;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
}
