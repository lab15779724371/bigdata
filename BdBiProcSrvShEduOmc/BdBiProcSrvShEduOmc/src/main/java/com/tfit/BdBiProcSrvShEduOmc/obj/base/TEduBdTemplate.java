package com.tfit.BdBiProcSrvShEduOmc.obj.base;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;

import lombok.Data;

@Data
public class TEduBdTemplate {
	
    private String id;

    //模板类型:(1：市教委 , 2：教育局 3学校）
    private Integer templateType; 

    //模板对象对象 -1:所有用户 -2 所有学校 （template_type 为2时，各个教育局编号）
    private String templateObj;

    //模板内容
    private String templateContent;

    //创建者
    private String creator;

    //创建时间
    private String createTime;

    //更新人
    private String updater;

    //最后更新时间
    private String lastUpdateTime;
    private byte[] annCont;              //通知内容
    private Integer amFlag;              //是否有附件标识，0:无，1:有

	private String amInfo;               //附件信息
	
    //附件列表
	private List<RbUlAttachment> amInfos;
	

}