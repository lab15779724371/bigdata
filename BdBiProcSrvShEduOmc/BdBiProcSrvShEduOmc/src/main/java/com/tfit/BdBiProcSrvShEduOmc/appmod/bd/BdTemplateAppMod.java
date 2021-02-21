package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbAnnounceAppMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdTemplate;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据学校列表应用模型
public class BdTemplateAppMod {
	
	private static final Logger logger = LogManager.getLogger(RbAnnounceAppMod.class.getName());
	
	/**
	 * 新增预警模板
	 * @param obj
	 * @param saasService
	 * @return
	 */
	public ApiResponse<T> saveTemplate(String token,TEduBdTemplate record, SaasService saasService,Db2Service db2Service) {
		
		if(record == null || record.getTemplateType() == null) {
			return new ApiResponse<T>(IOTRspType.Fail,"模板类型不能为空！");
		}
		
		
		//模板类型为教育局时，模板对象对象不能为空
		if(record.getTemplateType() == 2) {
			if(record == null || record.getTemplateObj() == null || "".equals(record.getTemplateObj())) {
				return new ApiResponse<T>(IOTRspType.Fail,"模板对象不能为空！");
			}
		}else if (record.getTemplateType() == 1) {
			record.setTemplateObj("-1");
		}else if (record.getTemplateType() == 3) {
			record.setTemplateObj("-2");
		}
		
		//附件信息
		if(record.getAmInfos() != null) {
			List<RbUlAttachment> amInfos = record.getAmInfos();
			if(amInfos.size() > 0) {
				String amInfo = "";
				for(int i = 0; i < amInfos.size(); i++) {
					String amRes = " ";
					int idx = -1;
					logger.info("当前附件URL：" + amInfos.get(i).getAmUrl() + "，文件服务域名：" + SpringConfig.repfile_srvdn);
					if((idx = amInfos.get(i).getAmUrl().indexOf(SpringConfig.repfile_srvdn)) != -1) {
						amRes = amInfos.get(i).getAmUrl().substring(idx+SpringConfig.repfile_srvdn.length(), amInfos.get(i).getAmUrl().length());
					}
					if(i < amInfos.size() - 1) {
						amInfo += amInfos.get(i).getAmName();
						amInfo += ",";
						amInfo += amRes;
						amInfo += ",";
					}
					else {
						amInfo += amInfos.get(i).getAmName();
						amInfo += ",";
						amInfo += amRes;
					}
				}
				record.setAmInfo(amInfo);
				record.setAmFlag(1);
			}
			else {
				record.setAmFlag(0);
			}
		}
		else {
			record.setAmFlag(0);
		}
		
		
		TEduBdUserDo parTebuDo = db2Service.getBdUserInfoByToken(token);
		TEduBdTemplate template = saasService.selectByTemplate(record);
		if(template ==null || template.getId()==null || "".equals(template.getId())) {
			 Integer maxId = saasService.selectMaxIdList();
			 if(maxId == null ) {
				 maxId = 1;
			 }else {
				 maxId ++;
			 }
			 record.setId(maxId.toString());
			 
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
			 
			//新增
			int iRet = saasService.insert(record);
			if(iRet >= 0) {
				return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
			}else {
				return new ApiResponse<T>(IOTRspType.Fail,"保存失败！");
			}
		}else {
			record.setId(template.getId());
			record.setCreateTime(template.getCreateTime());
			record.setCreator(template.getCreator());
			//修改
			if(parTebuDo != null && parTebuDo.getUserAccount() !=null && !"".equals(parTebuDo.getUserAccount())) {
    	    	//更新人
				 record.setUpdater(parTebuDo.getUserAccount());
			 }
			 //更新时间
			 record.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
			
			int iRet = saasService.updateByPrimaryKey(record);
			if(iRet >= 0) {
				return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
			}else {
				return new ApiResponse<T>(IOTRspType.Fail,"修改失败！");
			}
		}
		
		
	}	
	
