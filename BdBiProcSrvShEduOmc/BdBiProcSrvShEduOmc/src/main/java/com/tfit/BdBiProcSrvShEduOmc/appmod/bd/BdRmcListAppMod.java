package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProLicenseDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.EduSupplierDetail;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//基础数据团餐公司列表应用模型
public class BdRmcListAppMod {
	private static final Logger logger = LogManager.getLogger(BdRmcListAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 14;
	
	//数组数据初始化	
	String[] compName_Array = {"上海沪鑫餐饮管理有限公司", "上海沪鑫餐饮管理有限公司"};
	String[] location_Array = {"金山区", "金山区"};
	String[] detailAddr_Array = {"亭林镇康发路201号5幢1楼47室", "亭林镇康发路201号5幢1楼47室"};
	String[] email_Array = {"-", "-"};
	String[] contact_Array = {"吴福园", "吴福园"};
	String[] mobilePhone_Array = {"13816276959", "13816276959"};
	String[] qaLeader_Array = {"-", "-"};
	String[] contactNo_Array = {"-", "-"};
	int[] relPpNum_Array = {12, 30};
	String[] licName_Array = {"-", "-"};
	String[] licPic_Array = {"-", "-"};
	String[] licOptUnit_Array = {"-", "-"};
	String[] licNo_Array = {"-", "-"};
	String[] licIssueDate_Array = {"-", "-"};
	String[] licValidDate_Array = {"-", "-"};
	String[] uscc_Array = {"-", "-"};
	String[] usccPic_Array = {"-", "-"};
	String[] usccOptUnit_Array = {"-", "-"};
	String[] optScope_Array = {"-", "-"};
	String[] regAddr_Array = {"-", "-"};
	String[] udetailAddr_Array = {"-", "-"};
	String[] regCapital_Array = {"300", "600"};
	String[] corpRepName_Array = {"-", "-"};
	String[] ucssIssueAuth_Array = {"-", "-"};
	String[] ucssIssueDate_Array = {"-", "-"};
	String[] ucssValidDate_Array = {"-", "-"};
	String[] fhlName_Array = {"-", "-"};
	String[] fhlPic_Array = {"-", "-"};
	String[] fhlOptUnit_Array = {"-", "-"};
	String[] fhlNo_Array = {"-", "-"};
	String[] fhlIssueDate_Array = {"-", "-"};
	String[] fhlValidDate_Array = {"-", "-"};
	String[] tplName_Array = {"-", "-"};
	String[] tplPic_Array = {"-", "-"};
	String[] tplOptUnit_Array = {"-", "-"};
	String[] tplNo_Array = {"-", "-"};
	String[] tplIssueDate_Array = {"-", "-"};
	String[] tplValidDate_Array = {"-", "-"};
	String[] iosName_Array = {"-", "-"};
	String[] iosPic_Array = {"-", "-"};
	String[] iosOptUnit_Array = {"-", "-"};
	String[] iosNo_Array = {"-", "-"};
	String[] iosIssueDate_Array = {"-", "-"};
	String[] iosValidDate_Array = {"-", "-"};
	
	//模拟数据函数
	private BdRmcListDTO SimuDataFunc() {
		BdRmcListDTO brlDto = new BdRmcListDTO();
		//时戳
		brlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据团餐公司列表模拟数据
		List<BdRmcList> bdRmcList = new ArrayList<>();
		//赋值
		for (int i = 0; i < compName_Array.length; i++) {
			BdRmcList brl = new BdRmcList();			
			brl.setCompName(compName_Array[i]);
			brl.setLocation(location_Array[i]);
			brl.setDetailAddr(detailAddr_Array[i]);
			brl.setEmail(email_Array[i]);
			brl.setContact(contact_Array[i]);
			brl.setMobilePhone(mobilePhone_Array[i]);
			brl.setQaLeader(qaLeader_Array[i]);
			brl.setContactNo(contactNo_Array[i]);
			brl.setRelPpNum(relPpNum_Array[i]);
			brl.setLicName(licName_Array[i]);
			brl.setLicPic(licPic_Array[i]);
			brl.setLicOptUnit(licOptUnit_Array[i]);
			brl.setLicNo(licNo_Array[i]);
			brl.setLicIssueDate(licIssueDate_Array[i]);
			brl.setLicValidDate(licValidDate_Array[i]);
			brl.setUscc(uscc_Array[i]);
			brl.setUsccPic(usccPic_Array[i]);
			brl.setUsccOptUnit(usccOptUnit_Array[i]);
			brl.setOptScope(optScope_Array[i]);
			brl.setRegAddr(regAddr_Array[i]);
			brl.setUdetailAddr(udetailAddr_Array[i]);
			brl.setRegCapital(regCapital_Array[i]);
			brl.setCorpRepName(corpRepName_Array[i]);
			brl.setUcssIssueAuth(ucssIssueAuth_Array[i]);
			brl.setUcssIssueDate(ucssIssueDate_Array[i]);
			brl.setUcssValidDate(ucssValidDate_Array[i]);
			brl.setFhlName(fhlName_Array[i]);
			brl.setFhlPic(fhlPic_Array[i]);
			brl.setFhlOptUnit(fhlOptUnit_Array[i]);
			brl.setFhlNo(fhlNo_Array[i]);
			brl.setFhlIssueDate(fhlIssueDate_Array[i]);
			brl.setFhlValidDate(fhlValidDate_Array[i]);
			brl.setTplName(tplName_Array[i]);
			brl.setTplPic(tplPic_Array[i]);
			brl.setTplOptUnit(tplOptUnit_Array[i]);
			brl.setTplNo(tplNo_Array[i]);
			brl.setTplIssueDate(tplIssueDate_Array[i]);
			brl.setTplValidDate(tplValidDate_Array[i]);
			brl.setIosName(iosName_Array[i]);
			brl.setIosPic(iosPic_Array[i]);
			brl.setIosOptUnit(iosOptUnit_Array[i]);
			brl.setIosNo(iosNo_Array[i]);
			brl.setIosIssueDate(iosIssueDate_Array[i]);
			brl.setIosValidDate(iosValidDate_Array[i]);			
			bdRmcList.add(brl);
		}
		//设置数据
		brlDto.setBdRmcList(bdRmcList);
		//分页
		PageInfo pageInfo = new PageInfo();
		if(actPageSize == 0)
			pageTotal = compName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		brlDto.setPageInfo(pageInfo);
		//消息ID
		brlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return brlDto;
	}
	
	// 基础数据团餐公司列表函数
	BdRmcListDTO bdRmcList(String distIdorSCName, List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, 
			String compName, String contact, String fblNo, String uscc, 
			String regCapital) {
		BdRmcListDTO brlDto = new BdRmcListDTO();
		List<BdRmcList> bdRmcList = new ArrayList<>();
		Map<String, String> groupSupplierDetailMap = new HashMap<>();
		int i;
		String key = null, keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//查询变脸索引
		int[] queryVarIdxs = new int[13];
		for(i = 0; i < queryVarIdxs.length; i++)
			queryVarIdxs[i] = -1;
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1,3);
		for(i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		//学校id和供应商id
    	Map<String, String> SchIdTosupIdMap = new HashMap<>();
    	
    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
    	if(tessDoList != null) {
    		for(i = 0; i < tessDoList.size(); i++) {
    			SchIdTosupIdMap.put(tessDoList.get(i).getSchoolId(), tessDoList.get(i).getSupplierId());
    		}
    	}
		// 基础数据团餐公司详情
    	key = "group-supplier-detail";
    	groupSupplierDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
    	if (groupSupplierDetailMap != null) {
    		for (String curKey : groupSupplierDetailMap.keySet()) {
    			logger.info("*****************01");
    			keyVal = groupSupplierDetailMap.get(curKey);
    			// 基础数据团餐公司列表
    			String[] keyVals = keyVal.split(";");
    			if(keyVals.length >= 20) {
    				BdRmcList brl = new BdRmcList();
    				//团餐公司id
    				i = AppModConfig.getVarValIndex(keyVals, "id");
    				String rmcId = null;
    				if(i != -1)
    					rmcId = keyVals[i];
    				if(rmcId == null)
    					continue ;
    				//企业名称
    				brl.setCompName("-");
    				i = AppModConfig.getVarValIndex(keyVals, "suppliername");    				
    				if(i != -1) {
    					queryVarIdxs[0] = i;
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						brl.setCompName(keyVals[i]);
    				}
    				//所在地
    				brl.setLocation("-");
    				i = AppModConfig.getVarValIndex(keyVals, "area");    				
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						brl.setLocation(keyVals[i]);
    				}
    				//详细地址
    				brl.setDetailAddr("-");
    				i = AppModConfig.getVarValIndex(keyVals, "address");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						brl.setDetailAddr(keyVals[i]);
    				}
    				//电子邮箱
    				brl.setEmail("-");
    				//联系人
    				brl.setContact("-");
    				i = AppModConfig.getVarValIndex(keyVals, "contact");
    				if(i != -1) {
    					queryVarIdxs[1] = i;
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						brl.setContact(keyVals[i]);
    				}
    				//手机号
    				i = AppModConfig.getVarValIndex(keyVals, "contactway");
    				brl.setMobilePhone("-");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						brl.setMobilePhone(keyVals[i]);
    				}
    				//质量负责人
    				brl.setQaLeader("-");
    				i = AppModConfig.getVarValIndex(keyVals, "qaperson");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null"))
    						brl.setQaLeader(keyVals[i]);
    				}
    				//联系电话
    				brl.setContactNo("-");
    				i = AppModConfig.getVarValIndex(keyVals, "qaway");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						brl.setContactNo(keyVals[i]);
    				}
    				//关联项目点
    				int relPpNum = 0;
    				for(String curKey2 : SchIdTosupIdMap.keySet()) {
    					String curRmcId = SchIdTosupIdMap.get(curKey2);
    					if(curRmcId!=null && curRmcId.equals(rmcId))
    						relPpNum++;
    				}
    				brl.setRelPpNum(relPpNum);
    				//获取证照信息
    				TProLicenseDo tplDo1 = null;
    				tplDo1 = saasService.getLicenseInfoBySupplierIdLicType(rmcId, 1);
    				if(tplDo1 == null)
    					tplDo1 = saasService.getLicenseInfoBySupplierIdLicType(rmcId, 0);
    				//证照名称
    				brl.setLicName("-");
    				if(tplDo1 != null) {
    					if(tplDo1.getLicName() != null)
    						brl.setLicName(tplDo1.getLicName());
    				}
    				//证照图片
    				brl.setLicPic("-");
    				if(tplDo1 != null) {
    					String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
    					if(tplDo1.getLicPic() != null)
    						brl.setLicPic(picSrvDn + tplDo1.getLicPic());
    				}
    				//经营单位
    				brl.setLicOptUnit("-");
    				if(tplDo1 != null) {
    					if(tplDo1.getJobOrganization() != null)
    						brl.setLicOptUnit(tplDo1.getJobOrganization());
    				}
    				//许可证号
    				brl.setLicNo("-");
    				if(tplDo1 != null) {
    					if(tplDo1.getLicNo() != null)
    						brl.setLicNo(tplDo1.getLicNo());
    				}
    				//发证日期
    				brl.setLicIssueDate("-");
    				if(tplDo1 != null) {
    					if(tplDo1.getGiveLicDate() != null)
    						brl.setLicIssueDate(tplDo1.getGiveLicDate().toString());
    				}
    				//有效日期
    				brl.setLicValidDate("-");
    				if(tplDo1 != null) {
    					if(tplDo1.getLicEndDate() != null)
    						brl.setLicIssueDate(tplDo1.getLicEndDate().toString());
    				}
    				//获取营业执照信息
    				TProLicenseDo tplDo2 = null;
    				tplDo2 = saasService.getLicenseInfoBySupplierIdLicType(rmcId, 5);
    				//统一社会信用代码
    				brl.setUscc("-");
    				if(tplDo2 != null) {
    					if(tplDo2.getLicNo() != null)
    						brl.setUscc(tplDo2.getLicNo());
    				}
    				//证照图片
    				brl.setUsccPic("-");
    				if(tplDo2 != null) {
    					String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
    					if(tplDo2.getLicPic() != null)
    						brl.setUsccPic(picSrvDn + tplDo2.getLicPic());
    				}
    				//经营单位
    				brl.setUsccOptUnit("-");
    				if(tplDo2 != null) {
    					if(tplDo2.getJobOrganization() != null)
    						brl.setUsccOptUnit(tplDo2.getJobOrganization());
    				}
    				//经营范围
    				brl.setOptScope("-");
    				if(tplDo2 != null) {
    					if(tplDo2.getOccupationRange() != null)
    						brl.setOptScope(tplDo2.getOccupationRange());
    				}
    				//注册地址
    				brl.setRegAddr("-");
    				//详细地址
    				brl.setUdetailAddr("-");
    				//注册资本
    				brl.setRegCapital("-");
    				i = AppModConfig.getVarValIndex(keyVals, "regcapital");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						brl.setRegCapital(keyVals[i]);
    				}
    				//法人代表
    				brl.setCorpRepName("-");
    				i = AppModConfig.getVarValIndex(keyVals, "corporation");
    				if(i != -1) {
    					if(!keyVals[i].equalsIgnoreCase("null")) 
    						brl.setCorpRepName(keyVals[i]);
    				}
    				//发证机关
    				brl.setUcssIssueAuth("-");
    				if(tplDo2 != null) {
    					if(tplDo2.getOperation() != null) {
    						brl.setUcssIssueAuth(tplDo2.getOperation());
    					}
    				}
    				//发证日期
    				brl.setUcssIssueDate("-");
    				if(tplDo2 != null) {
    					if(tplDo2.getGiveLicDate() != null) {
    						brl.setUcssIssueDate(tplDo2.getGiveLicDate().toString());
    					}
    				}
    				//有效日期
    				brl.setUcssValidDate("-");
    				if(tplDo2 != null) {
    					if(tplDo2.getLicEndDate() != null) {
    						brl.setUcssValidDate(tplDo2.getLicEndDate().toString());
    					}
    				}
    				//获取食品卫生许可证信息
    				TProLicenseDo tplDo3 = null;
    				tplDo3 = saasService.getLicenseInfoBySupplierIdLicType(rmcId, 13);
    				//食品卫生许可证
    				brl.setFhlName("-");
    				if(tplDo3 != null) {
    					if(tplDo3.getLicName() != null)
    						brl.setFhlName(tplDo3.getLicName());
    				}
    				//证照图片
    				brl.setFhlPic("-");
    				if(tplDo3 != null) {
    					String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
    					if(tplDo3.getLicPic() != null)
    						brl.setFhlPic(picSrvDn + tplDo3.getLicPic());
    				}
    				//经营单位
    				brl.setFhlOptUnit("-");
    				if(tplDo3 != null) {
    					if(tplDo3.getJobOrganization() != null)
    						brl.setFhlOptUnit(tplDo3.getJobOrganization());
    				}
    				//许可证号
    				brl.setFhlNo("-");
    				if(tplDo3 != null) {
    					if(tplDo3.getLicNo() != null)
    						brl.setFhlNo(tplDo3.getLicNo());
    				}
    				//发证日期
    				brl.setFhlIssueDate("-");
    				if(tplDo3 != null) {
    					if(tplDo3.getGiveLicDate() != null) {
    						brl.setFhlIssueDate(tplDo3.getGiveLicDate().toString());
    					}
    				}
    				//有效日期
    				brl.setFhlValidDate("-");
    				if(tplDo3 != null) {
    					if(tplDo3.getLicEndDate() != null)
    						brl.setFhlValidDate(tplDo3.getLicEndDate().toString());
    				}
    				//获取运输许可证信息
    				TProLicenseDo tplDo4 = null;
    				tplDo4 = saasService.getLicenseInfoBySupplierIdLicType(rmcId, 14);
    				//运输许可证
    				brl.setTplName("-");
    				if(tplDo4 != null) {
    					if(tplDo4.getLicName() != null)
    						brl.setTplName(tplDo4.getLicName());
    				}
    				//证照图片
    				brl.setTplPic("-");
    				if(tplDo4 != null) {
    					String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
    					if(tplDo4.getLicPic() != null)
    						brl.setTplPic(picSrvDn + tplDo4.getLicPic());
    				}
    				//经营单位
    				brl.setTplOptUnit("-");
    				if(tplDo4 != null) {
    					if(tplDo4.getJobOrganization() != null)
    						brl.setTplOptUnit(tplDo4.getJobOrganization());
    				}
    				//许可证号
    				brl.setTplNo("-");
    				if(tplDo4 != null) {
    					if(tplDo4.getLicNo() != null)
    						brl.setTplNo(tplDo4.getLicNo());
    				}
    				//发证日期
    				brl.setTplIssueDate("-");
    				if(tplDo4 != null) {
    					if(tplDo4.getGiveLicDate() != null) {
    						brl.setTplIssueDate(tplDo4.getGiveLicDate().toString());
    					}
    				}
    				//有效日期
    				brl.setTplValidDate("-");
    				if(tplDo4 != null) {
    					if(tplDo4.getLicEndDate() != null)
    						brl.setTplValidDate(tplDo4.getLicEndDate().toString());
    				}
    				//获取IOS认证证书信息
    				TProLicenseDo tplDo5 = null;
    				tplDo5 = saasService.getLicenseInfoBySupplierIdLicType(rmcId, 8);
    				//IOS认证证书
    				brl.setIosName("-");    
    				if(tplDo5 != null) {
    					if(tplDo5.getLicName() != null)
    						brl.setIosName(tplDo5.getLicName());
    				}
    				//证照图片
    				brl.setIosPic("-");
    				if(tplDo5 != null) {
    					String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
    					if(tplDo5.getLicPic() != null)
    						brl.setIosPic(picSrvDn + tplDo5.getLicPic());
    				}
    				//经营单位
    				brl.setIosOptUnit("-");
    				if(tplDo5 != null) {
    					if(tplDo5.getJobOrganization() != null)
    						brl.setIosOptUnit(tplDo5.getJobOrganization());
    				}
    				//许可证号
    				brl.setIosNo("-");
    				if(tplDo5 != null) {
    					if(tplDo5.getLicNo() != null)
    						brl.setIosNo(tplDo5.getLicNo());
    				}
    				//发证日期
    				brl.setIosIssueDate("-");
    				if(tplDo5 != null) {
    					if(tplDo5.getGiveLicDate() != null) {
    						brl.setIosIssueDate(tplDo5.getGiveLicDate().toString());
    					}
    				}
    				//有效日期
    				brl.setIosValidDate("-");    
    				if(tplDo5 != null) {
    					if(tplDo5.getLicEndDate() != null)
    						brl.setIosValidDate(tplDo5.getLicEndDate().toString());
    				}
    				//条件判断
    				boolean isAdd = true;
    				int[] flIdxs = new int[6];
    				//判断企业名称（判断索引0）
    				if(compName != null) {
    					if(brl.getCompName().indexOf(compName) == -1)
    						flIdxs[0] = -1;
    				}
    				//判断区域（判断索引1）
    				if(distIdorSCName != null) {
    					if(!distIdorSCName.equals(brl.getLocation()))
    						flIdxs[1] = -1;
    				}
    				//判断联系人（判断索引2）
    				if(contact != null) {
    					if(contact.indexOf(brl.getContact()) == -1)
    						flIdxs[2] = -1;
    				}    				
    				//判断食品经营许可证（判断索引3）
    				if(fblNo != null) {
    					if(fblNo.indexOf(brl.getFhlNo()) == -1)
    						flIdxs[3] = -1;
    				}
    				//判断统一社会信用代码（判断索引4）
    				if(uscc != null) {
    					if(uscc.indexOf(brl.getUscc()) == -1)
    						flIdxs[4] = -1;
    				}
    				//判断注册资本，单位：万元（判断索引5）
    				if(regCapital != null) {
    					if(brl.getRegCapital().indexOf(regCapital) == -1)
    						flIdxs[5] = -1;
    				}
    				//总体条件判断
    				for(i = 0; i < flIdxs.length; i++) {
    					if(flIdxs[i] == -1) {
    						isAdd = false;
    						break;
    					}
    				}
    				//是否满足条件
    				if(isAdd)
    					bdRmcList.add(brl);
    			}
    			else
    				logger.info("基础数据学校："+ curKey + "，格式错误！");
    		}
    	}
    	//排序
    	SortList<BdRmcList> sortList = new SortList<BdRmcList>();  
    	sortList.Sort(bdRmcList, "getRelPpNum", "desc", "Integer");
		//时戳
		brlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<BdRmcList> pageBean = new PageBean<BdRmcList>(bdRmcList, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		brlDto.setPageInfo(pageInfo);
		//设置数据
		brlDto.setBdRmcList(pageBean.getCurPageData());
		//消息ID
		brlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return brlDto;
	}	
	
	// 基础数据团餐公司列表函数
	BdRmcListDTO bdRmcListTwo(String distIdorSCName, List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, 
			String compName, String contact, String fblNo, String uscc, String regCapital) {
		BdRmcListDTO brlDto = new BdRmcListDTO();
		List<BdRmcList> bdRmcList = new ArrayList<>();
    	 EduSupplierDetail inputObj = new EduSupplierDetail();
    	 inputObj.setArea(distIdorSCName);
    	 inputObj.setSupplierName(compName);
    	 inputObj.setContacts(contact);
    	 inputObj.setShipinLicName(fblNo);
    	 inputObj.setSocialCreditCode(uscc);
    	 inputObj.setRegCapital(regCapital);
    	 List<EduSupplierDetail> supplierList = saasService.getSupplierList(inputObj);
    	 for(EduSupplierDetail obj : supplierList) {
    		 BdRmcList brl = new BdRmcList();
    		if(CommonUtil.isNotEmpty(obj.getSupplierName()))
			 	brl.setCompName(obj.getSupplierName());                     //企业名称
    		if(CommonUtil.isNotEmpty(obj.getArea()))
				brl.setLocation(obj.getArea());                     //所在地
    		if(CommonUtil.isNotEmpty(obj.getAddress()))
				brl.setDetailAddr(obj.getAddress());                   //详细地址
    		if(CommonUtil.isNotEmpty(obj.getEmail()))
				brl.setEmail(obj.getEmail());                        //电子邮箱
    		if(CommonUtil.isNotEmpty(obj.getContacts()))
				brl.setContact(obj.getContacts());                      //联系人
    		if(CommonUtil.isNotEmpty(obj.getContactWay()))
				brl.setMobilePhone(obj.getContactWay());                  //手机号
    		if(CommonUtil.isNotEmpty(obj.getQaPerson()))
				brl.setQaLeader(obj.getQaPerson());                     //质量负责人
    		if(CommonUtil.isNotEmpty(obj.getQaWay()))
				brl.setContactNo(obj.getQaWay());                    //联系电话
    		if(CommonUtil.isNotEmpty(obj.getCollectTotal()==null?"":obj.getCollectTotal().toString()))
				brl.setRelPpNum(obj.getCollectTotal());                        //关联项目点数量
    		if(CommonUtil.isNotEmpty(obj.getShipinLicName()))	
				brl.setLicName(obj.getShipinLicName());                      //证照名称
			String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
			if(obj.getShipinLicUrl()!=null&&!"null".equals(obj.getShipinLicUrl())&&!"".equals(obj.getShipinLicUrl())){
				brl.setLicPic((obj.getShipinLicUrl()!=null&&obj.getShipinLicUrl().indexOf("http")>=0)?obj.getShipinLicUrl():(picSrvDn+obj.getShipinLicUrl()));//证照图片
			}
			if(CommonUtil.isNotEmpty(obj.getShipinJobOrganization()))
				brl.setLicOptUnit(obj.getShipinJobOrganization());                   //经营单位
			if(CommonUtil.isNotEmpty(obj.getShipinLicNo()))
				brl.setLicNo(obj.getShipinLicNo());                        //许可证号
			if(CommonUtil.isNotEmpty(obj.getShipinGiveLicDate()==null?"":obj.getShipinGiveLicDate().toString()))
				brl.setLicIssueDate(obj.getShipinGiveLicDate()==null?"":obj.getShipinGiveLicDate().toString());                 //发证日期
			if(CommonUtil.isNotEmpty(obj.getShipinLicEndDate()==null?"":obj.getShipinLicEndDate().toString()))
				brl.setLicValidDate(obj.getShipinLicEndDate()==null?"":obj.getShipinLicEndDate().toString());                 //有效日期
			if(CommonUtil.isNotEmpty(obj.getSocialCreditCode()))
				brl.setUscc(obj.getSocialCreditCode());                         //统一社会信用代码
			
			if(obj.getYinyeLicUrl()!=null&&!"null".equals(obj.getYinyeLicUrl())){
				brl.setUsccPic((obj.getYinyeLicUrl()!=null&&obj.getYinyeLicUrl().indexOf("http")>=0)?obj.getYinyeLicUrl():(picSrvDn+obj.getYinyeLicUrl()));//证照图片
			}
			if(CommonUtil.isNotEmpty(obj.getYinyeJobOrganization()))
				brl.setUsccOptUnit(obj.getYinyeJobOrganization());                  //经营单位
			if(CommonUtil.isNotEmpty(obj.getSupplierName()))
				brl.setOptScope("");                     //经营范围
			if(CommonUtil.isNotEmpty(obj.getYinyeZuceAddress()))
				brl.setRegAddr(obj.getYinyeZuceAddress());                      //注册地址
			if(CommonUtil.isNotEmpty(obj.getYinyeAddress()))
				brl.setUdetailAddr(obj.getYinyeAddress());                  //详细地址
			if(CommonUtil.isNotEmpty(obj.getRegCapital()))
				brl.setRegCapital(obj.getRegCapital());                   //注册资本，单位：万元
			if(CommonUtil.isNotEmpty(obj.getCorporation()))
				brl.setCorpRepName(obj.getCorporation());                  //法人代表
			if(CommonUtil.isNotEmpty(obj.getYinyeOperation()))
				brl.setUcssIssueAuth(obj.getYinyeOperation());                //发证机关
			if(CommonUtil.isNotEmpty(obj.getYinyeGiveLicDate()==null?"":obj.getYinyeGiveLicDate().toString()))
				brl.setUcssIssueDate(obj.getYinyeGiveLicDate()==null?"":obj.getYinyeGiveLicDate().toString());                //发证日期
			if(CommonUtil.isNotEmpty(obj.getYinyeLicEndDate()==null?"":obj.getYinyeLicEndDate().toString()))
				brl.setUcssValidDate(obj.getYinyeLicEndDate()==null?"":obj.getYinyeLicEndDate().toString());                //有效日期
			
			if(CommonUtil.isNotEmpty(obj.getShiweiLicName()))
				brl.setFhlName(obj.getShiweiLicName());                      //食品卫生许可证
			if(obj.getShiweiLicUrl()!=null&&!"null".equals(obj.getShiweiLicUrl())){
				brl.setFhlPic((obj.getShiweiLicUrl()!=null&&obj.getShiweiLicUrl().indexOf("http")>=0)?obj.getShiweiLicUrl():(picSrvDn+obj.getShiweiLicUrl()));//证照图片
			}
			if(CommonUtil.isNotEmpty(obj.getShiweiJobOrganization()))
				brl.setFhlOptUnit(obj.getShiweiJobOrganization());                   //经营单位
			if(CommonUtil.isNotEmpty(obj.getShiweiLicNo()))
				brl.setFhlNo(obj.getShiweiLicNo());                        //许可证号
			if(CommonUtil.isNotEmpty(obj.getShiweiGiveLicDate()==null?"":obj.getShiweiGiveLicDate().toString()))
				brl.setFhlIssueDate(obj.getShiweiGiveLicDate()==null?"":obj.getShiweiGiveLicDate().toString());                 //发证日期
			if(CommonUtil.isNotEmpty(obj.getShiweiLicEndDate()==null?"":obj.getShiweiLicEndDate().toString()))
				brl.setFhlValidDate(obj.getShiweiLicEndDate()==null?"":obj.getShiweiLicEndDate().toString());                 //有效日期
				
			if(CommonUtil.isNotEmpty(obj.getYunshuLicName()))
				brl.setTplName(obj.getYunshuLicName());                      //运输许可证
			if(obj.getYunshuLicUrl()!=null&&!"null".equals(obj.getYunshuLicUrl())){
				brl.setTplPic((obj.getYunshuLicUrl()!=null&&obj.getYunshuLicUrl().indexOf("http")>=0)?obj.getYunshuLicUrl():(picSrvDn+obj.getYunshuLicUrl()));//证照图片
			}
			if(CommonUtil.isNotEmpty(obj.getYunshuJobOrganization()))
				brl.setTplOptUnit(obj.getYunshuJobOrganization());                   //经营单位
			if(CommonUtil.isNotEmpty(obj.getYunshuLicNo()))
				brl.setTplNo(obj.getYunshuLicNo());                        //许可证号
			if(CommonUtil.isNotEmpty(obj.getYunshuGiveLicDate()==null?"":obj.getYunshuGiveLicDate().toString()))
				brl.setTplIssueDate(obj.getYunshuGiveLicDate()==null?"":obj.getYunshuGiveLicDate().toString());                 //发证日期
			if(CommonUtil.isNotEmpty(obj.getYunshuLicEndDate()==null?"":obj.getYunshuLicEndDate().toString()))
				brl.setTplValidDate(obj.getYunshuLicEndDate()==null?"":obj.getYunshuLicEndDate().toString());                 //有效日期
			
			if(CommonUtil.isNotEmpty(obj.getIosLicName()))
				brl.setIosName(obj.getIosLicName());                      //IOS认证证书
			if(obj.getIosLicUrl()!=null&&!"null".equals(obj.getIosLicUrl())){
				brl.setIosPic(obj.getIosLicUrl().indexOf("http")>=0?obj.getIosLicUrl():(picSrvDn+obj.getIosLicUrl()));//证照图片
			}
			if(CommonUtil.isNotEmpty(obj.getIosJobOrganization()))
				brl.setIosOptUnit(obj.getIosJobOrganization());                   //经营单位
			if(CommonUtil.isNotEmpty(obj.getIosLicNo()))
				brl.setIosNo(obj.getIosLicNo());                        //许可证号
			if(CommonUtil.isNotEmpty(obj.getIosGiveLicDate()==null?"":obj.getIosGiveLicDate().toString()))
				brl.setIosIssueDate(obj.getIosGiveLicDate()==null?"":obj.getIosGiveLicDate().toString());                 //发证日期
			if(CommonUtil.isNotEmpty(obj.getIosLicEndDate()==null?"":obj.getIosLicEndDate().toString()))
				brl.setIosValidDate(obj.getIosLicEndDate()==null?"":obj.getIosLicEndDate().toString());                 //有效日期
			
			bdRmcList.add(brl);
    	 }
    	
    	//排序
    	SortList<BdRmcList> sortList = new SortList<BdRmcList>();  
    	sortList.Sort(bdRmcList, "getRelPpNum", "desc", "Integer");
		//时戳
		brlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<BdRmcList> pageBean = new PageBean<BdRmcList>(bdRmcList, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		brlDto.setPageInfo(pageInfo);
		//设置数据
		brlDto.setBdRmcList(pageBean.getCurPageData());
		//消息ID
		brlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return brlDto;
	}	
	
	// 基础数据团餐公司列表模型函数
	public BdRmcListDTO appModFunc(String token, String compName, String distName, String prefCity, String province, String contact, String fblNo, String uscc, String regCapital, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		BdRmcListDTO brlDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) {    // 按区域，省或直辖市处理
				List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
				// 查找是否存在该区域和省市
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					if (curTdd.getId().compareTo(distName) == 0) {
						bfind = true;
						distIdorSCName = curTdd.getId();
						break;
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据团餐公司列表函数
					brlDto = bdRmcListTwo(distIdorSCName, tddList, db1Service, saasService, compName, contact, fblNo, uscc, regCapital);		
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tddList = null;
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					tddList = db1Service.getListByDs1IdName();
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据团餐公司列表函数
					brlDto = bdRmcListTwo(distIdorSCName, tddList, db1Service, saasService, compName, contact, fblNo, uscc, regCapital);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}														
		}
		else {    //模拟数据
			//模拟数据函数
			brlDto = SimuDataFunc();
			logger.info("输出模拟数据！");
		}		

		return brlDto;
	}
}
