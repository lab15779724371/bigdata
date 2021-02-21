package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import lombok.Data;

@Data
public class CaMatSupDets {
	//用料日期，格式：xxxx/xx/xx
	String matUseDate;
	//配货批次号
	String distrBatNumber;
	//学校名称
	String schName;
	//区域名称
	String distName;
	//详细地址
	String detailAddr;
	//学校类型（学制）
	String schType;
	//学校性质
	String schProp;
	//经营模式（供餐模式
	String optMode;
	//配送类型，原料或成品菜
	String dispType;
	//团餐公司名称
	String rmcName;
	//物料名称
	String matName;
	//标准名称
	String standardName;
	//分类（物料）
	String matClassify;
	//数量，单位：公斤
	String quantity;
	//换算关系
	String cvtRel;
	//换算数量
	String cvtQuantity;
	//批号
	String batNumber;
	//生产日期，格式：xxxxxxxx
	String prodDate;
	//保质期
	String qaGuaPeriod;
	//供应商名称
	String supplierName;
	//验收状态，0:待验收，1:已验收
	int acceptStatus;
	//验收数量
	String acceptNum;
	//验收比例
	float acceptRate;
	//配送单图片
	String gsBillPicUrl;
	//检疫图片
	String qaCertPicUrl;
	//配送单图片集合
	List<String> gsBillPicUrls;
	//验收图片集合
	List<String> qaCertPicUrls;
	//验收日期
	String acceptDate;
	//验收人
	String acceptPerson;
	
	//供应商编号
	String supplyId;
	//学校编号
	String schoolId;
	//表示是否为同一份配送单
	String ledgerMasterId;
}