	/**
	 * 修改预警模板
	 * @param obj
	 * @param saasService
	 * @return
	 */
	public ApiResponse<T> updateTemplate(TEduBdTemplate record, SaasService saasService) {
		
	    
		if(record == null || record.getId() == null || "".equals(record.getId())) {
			return new ApiResponse<T>(IOTRspType.Fail,"模板编号不能为空！");
		}
		if(record == null || record.getTemplateType() == null) {
			return new ApiResponse<T>(IOTRspType.Fail,"模板类型不能为空！");
		}
		
		//模板类型为教育局时，模板对象对象不能为空
		if(record.getTemplateType() == 2) {
			if(record == null || record.getTemplateObj() == null || "".equals(record.getTemplateObj())) {
				return new ApiResponse<T>(IOTRspType.Fail,"模板对象不能为空！");
			}
		}
		TEduBdTemplate template = saasService.selectByPrimaryKey(record.getId());
		if(template == null || template.getId() == null || "".equals(template.getId())) {
			return new ApiResponse<T>(IOTRspType.Fail,"模板不存在！");
		}
		
		int iRet = saasService.updateByPrimaryKey(record);
		if(iRet >= 0) {
			return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
		}else {
			return new ApiResponse<T>(IOTRspType.Fail,"修改失败！");
		}
	}	
	
	/**
	 * 删除预警模板
	 * @param obj
	 * @param saasService
	 * @return
	 */
	public ApiResponse<T> deleteTemplate(String id, SaasService saasService) {
		
		TEduBdTemplate template = saasService.selectByPrimaryKey(id);
		if(template == null || template.getId() == null || "".equals(template.getId())) {
			return new ApiResponse<T>(IOTRspType.Fail,"模板不存在！");
		}
		
		int iRet = saasService.deleteByPrimaryKey(id);
		if(iRet >= 0) {
			return new ApiResponse<T>(IOTRspType.Success,IOTRspType.Success.getMsg());
		}else {
			return new ApiResponse<T>(IOTRspType.Fail,"删除失败！");
		}
	}	
	
	/**
	 *获取预警模板
	 * @param obj
	 * @param saasService
	 * @return
	 */
	public ApiResponse<TEduBdTemplate> getTemplate(String id, SaasService saasService) {
		TEduBdTemplate template = saasService.selectByPrimaryKey(id);
		
		//附件信息
		List<RbUlAttachment> amInfos = new ArrayList<>();
		if(template.getAmInfo() != null) {
			String[] strAmInfos = template.getAmInfo().split(",");
			if(strAmInfos.length > 0 && strAmInfos.length%2 == 0) {
				for(int i = 0; i < strAmInfos.length/2; i++) {
					RbUlAttachment rua = new RbUlAttachment();
					rua.setAmName(strAmInfos[2*i]);
					rua.setAmUrl(SpringConfig.repfile_srvdn+strAmInfos[2*i+1]);
					amInfos.add(rua);
				}
			}
		}
		template.setAmInfos(amInfos);
		template.setAmInfo("");
		
		if(template.getAnnCont() != null)
				template.setTemplateContent(new String(template.getAnnCont(), 0, template.getAnnCont().length));                  //公告内容
			else
				template.setTemplateContent("");
		template.setAnnCont(null);
		
		return new ApiResponse<TEduBdTemplate>(template);
	}	
	
	/**
	 *获取预警模板
	 * @param obj
	 * @param saasService
	 * @return
	 */
	public ApiResponse<List<TEduBdTemplate>> listTemplate( SaasService saasService) {
		List<TEduBdTemplate> templateList = saasService.selectAllList();
		for(TEduBdTemplate obj : templateList) {
			if(obj != null && obj.getLastUpdateTime() !=null && !"".equals(obj.getLastUpdateTime())) {
				obj.setLastUpdateTime(obj.getLastUpdateTime().substring(0,obj.getLastUpdateTime().lastIndexOf(":")));
			}
		}
		return new ApiResponse<List<TEduBdTemplate>>(templateList);
	}	
	
}



