package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidAdminVideos;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidAdminVideosDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//教育培训视频管理视频列表应用模型
public class EtVidAdminVideosAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidAdminVideosAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化
	String[] vidId_Array = {"319d4ab7-5f60-4e99-9f32-495f024a0286", "449d4ab7-5f60-4e99-9f32-495f024a0286"};
	String[] uploadDate_Array = {"2018-08-20", "2018-08-20"};
	String[] vidName_Array = {"团餐公司操作演示视频04", "团餐公司操作演示视频03"};
	int[] vidCategory_Array = {1, 1};
	String[] pubPerson_Array = {"wang", "wang"};
	String[] auditPerson_Array = {"shsjw", "shsjw"};
	String[] auditDate_Array = {"2018-08-21", "2018-08-21"};
	int[] vidStatus_Array = {0, 1};
	
	//模拟数据函数
	private EtVidAdminVideosDTO SimuDataFunc(String vidStatus) {
		EtVidAdminVideosDTO evavDto = new EtVidAdminVideosDTO();
		//时戳
		evavDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//教育培训视频管理视频列表模拟数据
		List<EtVidAdminVideos> etVidLib = new ArrayList<>();
		//赋值
		for (int i = 0; i < vidId_Array.length; i++) {
			EtVidAdminVideos evav = new EtVidAdminVideos();
			evav.setVidId(vidId_Array[i]);
			evav.setUploadDate(uploadDate_Array[i]);
			evav.setVidName(vidName_Array[i]);
			evav.setVidCategory(vidCategory_Array[i]);
			evav.setPubPerson(pubPerson_Array[i]);
			evav.setAuditPerson(auditPerson_Array[i]);
			evav.setAuditDate(auditDate_Array[i]);
			evav.setVidStatus(vidStatus_Array[i]);
			if(vidStatus == null)
				etVidLib.add(evav);
			else {
				int curVidStatus = Integer.parseInt(vidStatus);
				if(curVidStatus == vidStatus_Array[i])
					etVidLib.add(evav);
			}
		}
		//设置数据
		evavDto.setEtVidAdminVideos(etVidLib);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = vidName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		evavDto.setPageInfo(pageInfo);
		//消息ID
		evavDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return evavDto;
	}
	
	// 教育培训视频管理视频列表函数
	private EtVidAdminVideosDTO etVidAdminVideos(String token, String startDate, String endDate, String vidName, int dateType, int vidCategory, int vidStatus, Db1Service db1Service, Db2Service db2Service) {
		EtVidAdminVideosDTO evavDto = null;		
		List<TEduBdEtvidLibDo> tebelDoList = null;
		int k, i;
		if(startDate != null && endDate != null) {
			//获取教育视频以开始和结束时间
			startDate += " 00:00:00";
			endDate += " 23:59:59";
			tebelDoList = db2Service.getTEduBdEtvidLibDosByCreateTime(startDate, endDate);
		}
		else {
			//获取所有教育视频
			tebelDoList = db2Service.getAllTEduBdEtvidLibDos();
		}
		if(tebelDoList != null) {
			evavDto = new EtVidAdminVideosDTO();
			// 时戳
			evavDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			List<EtVidAdminVideos> etVidAdminVideos = new ArrayList<>();
			for(k = 0; k < tebelDoList.size(); k++) {
				EtVidAdminVideos evav = new EtVidAdminVideos();
				evav.setVidId(tebelDoList.get(k).getId());                                                         //视频ID，视频记录唯一标识
				String[] createTimes = tebelDoList.get(k).getCreateTime().split(" ");
				evav.setUploadDate(createTimes[0]);                                                                //上传时间，格式：xxxx-xx-xx
				evav.setVidName(tebelDoList.get(k).getVidName());                                                  //视频名称 
				evav.setVidCategory(tebelDoList.get(k).getVidCategory());                                          //视频分类，0:系统操作，1:食品安全，2:政策法规
				evav.setPubPerson(tebelDoList.get(k).getUserName());                                               //发布人
				evav.setAuditPerson(null);                                                                         //审核人
				evav.setAuditDate(null);                                                                           //审核日期
				if(tebelDoList.get(k).getAuditStatus() != null)
					evav.setVidStatus(tebelDoList.get(k).getAuditStatus());                                        //视频状态，0:待审核，1:已审核，2:已下架，3:已驳回
				else
					evav.setVidStatus(0);
				//条件判断
				boolean isAdd = true;
				int[] flIdxs = new int[3];
				//判断视频名称（判断索引0）
				if(vidName != null) {
					if(evav.getVidName().indexOf(vidName) == -1)
						flIdxs[0] = -1;
				}
				//判断视频分类（判断索引1）
				if(vidCategory != -1) {
					if(evav.getVidCategory() != vidCategory)
						flIdxs[1] = -1;
				}
				//判断视频状态（判断索引2）
				if(vidStatus != -1) {
					if(evav.getVidStatus() != vidStatus)
						flIdxs[2] = -1;
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
					etVidAdminVideos.add(evav);		
			}
			//排序
	    	SortList<EtVidAdminVideos> sortList = new SortList<EtVidAdminVideos>();  
	    	sortList.Sort(etVidAdminVideos, "getUploadDate", "desc");			
			// 分页
			PageBean<EtVidAdminVideos> pageBean = new PageBean<EtVidAdminVideos>(etVidAdminVideos, curPageNum, pageSize);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageTotal(pageBean.getTotalCount());
			pageInfo.setCurPageNum(curPageNum);
			evavDto.setPageInfo(pageInfo);
			// 设置数据
			evavDto.setEtVidAdminVideos(pageBean.getCurPageData());
			// 消息ID
			evavDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}		
		
		return evavDto;
	}
	
	// 教育培训视频管理视频列表模型函数
	public EtVidAdminVideosDTO appModFunc(String token, String dateType, String startDate, String endDate, String vidName, String vidCategory, String vidStatus, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		EtVidAdminVideosDTO evavDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//日期类型，0:上传日期，1:审核日期
			int curDateType = -1;
			if(dateType != null && !dateType.isEmpty())
				curDateType = Integer.parseInt(dateType);
			//视频分类，0:系统操作，1:食品安全，2:政策法规
			int curVidCategory = -1;
			if(vidCategory != null && !vidCategory.isEmpty())
				curVidCategory = Integer.parseInt(vidCategory);
			//视频状态，0:待审核，1:已审核，2:已下架，3:已驳回
			int curVidStatus = -1;
			if(vidStatus != null && !vidStatus.isEmpty())
				curVidStatus = Integer.parseInt(vidStatus);
			// 判断参数形式
			if (token != null && db2Service != null) {    
				// 教育培训视频管理视频列表函数
				evavDto = etVidAdminVideos(token, startDate, endDate, vidName, curDateType, curVidCategory, curVidStatus, db1Service, db2Service);
			}
			else {
				logger.info("访问接口参数非法！");
			}						
		}
		else {    //模拟数据
			//模拟数据函数
			evavDto = SimuDataFunc(vidStatus);
		}		

		return evavDto;
	}
}
