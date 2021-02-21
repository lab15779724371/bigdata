package com.tfit.BdBiProcSrvShEduOmc.appmod.cp;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrDetList;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrDetListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//投诉举报详情列表应用模型
public class CrDetListAppMod {
	private static final Logger logger = LogManager.getLogger(CrDetListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String [] crId_Array = {"12703e54-35cc-4db0-84a3-ca5e7c7d5ac7", "33803e54-35cc-4db0-84a3-ca5e7c7d5ac7", "44903e54-35cc-4db0-84a3-ca5e7c7d5ac7", "55a03e54-35cc-4db0-84a3-ca5e7c7d5ac7"};
	String [] subDate_Array = {"2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03"};
	String [] distName_Array = {"徐汇区", "徐汇区", "徐汇区", "徐汇区"};
	String [] title_Array = {"上海市**中学食堂卫生条件差", "上海市**小学食堂卫生条件差", "上海市**中学食堂卫生条件差", "上海市**小学食堂卫生条件差"};
	String [] schName_Array = {"上海市**中学", "上海市徐汇区**小学", "上海市**中学", "上海市徐汇区**小学"};
	String [] complainant_Array = {"匿名", "匿名", "匿名", "匿名"};
	String [] contractor_Array = {"", "", "徐汇区教育局", "徐汇区教育局"};
	int[] procStatus_Array = {0, 0, 1, 2};
	String [] handleDate_Array = {"", "", "", "2018-09-05"};
	
	//模拟数据函数
	private CrDetListDTO SimuDataFunc() {
		CrDetListDTO cdlDto = new CrDetListDTO();
		//时戳
		cdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//投诉举报详情列表模拟数据
		List<CrDetList> crDetList = new ArrayList<>();
		//赋值
		for (int i = 0; i < crId_Array.length; i++) {
			CrDetList cdl = new CrDetList();
			cdl.setCrId(crId_Array[i]);
			cdl.setSubDate(subDate_Array[i]);
			cdl.setDistName(distName_Array[i]);
			cdl.setTitle(title_Array[i]);
			cdl.setSchName(schName_Array[i]);
			cdl.setComplainant(complainant_Array[i]);
			cdl.setContractor(contractor_Array[i]);
			cdl.setProcStatus(procStatus_Array[i]);
			cdl.setHandleDate(handleDate_Array[i]);
			crDetList.add(cdl);
		}
		//设置数据
		cdlDto.setCrDetList(crDetList);
		//分页信息
		PageInfo pageInfo = new PageInfo();
		pageTotal = crId_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cdlDto.setPageInfo(pageInfo);
		//消息ID
		cdlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cdlDto;
	}
	
	// 投诉举报详情列表函数
	private CrDetListDTO crDetList(String distIdorSCName, String[] dates, String schName, String contractor, int procStatus, Db1Service db1Service, Db2Service db2Service) {
		CrDetListDTO cdlDto = new CrDetListDTO();
		//时戳
		cdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//投诉举报详情列表
		List<CrDetList> crDetList = new ArrayList<>();
		//获取投诉举报以日期段，日期格式：xxxx-xx-xx
	    List<TEduBdComplaintDo> tebcpDoList = db2Service.getTEduBdComplaintDosBySubDate(dates[0], dates[0]);
		//赋值
		if(tebcpDoList != null) {
			for (int i = 0; i < tebcpDoList.size(); i++) {
				TEduBdComplaintDo tebcp = tebcpDoList.get(i);
				if(tebcp.getSubDate() != null) {
					CrDetList cdl = new CrDetList();
					cdl.setCrId(tebcp.getId());                //举报投诉ID
					cdl.setSubDate(tebcp.getSubDate());        //提交日期，格式：xxxx-xx-xx
					//从数据源ds1的数据表t_edu_school中查找学校信息以学校id
  	  			    TEduSchoolDo tesDo = db1Service.getTEduSchoolDoBySchId(tebcp.getSchoolId(), 3);
  	  			    cdl.setDistName("-");        //区域名称
  	  			    cdl.setSchName("-");         //学校名称
  	  			    if(tesDo != null) {
  	  			    	if(tesDo.getArea() != null)
  	  			    		cdl.setDistName(AppModConfig.distIdToNameMap.get(tesDo.getArea()));        //区域名称
  	  			    	cdl.setSchName(tesDo.getSchoolName());
  	  			    }
					cdl.setTitle(tebcp.getTitle());                  //投诉举报主题					
					cdl.setComplainant(tebcp.getCptName());          //投诉人名称
					cdl.setContractor(tebcp.getContractor());        //承办人名称
					cdl.setProcStatus(tebcp.getCpStatus());          //处理状态，0:待处理，1:已指派，2:已办结
					cdl.setHandleDate(tebcp.getFinishDate());        //办结日期，格式：xxxx-xx-xx
					//区域筛选
					if(distIdorSCName != null && !distIdorSCName.isEmpty()) {
						if(!cdl.getDistName().equalsIgnoreCase(AppModConfig.distIdToNameMap.get(distIdorSCName)))
							continue ;
					}
					//条件判断
					boolean isAdd = true;
					int[] flIdxs = new int[3];
					//判断学校名称（判断索引0）
					if(schName != null && !schName.isEmpty()) {
						if(cdl.getSchName().indexOf(schName) == -1)
							flIdxs[0] = -1;
					}
					//判断承办人名称（判断索引1）
					if(contractor != null && !contractor.isEmpty()) {
						if(cdl.getContractor().equals(contractor))
							flIdxs[1] = -1;
					}
					//判断处理状态（判断索引2）
					if(procStatus != -1) {
						if(cdl.getProcStatus() != procStatus)
							flIdxs[2] = -1;
					}
					//总体条件判断
					for(int j = 0; j < flIdxs.length; j++) {
						if(flIdxs[j] == -1) {
							isAdd = false;
							break;
						}
					}
					//是否满足条件
					if(isAdd)
						crDetList.add(cdl);
				}
			}
		}
		//设置数据
		cdlDto.setCrDetList(crDetList);
		// 分页
    	PageBean<CrDetList> pageBean = new PageBean<CrDetList>(crDetList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	cdlDto.setPageInfo(pageInfo);
		//消息ID
		cdlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return cdlDto;
	}
	
	// 投诉举报详情列表模型函数
	public CrDetListDTO appModFunc(String token, String subDate, String title, String distName, String prefCity, String province, String schName, String contractor, String procStatus, Db1Service db1Service, Db2Service db2Service) {
		CrDetListDTO cdlDto = null;
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (subDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			}
			else {
				dates = new String[1];
				dates[0] = subDate;
			}
			for (int i = 0; i < dates.length; i++) {
				logger.info("dates[" + i + "] = " + dates[i]);
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";
			//处理状态，0:待处理，1:已指派，2:已办结
			int curProcStatus = -1;
			if(procStatus != null && !procStatus.isEmpty())
				curProcStatus = Integer.parseInt(procStatus);
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				List<TEduDistrictDo> tedList = db1Service.getListByDs1IdName();
				if(tedList != null) {
					// 查找是否存在该区域和省市
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						if (curTdd.getId().compareTo(distName) == 0) {
							bfind = true;
							distIdorSCName = curTdd.getId();
							break;
						}
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 投诉举报详情列表函数
					cdlDto = crDetList(distIdorSCName, dates, schName, contractor, curProcStatus, db1Service, db2Service);
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tedList = null;
				if (province.compareTo("上海市") == 0) {
					tedList = db1Service.getListByDs1IdName();
					if(tedList != null)
						bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 投诉举报详情列表函数
					cdlDto = crDetList(distIdorSCName, dates, schName, contractor, curProcStatus, db1Service, db2Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			cdlDto = SimuDataFunc();
		}		

		return cdlDto;
	}
}
