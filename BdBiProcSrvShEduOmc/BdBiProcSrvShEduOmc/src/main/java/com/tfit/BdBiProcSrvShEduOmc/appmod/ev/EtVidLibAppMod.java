package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLib;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//教育培训视频库应用模型
public class EtVidLibAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidLibAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化
	String[] vidId_Array = {"294dd56a-95c1-44ea-bc6a-7f6f3041c29a", "a36a0eb6-ac8a-4f05-b2bb-1723875af0e0", "82eb9015-2932-4e14-b6ac-935725badf6d", "b386549f-a86d-453c-8e69-ccad3f581c45", "eeadb734-61b8-4818-8a6b-cc78a04673af", "29f9e757-330e-4cf3-b879-6e0da1a5c74c", "c497549f-a86d-453c-8e69-ccad3f581c45", "e58f7a33-2fed-49e7-ade4-450ff8906fd1", "b98cb275-cae0-4323-9a9c-f1c9563b67f0", "0f6ef492-52ca-4d40-9434-8e0d7b2d3c07"};
	String[] vidName_Array = {"平台前端操作演示视频01", "平台前端操作演示视频02", "平台前端操作演示视频03", "平台前端操作演示视频04", "平台前端操作演示视频05", "平台前端操作演示视频06", "平台前端操作演示视频07", "平台前端操作演示视频08", "平台前端操作演示视频09", "平台前端操作演示视频10"};
	int[] vidCategory_Array = {1, 1, 2, 2, 3, 3, 1, 1, 2, 2};
	int[] playCount_Array = {5885, 4885, 3001, 2889, 1993, 1893, 993, 893, 793, 693};
	String[] uploadTime_Array = {"2018-09-06 09:51:35", "2018-09-06 10:51:35", "2018-09-06 11:51:35", "2018-09-06 12:51:35", "2018-09-06 13:51:35", "2018-09-06 14:51:35", "2018-09-07 17:51:35", "2018-09-07 18:51:35", "2018-09-07 19:00:35", "2018-09-07 19:21:35"};
	int[] likeCount_Array = {1000, 800, 600, 400, 200, 100, 200, 100, 200, 100};
	String[] vidUrl_Array = {"/etVidLib/1.mp4", "/etVidLib/2.mp4", "/etVidLib/3.mp4", "/etVidLib/4.mp4", "/etVidLib/5.mp4", "/etVidLib/6.mp4", "/etVidLib/7.mp4", "/etVidLib/8.mp4", "/etVidLib/9.mp4", "/etVidLib/10.mp4"};

	//模拟数据函数
	private EtVidLibDTO SimuDataFunc() {
		EtVidLibDTO evlDto = new EtVidLibDTO();
		//时戳
		evlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//教育培训视频库模拟数据
		List<EtVidLib> etVidLib = new ArrayList<>();
		//赋值
		for (int i = 0; i < vidId_Array.length; i++) {
			EtVidLib evl = new EtVidLib();
			evl.setVidId(vidId_Array[i]);
			evl.setVidName(vidName_Array[i]);
			evl.setVidCategory(vidCategory_Array[i]);
			evl.setPlayCount(playCount_Array[i]);
			evl.setUploadTime(uploadTime_Array[i]);
			evl.setLikeCount(likeCount_Array[i]);
			evl.setVidUrl(SpringConfig.video_srvdn + vidUrl_Array[i]);
			etVidLib.add(evl);
		}
		//设置数据
		evlDto.setEtVidLib(etVidLib);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = vidName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		evlDto.setPageInfo(pageInfo);
		//消息ID
		evlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return evlDto;
	}
	
	// 用户管理函数
	private EtVidLibDTO etVidLib(String token, String vidName, int vidCategory, int sortType, Db2Service db2Service) {
		EtVidLibDTO evlDto = null;
		//获取所有教育视频
	    List<TEduBdEtvidLibDo> tebelDoList = db2Service.getAllTEduBdEtvidLibDos();
		int k, i;
		if(tebelDoList != null) {
			evlDto = new EtVidLibDTO();
			List<EtVidLib> etVidLib = new ArrayList<>();
			for(k = 0; k < tebelDoList.size(); k++) {
				EtVidLib evl = new EtVidLib();
				TEduBdEtvidLibDo tebelDo = tebelDoList.get(k);				
				evl.setVidId(tebelDo.getId());                         //视频ID，视频记录唯一标识
				evl.setVidName(tebelDo.getVidName());                  //视频名称，模糊查询
				evl.setSubTitle(tebelDo.getSubTitle());                //副标题
				evl.setVidCategory(tebelDo.getVidCategory());          //视频分类，默认为0，0:全部，1:系统操作，2:食品安全，3:政策法规
				evl.setPlayCount(0);                                   //播放次数
				evl.setUploadTime(tebelDo.getCreateTime());            //上传时间，格式：xxxx-xx-xx xx:xx:xx
				evl.setLikeCount(0);                                   //点赞数
				evl.setVidThumbUrl(SpringConfig.video_srvdn + tebelDo.getThumbUrl());             //视频缩略图URL
				evl.setVidUrl(SpringConfig.video_srvdn + tebelDo.getVidUrl());                    //视频URL
				//条件判断
				boolean isAdd = true;
				int[] flIdxs = new int[3];
				//判断用户名（判断索引0）
				if(vidName != null) {
					if(evl.getVidName().indexOf(vidName) == -1)
						flIdxs[0] = -1;
				}
				//判断姓名（判断索引1）
				if(vidCategory != -1) {
					if(evl.getVidCategory() != vidCategory)
						flIdxs[1] = -1;
				}
				//判断角色（判断索引2）
				if(sortType != -1) {
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
					etVidLib.add(evl);			
			}
			// 设置返回数据
			evlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 分页
			PageBean<EtVidLib> pageBean = new PageBean<EtVidLib>(etVidLib, curPageNum, pageSize);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageTotal(pageBean.getTotalCount());
			pageInfo.setCurPageNum(curPageNum);
			evlDto.setPageInfo(pageInfo);
			// 设置数据
			evlDto.setEtVidLib(pageBean.getCurPageData());
			// 消息ID
			evlDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		
		return evlDto;
	}
	
	// 教育培训视频库模型函数
	public EtVidLibDTO appModFunc(String token, String vidName, String vidCategory, String sortType, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		EtVidLibDTO evlDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//视频分类，1:系统操作，2:食品安全，3:政策法规
			int curVidCategory = -1;
			if(vidCategory != null)
				curVidCategory = Integer.parseInt(vidCategory);
			//排序类型，默认为0，0:按播放次数降序，1:按上传时间降序，2:按好评率降序
			int curSortType = -1;
			if(sortType != null)
				curSortType = Integer.parseInt(sortType);
			// 判断参数形式
			if (token != null && db2Service != null) {    
				// 用户管理函数
				evlDto = etVidLib(token, vidName, curVidCategory, curSortType, db2Service);
			}
			else {
				logger.info("访问接口参数非法！");
			}						
		}
		else {    //模拟数据
			//模拟数据函数
			evlDto = SimuDataFunc();
		}		

		return evlDto;
	}
}
