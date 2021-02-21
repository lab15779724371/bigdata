package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.Set;

import lombok.Data;

@Data
public class MaterialList {
	//
    private String id;
    //供应时间
    private String supplyDate;
    //学校id
    private String schoolId;
    //学校名字
    private String schoolName;
    //区
    private String area;
    //地址
    private String address;
    //法人代表
    private String corporation;
    //联系人
    private String foodSafetyPersion;
    //联系电话
    private String foodSafetyMobilephone;
    //原料id
    private String materialId;
    //原料名字
    private String materialName;
    //供应商id
    private String supplyId;
    //供应商名字
    private String supplyName;
    //菜品
    private Set<Object> dishSet;
    //学校和团餐公司的关联id
    private String projId;
    //团餐公司id
    private String supplierId;
    //团餐公司名字
    private String supplierName;
    //学制编号
    private String levelName;
    //发货批次
    private String wareBatchNo; 
}
