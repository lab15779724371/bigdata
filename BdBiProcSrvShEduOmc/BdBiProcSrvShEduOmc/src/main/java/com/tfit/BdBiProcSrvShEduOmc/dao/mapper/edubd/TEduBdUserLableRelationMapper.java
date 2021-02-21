package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdAddressLableObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdUserLableRelationObj;

public interface TEduBdUserLableRelationMapper {
    int deleteByPrimaryKey(@Param("userId")String userId,@Param("lableId")Integer lableId);

    int insert(TEduBdUserLableRelationObj record);

    int insertSelective(TEduBdUserLableRelationObj record);

    //TEduBdUserLableRelationObj selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TEduBdUserLableRelationObj record);

    int updateByPrimaryKey(TEduBdUserLableRelationObj record);
    
    int deleteByLableId(Integer lableId);
    /**
     * 获取最大的Id编号
     * @return
     */
    List<TEduBdUserLableRelationObj> selectList(@Param("lable")TEduBdUserLableRelationObj tEduBdUserLableRelationObj,@Param("startNum") Integer startNum,@Param("pageSize") Integer pageSize);
}