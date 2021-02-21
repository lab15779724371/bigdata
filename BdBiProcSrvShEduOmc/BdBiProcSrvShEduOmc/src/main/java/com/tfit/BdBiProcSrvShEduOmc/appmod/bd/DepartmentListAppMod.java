package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.DepartmentList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.DepartmentListDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//基础数据学校列表应用模型
public class DepartmentListAppMod {
	private static final Logger logger = LogManager.getLogger(DepartmentListAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//二级排序条件
	final String[] methods = {"getDistName", "getSchType"};
	final String[] sorts = {"asc", "asc"};
	final String[] dataTypes = {"String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 14;
	//模拟数据函数
	private DepartmentListDTO SimuDataFunc() {
		return null;
	}
	
	//获取证件索引，0对应食品经营许可证、1对应餐饮服务许可证
	int getLicIndex(String[] keyVals) {
		int i, index = 0;
		i = AppModConfig.getVarValIndex(keyVals, "slictype");
		if(i != -1) {
			if(!keyVals[i].equalsIgnoreCase("null")) 
				index = 0;
			else
				index = 1;
		}
		
		return index;
	}
	
	// 基础数据学校列表函数
	DepartmentListDTO deparmentList(String deparmentId,Db1Service db1Service,Db2Service db2Service, Map<String,String> inputMap) {
		
		String dataType = inputMap.get("dataType");
		
		if(CommonUtil.isNotEmpty(inputMap.get("page"))) {
			this.curPageNum = Integer.parseInt(inputMap.get("page").toString());
		}
		if(CommonUtil.isNotEmpty(inputMap.get("pageSize"))) {
			this.pageSize = Integer.parseInt(inputMap.get("pageSize").toString());
		}
		
		DepartmentListDTO bslDto = new DepartmentListDTO();
		
		DepartmentObj inputObj = CommonUtil.map2Object(inputMap, DepartmentObj.class);
		if(inputObj == null) {
			//return new ApiResponse<>(IOTRspType.Param_VisitFrmErr, IOTRspType.Param_VisitFrmErr.getMsg()); 
		}
		//权限过滤
		if(CommonUtil.isEmpty(inputObj.getDepartmentId())) {
			inputObj.setDepartmentId(deparmentId);
		}
		
		List<DepartmentList> resultList = new ArrayList<DepartmentList>();
		List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(inputObj,null, -1, -1);	
		
		//获取用户信息，用于匹配部门的编辑人
		Map<String,TEduBdUserDo> userMap = new HashMap<String,TEduBdUserDo>();
		//此接口分下拉列表和管理列表界面
		if("list".equals(dataType)) {
			List<TEduBdUserDo>  tebuDoList = db2Service.getAllBdUserInfoByParentId(null,null,null);
			if(tebuDoList != null) {
				userMap = tebuDoList.stream().collect(Collectors.toMap(TEduBdUserDo::getId,(b)->b));
			}
		}
		for(DepartmentObj obj : deparmentList) {
			DepartmentList departmentList = new DepartmentList();
			
			try {
				BeanUtils.copyProperties(departmentList, obj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			if("list".equals(dataType)) {
				departmentList.setUpdaterName(userMap.get(departmentList.getUpdater())==null?"":userMap.get(departmentList.getUpdater()).getName());
				resultList.add(departmentList);
			}else if("departmentList".equals(dataType)) {
				 //下拉列表只获取16个区，拼接市教委
				  if(CommonUtil.isNotEmpty
						  (departmentList.getDepartmentId()) &&
						  (Integer.valueOf(departmentList.getDepartmentId()) >=1 && Integer.valueOf(departmentList.getDepartmentId()) <=16)  ) {
					  resultList.add(departmentList);
				  }
				  
				  departmentList.setDepartmentId(departmentList.getDepartmentId());
			}else {
				
			  //下拉列表只获取16个区，拼接市教委
			  if(CommonUtil.isNotEmpty
					  (departmentList.getDepartmentId()) &&
					  (Integer.valueOf(departmentList.getDepartmentId()) >=1 && Integer.valueOf(departmentList.getDepartmentId()) <=16)  ) {
				  resultList.add(departmentList);
			  }
			  
			  departmentList.setDepartmentId(departmentList.getDepartmentName());
			}
		}
		
		if("list".equals(dataType)) {
			
		}else if("departmentList".equals(dataType)) {
			
		}else {
			DepartmentList departmentList = new DepartmentList();
			//主管部门编号
			departmentList.setDepartmentId("市教委");
		    //主管部门名称
		    departmentList.setDepartmentName("市教委");
			resultList.add(departmentList);
		}
		
		PageBean<DepartmentList> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<DepartmentList>(resultList, 1, deparmentList.size());
		}else {
			pageBean = new PageBean<DepartmentList>(resultList, curPageNum, pageSize);
		}
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		// 设置数据
		bslDto.setDepartmentList(pageBean.getCurPageData());
		
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		
		return bslDto;
	}	
	
	// 基础数据学校列表模型函数
	public DepartmentListDTO appModFunc(String token,Map<String,String> inputMap, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		DepartmentListDTO bslDto = null;
		
		String province = inputMap.get("province")==null?null:inputMap.get("province").toString();
		String distName = inputMap.get("distName")==null?null:inputMap.get("distName").toString();
		String prefCity = inputMap.get("prefCity")==null?null:inputMap.get("prefCity").toString();
		
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";  		
			// 参数查找标识
			boolean bfind = false;
			String departmentId = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) {    // 按区域，省或直辖市处理
				departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
				// 基础数据学校列表函数
				bslDto = deparmentList(departmentId, db1Service, db2Service, inputMap);	
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					departmentId = null;
				}
				if (bfind) {
					departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据学校列表函数
					bslDto = deparmentList(departmentId, db1Service, db2Service, inputMap);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}															
		}
		else {    //模拟数据
			//模拟数据函数
			bslDto = SimuDataFunc();
		}		

		return bslDto;
	}
}



