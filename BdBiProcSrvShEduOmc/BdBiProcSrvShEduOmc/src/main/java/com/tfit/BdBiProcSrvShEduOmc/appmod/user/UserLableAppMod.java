package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.common.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSimpleObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdAddressLableObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdUserLableRelationObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//用户管理应用模型
public class UserLableAppMod {
	private static final Logger logger = LogManager.getLogger(UserLableAppMod.class.getName());
	
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	/**
	 * 标签列表
	 * @param token
	 * @param inputMap
	 * @param db2Service
	 * @return
	 */
	public ApiResponse<PageInfo<List<TEduBdAddressLableObj>>> getAddressLableList(String token,Map<String,String> inputMap,Db2Service db2Service) {
		if(CommonUtil.isNotEmpty(inputMap.get("page")==null?null:inputMap.get("page").toString())) {
			this.curPageNum = Integer.parseInt(inputMap.get("page").toString());
		}
		if(CommonUtil.isNotEmpty(inputMap.get("pageSize")==null?null:inputMap.get("pageSize").toString())) {
			this.pageSize = Integer.parseInt(inputMap.get("pageSize").toString());
		}
		
		Integer startNum = (this.curPageNum-1)*this.pageSize;
		if(curPageNum == -1) {
			startNum = -1;
		}
		
		TEduBdAddressLableObj tEduBdAddressLableObj = new TEduBdAddressLableObj();
		tEduBdAddressLableObj.setLableName(inputMap.get("lableName"));
		tEduBdAddressLableObj.setStat(CommonUtil.isNotEmpty(inputMap.get("stat"))?Integer.valueOf(inputMap.get("stat")):null);
		List<TEduBdAddressLableObj>  resultList = db2Service.selectListAndUserCount(tEduBdAddressLableObj,startNum,this.pageSize);
		int count = db2Service.selectListAddressLableCount(tEduBdAddressLableObj);
		PageInfo<List<TEduBdAddressLableObj>> pageObj = new PageInfo<>(resultList,curPageNum,count);
		return new ApiResponse<>(pageObj);
	}
    
	/**
	 * 获取标签详情
	 * @param token
	 * @param id
	 * @param db2Service
	 * @return
	 */
	public ApiResponse<TEduBdAddressLableObj> getAddressLableInfo(String token, Integer id,Db2Service db2Service) {
		TEduBdAddressLableObj result = db2Service.selectByPrimaryKeyAddressLable(id);
		List<UserSimpleObj> userList = new ArrayList<>();
		if(result != null && result.getId() != null ) {
			List<TEduBdUserDo> tebuDoList = db2Service.getBdUserInfoByUserLableRelation(result.getId());
			if(tebuDoList !=null && tebuDoList.size() > 0) {
				for(int k = 0; k < tebuDoList.size(); k++) {
					UserSimpleObj aum = new UserSimpleObj();
					aum.setUserId(tebuDoList.get(k).getId());
					aum.setUserName(tebuDoList.get(k).getUserAccount());                                              //用户名
					aum.setFullName(tebuDoList.get(k).getName());                                                     //姓名
					aum.setAccountType(AppModConfig.accountTypeIdToNameMap.get(tebuDoList.get(k).getIsAdmin()));      //账号类型
					aum.setUserOrg(tebuDoList.get(k).getOrgName());                                                   //单位
					aum.setEmail(tebuDoList.get(k).getEmail());
					userList.add(aum);	
				}
				
				result.setUserList(userList);
			}
			
		}
		return new ApiResponse<>(result);
	}
	
	/**
	 * 删除标签
	 * @param token
	 * @param id
	 * @param db2Service
	 * @return
	 */
	public ApiResponse<T> deleteAddressLableInfo(String token,Integer id,Db2Service db2Service) {
		
		if(id ==null) {
			return new ApiResponse<T>(IOTRspType.Fail,"id不能为空！");
		}
		
		TEduBdAddressLableObj template = db2Service.selectByPrimaryKeyAddressLable(id);
		if(template == null || template.getId() == null) {
			return new ApiResponse<T>(IOTRspType.Fail,"模板不存在！");
		}
		
		int iRet = db2Service.deleteByPrimaryKeyAddressLable(id);
		if(iRet >= 0) {
			/**
			 * 人员
			 */
			//删除
			iRet = db2Service.deleteByLableId(id);
			return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
		}else {
			return new ApiResponse<T>(IOTRspType.Fail,"删除失败！");
		}
		
	}
	
