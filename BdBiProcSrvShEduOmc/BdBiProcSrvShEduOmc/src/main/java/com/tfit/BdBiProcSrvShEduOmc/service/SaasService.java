package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;
import java.util.Map;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.CreatorDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SupplierIdDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TBaseMaterialDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TBaseMaterialSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TBaseMaterialTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduCaterTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduMenuGroupDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduMenuTemplateDishesDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProCategoryDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProDishesDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProLicenseDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProRecyclerSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProReserveSampleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProWarningDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TSaasPackageDo;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.EduSupplierDetail;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdTemplate;

public interface SaasService {
	//获取所有供应商
	List<SupplierIdDo> getAllSupplierId();
	
	//以供应商获取创建者
	List<CreatorDo> getCreatorBySupplierId(String strSupplierId);
	
	//从表t_pro_supplier中获取供应商id和供应商名称
	List<TProSupplierDo> getIdSupplierIdName();
	
	//从表t_pro_supplier中获取团餐公司id和团餐公司名称
	List<TProSupplierDo> getRmcIdName();
	
	//从表t_pro_supplier中获取原料供应商id和原料供应商名称
	List<TProSupplierDo> getAllMatSupplierIdName();
	
	//从表t_edu_school_supplier中获取供应商id和学校名称
    List<TEduSchoolSupplierDo> getSupplierIdSchoolName();
    
    //学校名称映射团餐公司名称（供应商名称）
    Map<String, String> getSchoolNameToRmcNameMap();
    
    //获取证照预警类型（表名：t_pro_warning）
  	List<TProWarningDo> getLicWarnType();
  	
  	//从表t_edu_school_supplier中获取供应商id以学校id
    TEduSchoolSupplierDo getSupplierIdSchoolId(String schoolId);
    
    //从表t_pro_supplier中获取供应商名称以供应商id
  	TProSupplierDo getSupplierNameBySupplierId(String supplierId);
  	
  	//从表t_edu_school_supplier中获取学校id和供应商id
    List<TEduSchoolSupplierDo> getAllSupplierIdSchoolId();
    
    //从表t_edu_school_supplier中获取主键id、学校id和供应商id
    List<TEduSchoolSupplierDo> getAllIdSupplierIdSchoolId(Integer stat);
    
    //获取回收单位ID和名称
  	List<TProRecyclerSupplierDo> getAllRecyclerIdName();
  	
  	//获取回收人名称
    List<String> getAllRecPersonName();
  	
  	//获取所有菜单组ID和名称
    List<TEduMenuGroupDo> getAllMenuGroupIdName();
    
    //获取留样单位
    List<TProReserveSampleDo> getAllRsUnits();
    
    //获取所有菜单组名称
    List<TSaasPackageDo> getAllMenuGroupName();
    
    //获取所有物料分类
   	List<TBaseMaterialTypeDo> getAllMatClassifyIdName();
   	
   	//获取所有物料名称
   	List<TBaseMaterialDo> getAllMatNames();
   	
   	//获取所有物料名称2
   	List<TBaseMaterialSupplierDo> getAllMatNames2();
   	
   	//获取所有菜品类别
   	List<TEduMenuTemplateDishesDo> getAllDishTypes();
   	
   	//获取所有菜品名称
   	List<TProDishesDo> getAllDishNames();
   	
   	//获取所有菜品类别2
   	List<TProCategoryDo> getAllDishTypes2();
   	
   	//获取所有餐别类型名称
  	List<TEduCaterTypeDo> getAllCaterTypeNames();
  	
  	//获取证照信息以供应商id和证照类型（0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 6：税务登记证 7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 14:运输许可证 15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证）
  	TProLicenseDo getLicenseInfoBySupplierIdLicType(String supplierId, int licType);
  	
  	/**
  	 * 预警模板相关
  	 * 
  	 */
    int deleteByPrimaryKey(String id);
    int insert(TEduBdTemplate record);
    TEduBdTemplate selectByPrimaryKey(String id);
    int updateByPrimaryKey(TEduBdTemplate record);
    List<TEduBdTemplate> selectAllList();
    //获取最大的Id编号
    Integer selectMaxIdList();
    
    //根据模板数据查询模板
    TEduBdTemplate selectByTemplate(TEduBdTemplate key);
    
    /**
     * 团餐公司、供应商相关
     */
	//从表t_pro_supplier中获取原料供应商id和原料供应商名称
    List<EduSupplierDetail> getSupplierList(EduSupplierDetail obj);
}
