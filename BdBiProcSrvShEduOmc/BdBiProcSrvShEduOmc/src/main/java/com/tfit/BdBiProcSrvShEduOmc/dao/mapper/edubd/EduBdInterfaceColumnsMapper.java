package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdInterfaceColumnsDo;

public interface EduBdInterfaceColumnsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EduBdInterfaceColumnsDo record);

    int insertSelective(EduBdInterfaceColumnsDo record);

    EduBdInterfaceColumnsDo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EduBdInterfaceColumnsDo record);

    int updateByPrimaryKey(EduBdInterfaceColumnsDo record);
    
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    EduBdInterfaceColumnsDo selectByInterfaceName(@Param(value = "userId")String userId,@Param(value = "interfaceName")String interfaceName);
    
    /**
     * 获取最大的Id编号
     * @return
     */
    Integer selectMaxId();
}