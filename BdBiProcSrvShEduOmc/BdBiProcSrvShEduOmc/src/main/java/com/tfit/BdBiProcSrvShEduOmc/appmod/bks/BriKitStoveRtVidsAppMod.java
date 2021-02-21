package com.tfit.BdBiProcSrvShEduOmc.appmod.bks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdBriKitStoveDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStoveRtVids;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStoveRtVidsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//3.7.1 - 明厨亮灶实时视频列表应用模型
public class BriKitStoveRtVidsAppMod {
	private static final Logger logger = LogManager.getLogger(BriKitStoveRtVidsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化
	String[] ppName_Array = {"上海市天山中学（食堂）", "上海市天山中学（食堂）", "上海市紫薇幼儿园（食堂）", "上海市紫薇幼儿园（食堂）", "上师大附属小学（食堂）", "上师大附属小学（食堂）"};
	String[] camId_Array = {"1", "2", "3", "4", "5", "6"};
	String[] camIp_Array = {"10.1.21.3", "10.1.21.4", "10.1.21.5", "10.1.21.6", "10.1.21.7", "10.1.21.8"};
	String[] vidSrcName_Array = {"上海市天山中学（食堂）-厨房 01", "上海市天山中学（食堂）-厨房 02", "上海市紫薇幼儿园（食堂）-厨房 01", "上海市紫薇幼儿园（食堂）-厨房 02", "上师大附属小学（食堂）-厨房 01", "上师大附属小学（食堂）-厨房 02"};
	String[] vidUrl_Array = {"/briKitStoveRtVids/1.mp4", "/briKitStoveRtVids/2.mp4", "/briKitStoveRtVids/3.mp4", "/briKitStoveRtVids/4.mp4", "/briKitStoveRtVids/5.mp4", "/briKitStoveRtVids/6.mp4"};

	//模拟数据函数
	private BriKitStoveRtVidsDTO SimuDataFunc() {
		BriKitStoveRtVidsDTO bksrvDto = new BriKitStoveRtVidsDTO();
		//时戳
		bksrvDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//明厨亮灶实时视频列表模拟数据
		List<BriKitStoveRtVids> briKitStoveRtVids = new ArrayList<>();
		//赋值
		for (int i = 0; i < ppName_Array.length; i++) {
			BriKitStoveRtVids bksrv = new BriKitStoveRtVids();
			bksrv.setPpName(ppName_Array[i]);
			bksrv.setCamId(camId_Array[i]);
			bksrv.setCamIp(camIp_Array[i]);
			bksrv.setVidSrcName(vidSrcName_Array[i]);
			bksrv.setVidRtspUrl(SpringConfig.video_srvdn + vidUrl_Array[i]);
			bksrv.setVidRtmpUrl(SpringConfig.video_srvdn + vidUrl_Array[i]);
			briKitStoveRtVids.add(bksrv);
		}
		//设置数据
		bksrvDto.setBriKitStoveRtVids(briKitStoveRtVids);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = ppName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bksrvDto.setPageInfo(pageInfo);
		//消息ID
		bksrvDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bksrvDto;
	}
	
	// 明厨亮灶实时视频列表函数
	private BriKitStoveRtVidsDTO briKitStoveRtVids(String distIdorSCName, String token, String ppName, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		BriKitStoveRtVidsDTO bksrvDto = new BriKitStoveRtVidsDTO();
		//时戳
		bksrvDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//明厨亮灶实时视频列表
		List<BriKitStoveRtVids> briKitStoveRtVids = new ArrayList<>();
		List<TEduBdBriKitStoveDo> tebbksDoList = null;
		if(distIdorSCName == null)
			tebbksDoList = db2Service.getAllSchVidSurvInfos();
		else
			tebbksDoList = db2Service.getSchVidSurvInfosByDistId(distIdorSCName);
		if(tebbksDoList != null) {
			Map<String, String> schIdToSurvVidMap = new HashMap<>();
			String keyVal = null;
			String[] ppNames = null;
			//学校id映射学校视频监控记录信息
			for(int i = 0; i < tebbksDoList.size(); i++) {
				TEduBdBriKitStoveDo tebbks = tebbksDoList.get(i);
				if(schIdToSurvVidMap.containsKey(tebbks.getSchoolId())) {
					keyVal = schIdToSurvVidMap.get(tebbks.getSchoolId());
					keyVal = keyVal + "," + String.valueOf(i);
				}
				else {
					keyVal = String.valueOf(i);					
				}
				schIdToSurvVidMap.put(tebbks.getSchoolId(), keyVal);
			}
			//输入学校ID	
			if(ppName != null) {
				ppNames = ppName.split(",");
			}
			//赋值
			for (String key : schIdToSurvVidMap.keySet()) {		
				//查询学校视频监控记录信息
				boolean bFindSch = false;
				if(ppNames != null) {
					for(int i = 0; i < ppNames.length; i++) {
						if(ppNames[i].equals(key)) {
							bFindSch = true;
							break;
						}
					}
				}
				else {
					bFindSch = true;
				}
				//查找到学校视频监控记录
				if(bFindSch) {
					keyVal = schIdToSurvVidMap.get(key);
					String[] keyVals = keyVal.split(",");
					for(int i = 0; i < keyVals.length; i++) {
						int j = Integer.parseInt(keyVals[i]);
						TEduBdBriKitStoveDo tebbksDo = tebbksDoList.get(j);
						BriKitStoveRtVids bksrv = new BriKitStoveRtVids();
						//视频ID
						bksrv.setVidId((int)(tebbksDo.getId().longValue()));
						//学校名称
						bksrv.setPpName(tebbksDo.getSchoolName());
						//摄像头id
						bksrv.setCamId(tebbksDo.getCameraId());
						//摄像头rtmp视频网络IP
						if(tebbksDo.getCamRtmpOuterNetIp() != null)
							bksrv.setCamIp(tebbksDo.getCamRtmpOuterNetIp());
						else
							bksrv.setCamIp(tebbksDo.getCamRtmpInnerNetIp());
						//视频源名称
						bksrv.setVidSrcName(tebbksDo.getVidSrcName());
						//rtsp视频URL
						if(tebbksDo.getVidOuterNetRtspUrl() != null)
							bksrv.setVidRtspUrl(tebbksDo.getVidOuterNetRtspUrl());
						else
							bksrv.setVidRtspUrl(tebbksDo.getVidInnerNetRtspUrl());
						briKitStoveRtVids.add(bksrv);
						//rtmp视频URL
						if(tebbksDo.getVidOuterNetRtmpUrl() != null)
							bksrv.setVidRtmpUrl(tebbksDo.getVidOuterNetRtmpUrl());
						else
							bksrv.setVidRtmpUrl(tebbksDo.getVidInnerNetRtmpUrl());
					}
				}
			}
		}
		// 分页
    	PageBean<BriKitStoveRtVids> pageBean = new PageBean<BriKitStoveRtVids>(briKitStoveRtVids, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	bksrvDto.setPageInfo(pageInfo);
    	// 设置数据
    	bksrvDto.setBriKitStoveRtVids(pageBean.getCurPageData());
		//消息ID
		bksrvDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();		

		return bksrvDto;
	}	
	
	// 明厨亮灶实时视频列表模型函数
	public BriKitStoveRtVidsDTO appModFunc(String token, String ppName, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		BriKitStoveRtVidsDTO bksrvDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			if(token != null && db1Service != null && db2Service != null) {
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
						// 明厨亮灶实时视频列表函数
						bksrvDto = briKitStoveRtVids(distIdorSCName, token, ppName, db1Service, db2Service, codes);
					}
				} 
				else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
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
						// 明厨亮灶实时视频列表函数
						bksrvDto = briKitStoveRtVids(distIdorSCName, token, ppName, db1Service, db2Service, codes);
					}
				} 
				else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理
					
				} 
				else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理
					
				} 
				else {
					logger.info("访问接口参数非法！");
				}		
			}
		}
		else {    //模拟数据
			//模拟数据函数
			bksrvDto = SimuDataFunc();
		}		

		return bksrvDto;
	}
}
