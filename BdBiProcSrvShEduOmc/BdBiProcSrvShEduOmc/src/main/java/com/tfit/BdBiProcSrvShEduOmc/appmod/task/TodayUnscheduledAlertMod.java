package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddWarnBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.PushRecipientInfo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class TodayUnscheduledAlertMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	ObjectMapper objectMapper = new ObjectMapper();
	// 资源路径
	String fileResPath = "/amSaveUserInfo/";

	public String appModFunc(HttpServletRequest request, Db1Service db1Service, Db2Service db2Service) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		List<AppCommonDao> sourceDao = null;
		AppCommonDao pageTotal = null;
		List<LinkedHashMap<String, Object>> dataList = new ArrayList();
		LinkedHashMap<String, Object> data =new LinkedHashMap<String, Object>();
		// 业务操作
		try {
	  		//授权码
	  		String token =request.getHeader("Authorization");
	  		//验证授权
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
			if (verTokenFlag) {

				// Body传输内容，格式为 application/json
				AddWarnBody asuib = new AddWarnBody();
				String strBodyCont = null;
				try {
					strBodyCont = new ToolUtil().GetBodyJsonReq(request, false);
					if (strBodyCont != null)
						asuib = objectMapper.readValue(strBodyCont, AddWarnBody.class);
				}catch(Exception e) {
					//body内容为空
				}
				TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
				String category="2";
				if (asuib != null && strBodyCont !=null && !strBodyCont.trim().isEmpty()) {
					List<PushRecipientInfo> pushList=asuib.getPushRecipient();
					String pushRecipient="";
					for(int i=0;i<pushList.size();i++) {
						pushRecipient +="("+pushList.get(i).getUserAccount()+",";
						pushRecipient +=pushList.get(i).getName()+",";
						pushRecipient +=pushList.get(i).getStat()+")|";
					}
					pushRecipient=pushRecipient.substring(0,pushRecipient.length()-1);
					
					//筛选参数
					LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
					filterParamMap.put("category", category);
					filterParamMap.put("unscheduled", asuib.getUnscheduled());
//					filterParamMap.put("week", asuib.getWeek());
					filterParamMap.put("time", asuib.getTime());
					filterParamMap.put("frequency", asuib.getFrequency());
					filterParamMap.put("interval", asuib.getInterval());
					filterParamMap.put("pushRecipient", pushRecipient);
					filterParamMap.put("emailRecipient", (asuib.getEmailRecipient())==null?"":(asuib.getEmailRecipient()).toString().replace("[", "").replace("]", ""));
					db2Service.getLwUnscheduledAlertUpdate(filterParamMap,userINfo.getUserAccount(),category);
				}
				//默认发送人
				String pushRecipient="";
				if(userINfo.getIsAdmin()>=1) {
					sourceDao=db2Service.getoperatorList(userINfo.getOrgName());
					for(int i=0;i<sourceDao.size();i++) {
						String stat="0";
						if((userINfo.getUserAccount()).equals((String) sourceDao.get(i).getCommonMap().get("userAccount"))) {
							stat="1";
						}
						pushRecipient +="("+(String) sourceDao.get(i).getCommonMap().get("userAccount")+",";
						pushRecipient +=(String) sourceDao.get(i).getCommonMap().get("name")+",";
						pushRecipient +=stat+")|";
					}
					pushRecipient=pushRecipient.substring(0,pushRecipient.length()-1);
				}else {
					pushRecipient+="("+userINfo.getUserAccount()+",";
					pushRecipient+=userINfo.getName()+",";
					pushRecipient+="1"+")";
				}
				
				//查询下周未排菜预警信息
				AppCommonDao resultInfo=db2Service.getLwUnscheduledAlert(userINfo.getUserAccount(), category,pushRecipient);
				String recipient=(String) resultInfo.getCommonMap().get("pushRecipient");
				data=resultInfo.getCommonMap();
				data.remove("week");
				data.remove("pushRecipient");
				data.remove("dishesWarn");
				data.remove("deliveryWarn");
				
				String[] recipientArr=recipient.split("\\|");
				for(int i=0;i<recipientArr.length;i++) {
					String value=recipientArr[i].replace("(", "").replace(")", "");
					LinkedHashMap<String, Object> recipientMap =new LinkedHashMap<String, Object>();
					recipientMap.put("userAccount",value.split(",")[0]);
					recipientMap.put("name",value.split(",")[1]);
					recipientMap.put("stat",value.split(",")[2]);
					dataList.add(recipientMap);
				}
				appCommonData.setData(data);
				appCommonData.setDataList(dataList);
				
				appCommonExternalModulesDto.setData(appCommonData);
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

		String strResp = null;
		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
			strResp = new ToolUtil().rmExternalStructure(strResp,"pushRecipient");
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}
}
