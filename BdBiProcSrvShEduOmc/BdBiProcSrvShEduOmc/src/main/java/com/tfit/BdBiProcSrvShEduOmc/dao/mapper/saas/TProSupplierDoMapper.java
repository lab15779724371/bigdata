package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;

@Mapper
public interface TProSupplierDoMapper {
	//从表t_pro_supplier中获取供应商id和供应商名称
	List<TProSupplierDo> getIdSupplierIdName();
	
	//从表t_pro_supplier中获取供应商名称以供应商id
	TProSupplierDo getSupplierNameBySupplierId(@Param(value = "supplierId")String supplierId);
	
	//从表t_pro_supplier中获取团餐公司id和团餐公司名称
	List<TProSupplierDo> getRmcIdName();
	
	//从表t_pro_supplier中获取原料供应商id和原料供应商名称
	List<TProSupplierDo> getAllMatSupplierIdName();
}
