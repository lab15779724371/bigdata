package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.task.PpCheckDetsTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.*;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 预警规则
 * @Author: jianghy
 * @Date: 2020/1/15
 * @Time: 14:34
 */
public class LevelWarningMod {

	ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * @Description: 设置预警规则
	 * @Param: [request, db1Service, db2Service]
	 * @return: java.lang.String
	 * @Author: jianghy
	 * @Date: 2020/1/15
	 * @Time: 14:34
	 */
	public String appModFunc(HttpServletRequest request,WarnLevelBody wlb,Db1Service db1Service, Db2Service db2Service) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		List<AppCommonDao> sourceDao = null;
		String strResp = null;
		AppCommonDao pageTotal = null;
		List<LinkedHashMap<String, Object>> dataList = new ArrayList();
		LinkedHashMap<String, Object> data =new LinkedHashMap<String, Object>();
		// 业务操作
		try {
			//授权码
			String token =request.getHeader("Authorization");
			//验证授权
//			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
			boolean verTokenFlag = true;
			if (verTokenFlag) {
				//获取当前登录用户
				TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
				String pushReceiverMsg="";
				if (wlb != null) {
					List<PushReceiverInfo> pushReceiverInfo= wlb.getPushReceiverMsg();
					if (!CollectionUtils.isEmpty(pushReceiverInfo)){
						for (PushReceiverInfo info : pushReceiverInfo) {
							pushReceiverMsg += info.getUserAccount() + ",";
							pushReceiverMsg += info.getName() +",";
							pushReceiverMsg += info.getMobileNo() +",";
							pushReceiverMsg += info.getEmail() +"/";

						}
						pushReceiverMsg = pushReceiverMsg.substring(0,pushReceiverMsg.length()-1);
					}
				}
				wlb.setPushReceiverMsgStr(pushReceiverMsg);
				wlb.setUserAccount(userINfo.getUserAccount());
				//如果id不为空代表是编辑操作
				if (wlb.getId() != null){
					db2Service.updateWarnRuleSetting(wlb);
				}else{
					//设置固定格式数据
					setWarnLevelBody(wlb);
					db2Service.insertWarnRuleSetting(wlb);
				}
				appCommonExternalModulesDto.setResCode(IOTRspType.Success.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.Success.getMsg());
			} else {
				appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			appCommonExternalModulesDto.setResCode(IOTRspType.SET_WARN_RULE_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(IOTRspType.SET_WARN_RULE_ERR.getMsg());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
			strResp = new ToolUtil().rmExternalStructure(strResp);
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}


	/** 
	 * @Description: 获取预警规则
	 * @Param: [request, db1Service, db2Service] 
	 * @return: java.lang.String 
	 * @Author: jianghy 
	 * @Date: 2020/1/15
	 * @Time: 15:23       
	 */
	public String getWarnRuleSetting(HttpServletRequest request, JSONObject jsonObject,Db1Service db1Service, Db2Service db2Service) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		List<AppCommonDao> sourceDao = null;
		AppCommonDao pageTotal = null;
		List<Map<String,String>> dataList = new ArrayList();

		// 业务操作
		try {
			// 授权码
			String token = request.getHeader("Authorization");
			// 验证授权
//            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);
			boolean verTokenFlag = true;
			if (verTokenFlag) {
				Integer warnType = jsonObject.getInteger("warnType");
				Integer warnAlertType = jsonObject.getInteger("warnAlertType");
				sourceDao = db2Service.getCheckWarnSetting(warnType,warnAlertType);
				WarnLevelBody wlb = PpCheckDetsTaskMod.getWarnRuleInfo(sourceDao);
				// 获取列表数据
				appCommonExternalModulesDto.setData(wlb);
				// 以上业务逻辑层修改
				// 固定返回
			} else {
				appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
			}
		} catch (Exception e) {
			appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
		}

		ObjectMapper objectMapper = new ObjectMapper();
		String strResp = null;
		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
			strResp = new ToolUtil().rmExternalStructure(strResp);
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}

	/** 
	 * @Description: 编辑预警规则固定内容
	 * @Param: [wlb] 
	 * @return: com.tfit.BdBiProcSrvShEduOmc.dao.WarnLevelBody 
	 * @Author: jianghy 
	 * @Date: 2020/1/15
	 * @Time: 15:09       
	 */
	public WarnLevelBody setWarnLevelBody(WarnLevelBody wlb){
		String warnDataTime = "";
		String warnPushTime = "";
		String warnPushContent = "";
		if (wlb != null){
			if (wlb.getWarnAlertType() == 1){
				warnDataTime = "14:00";
				warnPushTime = "14:00";
				if (wlb.getWarnType() == 1){
					warnPushContent = "次日排菜未上报学校名单";
				}else if (wlb.getWarnType() == 2){
					warnPushContent = "当日验收未上报学校名单";
				}
			}else if (wlb.getWarnAlertType() == 2){
				warnDataTime = "16:00";
				warnPushTime = "16:00";
				if (wlb.getWarnType() == 1){
					warnPushContent = "次日排菜未上报学校名单";
				}else if (wlb.getWarnType() == 2){
					warnPushContent = "当日验收未上报学校名单";
				}
			}else if (wlb.getWarnAlertType() == 3){
				warnDataTime = "17:00";
				warnPushTime = "17:00";
				if (wlb.getWarnType() == 1){
					warnPushContent = "次日排菜未上报学校名单";
				}else if (wlb.getWarnType() == 2){
					warnPushContent = "当日验收未上报学校名单";
				}
			}else if (wlb.getWarnAlertType() == 4){
				warnDataTime = "05:00";
				warnPushTime = "05:00";
				if (wlb.getWarnType() == 1){
					warnPushContent = "当日排菜未上报学校名单";
				}else if (wlb.getWarnType() == 2){
					warnPushContent = "昨日验收未上报学校名单";
				}
			}else if (wlb.getWarnAlertType() == 5){
				warnDataTime = "11:00";
				warnPushTime = "11:00";
				if (wlb.getWarnType() == 1){
					warnPushContent = "当日排菜未上报学校名单";
				}else if (wlb.getWarnType() == 2){
					warnPushContent = "昨日验收未上报学校名单";
				}
			}
		}
		wlb.setWarnDataTime(warnDataTime);
		wlb.setWarnPushTime(warnPushTime);
		wlb.setWarnPushContent(warnPushContent);
		return wlb;
	}
}
