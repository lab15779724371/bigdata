package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProLicenseDo;

@Mapper
public interface TProLicenseDoMapper {
	//获取证照信息以供应商id和证照类型（0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 6：税务登记证 7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 14:运输许可证 15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证）
	TProLicenseDo getLicenseInfoBySupplierIdLicType(@Param(value = "supplierId")String supplierId, @Param(value = "licType")int licType);
}