package com.tfit.BdBiProcSrvShEduOmc.appmod.im.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.dto.DishEmailDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/** 
 * @Description: 预警推送管理
 * @Author: jianghy 
 * @Date: 2020/1/18
 * @Time: 16:52       
 */
public class WarnPushTaskMod {
	private static final Logger logger = LogManager.getLogger(WarnPushTaskMod.class.getName());
	ObjectMapper objectMapper = new ObjectMapper();


	/** 
	 * @Description: 通知入库
	 * @Param: [db2Service, warnType, warnAlertType, warnTitle, pushDate, userAccount, closingDate, dishDate, departmentName, dishMap] 
	 * @return: boolean 
	 * @Author: jianghy 
	 * @Date: 2020/1/19
	 * @Time: 9:43
	 */
	public boolean insertPushData(Db2Service db2Service,Integer schoolNum,Integer warnType,Integer warnAlertType,String warnTitle,String pushDate,String userAccount,String closingDate,String dishDate,String departmentName,Map<String,List<DishEmailDto>> dishMap) {
		String id = UUID.randomUUID().toString();
		//插入标题数据
		Map<String,Object> map = new HashedMap();
		map.put("id",id);
		map.put("warnType",warnType);//预警类型:(1：未排菜预警 , 2：未验收预警）
		map.put("warnTitle",warnTitle);//预警标题（例如：当日未排菜学校名单）
		map.put("searchEndDate",closingDate);//截至日期
		map.put("pushDate",pushDate);//推送时间
		map.put("userAccount",userAccount);//用户账户
		map.put("periodDate",dishDate);//供餐日期
		map.put("searchDepartment",departmentName);//统计部门
        map.put("schoolNum",schoolNum);//学校数量
		db2Service.insertWarnTitle(map);

		//插入内容数据
		List<LinkedHashMap<String,Object>> list = new ArrayList<>();
		for(Map.Entry<String, List<DishEmailDto>> totalMap : dishMap.entrySet()) {
			List<DishEmailDto> value = totalMap.getValue();
			if (CollectionUtils.isNotEmpty(value)){
				for (DishEmailDto dishEmailDto : value) {
					LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
					linkedHashMap.put("id", UUID.randomUUID().toString());
					linkedHashMap.put("titleId", id);//标题id
					linkedHashMap.put("departmentName", totalMap.getKey());//管理部门名称
					linkedHashMap.put("schTypeName", dishEmailDto.getSchType());//学制
					linkedHashMap.put("diningName", dishEmailDto.getSchName());//项目点名称
					linkedHashMap.put("warnType",warnType);//预警类型:(1：未排菜预警 , 2：未验收预警）
					linkedHashMap.put("warnAlertType", warnAlertType);//预警提示类型：(1：提示，2：提醒，3：预警，4：督办，5：追责)
					linkedHashMap.put("optDate", dishEmailDto.getNoDishDate());//通用日期(如，未排菜日期，未验收日期)
					linkedHashMap.put("searchEndDate", closingDate);//截至日期
					linkedHashMap.put("pushAccount", userAccount);//推送对象
					list.add(linkedHashMap);
				}
			}
		}
		db2Service.insertWarnContent(list);
		return true;
	}


	/** 
	 * @Description: 修改预警信息读取状态
	 * @Param: [db2Service, titleId] 
	 * @return: boolean 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 16:57       
	 */
	public String updateWarnReadSatus(HttpServletRequest request, Db2Service db2Service, String titleId){
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		String strResp = "";
		try {
			//授权码
			String token =request.getHeader("Authorization");
			//验证授权
//			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
			boolean verTokenFlag = true;
			if (verTokenFlag) {
				db2Service.updateWarnReadSatus(titleId);
				appCommonExternalModulesDto.setResCode(IOTRspType.Success.getCode().toString());
				appCommonExternalModulesDto.setResMsg("读取状态修改成功");
			} else {
				appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
			}
		} catch (Exception e) {
			appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
		}

		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}
}