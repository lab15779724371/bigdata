package com.tfit.BdBiProcSrvShEduOmc.obj.base;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class TBaseMaterial {
    private String id;

    private String materialName;

    private String parentId;

    private String otherNames;

    private String typeId;

    private String typeName;

    private BigDecimal calorie;

    private BigDecimal carbohydrate;

    private BigDecimal fat;

    private BigDecimal protein;

    private BigDecimal dietaryFiber;

    private BigDecimal fibre;

    private BigDecimal vitamineA;

    private BigDecimal vitamineC;

    private BigDecimal vitamineE;

    private BigDecimal carotene;

    private BigDecimal oryzanin;

    private BigDecimal lactochrome;

    private BigDecimal niacin;

    private BigDecimal cholesterol;

    private BigDecimal magnesium;

    private BigDecimal calcium;

    private BigDecimal iron;

    private BigDecimal zinc;

    private BigDecimal copper;

    private BigDecimal manganese;

    private BigDecimal potassium;

    private BigDecimal phosphorus;

    private BigDecimal sodium;

    private BigDecimal selenium;

    private Integer reviewed;

    private String refuseReason;

    private String creator;

    private String createTime;

    private String updater;

    private String lastUpdateTime;

    private Integer stat;

    private Integer source;

}