	/**
	 * 添加标签
	 * @param token
	 * @param record
	 * @param db2Service
	 * @return
	 */
	public ApiResponse<T> addAddressLableInfo(String token,TEduBdAddressLableObj record ,Db2Service db2Service) {
		if(CommonUtil.isEmpty(record.getLableName())) {
			return new ApiResponse<T>(IOTRspType.Fail,"标签名称不能为空！");
		}
		
		TEduBdAddressLableObj tEduBdAddressLableObj = new TEduBdAddressLableObj();
		tEduBdAddressLableObj.setLableName(record.getLableName());
		List<TEduBdAddressLableObj>  resultList = db2Service.selectListAddressLable(tEduBdAddressLableObj,null,null);
		if((!(resultList == null || resultList.size()==0 || resultList.get(0) == null || resultList.get(0).getId() == null)) &&
				resultList.get(0).getId() != record.getId()) {
			return new ApiResponse<T>(IOTRspType.Fail,"该标签已存在，请返回修改！");
		}
		
		TEduBdUserDo parTebuDo = db2Service.getBdUserInfoByToken(token);
		//修改
		if(parTebuDo != null && parTebuDo.getUserAccount() !=null && !"".equals(parTebuDo.getUserAccount())) {
	    	//更新人
			 record.setUpdater(parTebuDo.getUserAccount());
		 }
		 //更新时间
		 record.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
		 
		Integer iResult = -1;
		if(record.getId() == null || record.getId() == -1) {
			//取号
			Integer maxId = db2Service.getAddressLableMaxId();
			if(maxId==null) {
				maxId = 0;
			}
			record.setId(maxId+1);
			
			if(parTebuDo != null && parTebuDo.getUserAccount() !=null && !"".equals(parTebuDo.getUserAccount())) {
				//创建者
				 record.setCreator(parTebuDo.getUserAccount());
    	    	//创建时间
				 record.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
    	    	//更新人
				 record.setUpdater(parTebuDo.getUserAccount());
    	    	//更新时间
				 record.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
			 }
			
			iResult = db2Service.insertAddressLable(record);
		}else {
			iResult = db2Service.updateByPrimaryKeySelectiveAddressLable(record);
		}
		
		if(iResult >= 0) {
			/**
			 * 人员
			 */
			//删除
			iResult = db2Service.deleteByLableId(record.getId());
			//新增
			if(record.getUserIdList() !=null && record.getUserIdList().size()>0) {
				for(String userId : record.getUserIdList()) {
					TEduBdUserLableRelationObj relation = new TEduBdUserLableRelationObj();
					relation.setUserId(userId);
					relation.setLableId(record.getId());
					if(parTebuDo != null && parTebuDo.getUserAccount() !=null && !"".equals(parTebuDo.getUserAccount())) {
						//创建者
						relation.setCreator(parTebuDo.getUserAccount());
		    	    	//创建时间
						relation.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
		    	    	//更新人
						relation.setUpdater(parTebuDo.getUserAccount());
		    	    	//更新时间
						relation.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
					 }
					iResult = db2Service.insertUserLableRelation(relation);
				}
			} 
			
			return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
		}else {
			return new ApiResponse<T>(IOTRspType.Fail,"保存失败！");
		}
		
	}
	

	/**
	 * 根据标签标好查询标签下的人员类别
	 * @param token
	 * @param id
	 * @param db2Service
	 * @return
	 */
	public ApiResponse<List<UserSimpleObj>> getUserListByLable(String token, Integer lableId,Db2Service db2Service) {
		List<UserSimpleObj> userList = new ArrayList<>();
		if(lableId != null ) {
			List<TEduBdUserDo> tebuDoList = db2Service.getBdUserInfoByUserLableRelation(lableId);
			if(tebuDoList !=null && tebuDoList.size() > 0) {
				for(int k = 0; k < tebuDoList.size(); k++) {
					UserSimpleObj aum = new UserSimpleObj();
					aum.setUserId(tebuDoList.get(k).getId());
					aum.setUserName(tebuDoList.get(k).getUserAccount());                                              //用户名
					aum.setFullName(tebuDoList.get(k).getName());                                                     //姓名
					aum.setAccountType(AppModConfig.accountTypeIdToNameMap.get(tebuDoList.get(k).getIsAdmin()));      //账号类型
					aum.setUserOrg(tebuDoList.get(k).getOrgName());                                                   //单位
					aum.setEmail(tebuDoList.get(k).getEmail());
					
					aum.setLableId(lableId);
					userList.add(aum);	
				}
			}
		}
		return new ApiResponse<>(userList);
	}
	
	/**
	 * 删除标签下的具体人员
	 * @param token
	 * @param id
	 * @param db2Service
	 * @return
	 */
	public ApiResponse<T> deleteUserLableRelation(String token,String userId,Integer lableId,Db2Service db2Service) {
		if(userId ==null) {
			return new ApiResponse<T>(IOTRspType.Fail,"userId不能为空！");
		}
		if(lableId ==null) {
			return new ApiResponse<T>(IOTRspType.Fail,"lableId不能为空！");
		}
		int iRet = db2Service.deleteByPrimaryKeyUserLableRelation(userId, lableId);
		if(iRet >= 0) {
			return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
		}else {
			return new ApiResponse<T>(IOTRspType.Fail,"删除失败！");
		}
		
	}

}
