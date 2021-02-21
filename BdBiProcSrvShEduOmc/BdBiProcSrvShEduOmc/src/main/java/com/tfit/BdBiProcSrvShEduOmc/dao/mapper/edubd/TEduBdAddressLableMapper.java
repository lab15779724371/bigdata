package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdAddressLableObj;

public interface TEduBdAddressLableMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TEduBdAddressLableObj record);

    TEduBdAddressLableObj selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TEduBdAddressLableObj record);

    int updateByPrimaryKey(TEduBdAddressLableObj record);
    
    
    List<TEduBdAddressLableObj> selectList(@Param("lable")TEduBdAddressLableObj tEduBdAddressLableObj,@Param("startNum") Integer startNum,@Param("pageSize") Integer pageSize);
    int selectListCount(@Param("lable")TEduBdAddressLableObj tEduBdAddressLableObj);
    List<TEduBdAddressLableObj> selectListAndUserCount(@Param("lable")TEduBdAddressLableObj tEduBdAddressLableObj,@Param("startNum") Integer startNum,@Param("pageSize") Integer pageSize);
    
    /**
     * 获取最大的Id编号
     * @return
     */
    Integer selectMaxId();
}