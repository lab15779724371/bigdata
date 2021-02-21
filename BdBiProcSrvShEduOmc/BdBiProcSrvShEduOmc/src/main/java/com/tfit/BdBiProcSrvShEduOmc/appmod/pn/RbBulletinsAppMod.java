package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMsgNoticeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletins;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletinsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//通知列表应用模型
public class RbBulletinsAppMod {
	private static final Logger logger = LogManager.getLogger(RbBulletinsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;	
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] bulletinId_Array = {"256510ab-a428-46b4-b3a7-6388191e1ffc20180321", "366510ab-a428-46b4-b3a7-6388191e1ffc20180321"};
	String[] pubOrg_Array = {"上海市教委", "上海市教委"};
	String[] publisher_Array = {"admin", "admin"};
	String[] title_Array = {"关于天山中学违规处罚通知", "关于天山中学违规使用添加剂的处罚通知"};
	String[] rcvOrgName_Array = {null, null};
	String[] pubDate_Array = {"2018-08-20", "2018-08-22"};
	int[] amFlag_Array = {0, 1};
	int[] readFlag_Array = {0, 1};

	//模拟数据函数
	private RbBulletinsDTO SimuDataFunc() {
		RbBulletinsDTO rbbDto = new RbBulletinsDTO();
		//时戳
		rbbDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//通知列表模拟数据
		List<RbBulletins> rbBulletins = new ArrayList<>();
		//赋值
		for (int i = 0; i < title_Array.length; i++) {
			RbBulletins rbb = new RbBulletins();
			rbb.setBulletinId(bulletinId_Array[i]);			
			rbb.setPubOrg(pubOrg_Array[i]);
			rbb.setPublisher(publisher_Array[i]);
			rbb.setTitle(title_Array[i]);
			rbb.setRcvOrgName(rcvOrgName_Array[i]);
			rbb.setPubDate(pubDate_Array[i]);
			rbb.setAmFlag(amFlag_Array[i]);
			rbb.setReadFlag(readFlag_Array[i]);
			rbBulletins.add(rbb);
		}
		//设置数据
		rbbDto.setRbBulletins(rbBulletins);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = bulletinId_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		rbbDto.setPageInfo(pageInfo);
		//消息ID
		rbbDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rbbDto;
	}
	
	// 通知列表模型函数
	public RbBulletinsDTO rbBulletins(String token, String userName, String startPubDate, String endPubDate, String title, int readFlag, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		int i, j;
		RbBulletinsDTO rbbDto = new RbBulletinsDTO();
		List<RbBulletins> rbBulletins = new ArrayList<>();
		//从数据源ds2的数据表t_edu_bd_user中查找用户信息以授权码token
	    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
		if(userName == null) {   //消息通知
			//查询消息通知状态记录列表以接收用户名
			List<TEduBdNoticeStatusDo> tebnsDoList = db2Service.getMsgNoticeStatusByRcvUserName(tebuDo.getUserAccount());
			if(tebnsDoList != null) {
				for(i = 0; i < tebnsDoList.size(); i++) {
					TEduBdNoticeStatusDo tebnsDo = tebnsDoList.get(i);
					//查询消息通知记录以通知id
				    TEduBdMsgNoticeDo tebmnDo = db2Service.getMsgNoticeById(tebnsDo.getBulletinId());
				    if(tebmnDo != null) {
				    	if(!tebnsDo.getOwnerUserName().equals(tebnsDo.getRcvUserName())) {
				    		RbBulletins rbt = new RbBulletins();
				    		//通知id
				    		rbt.setBulletinId(tebmnDo.getId());
				    		//通知类型
				    		rbt.setAnnounceType(0);
				    		if(tebmnDo.getAnnounceType() != null) {
				    			rbt.setAnnounceType(tebmnDo.getAnnounceType());
				    		}
				    		//发布单位					    
				    		TEduBdUserDo tmpTebuDo =  db2Service.getBdUserInfoByUserName(tebnsDo.getOwnerUserName());  //从数据源ds2的数据表t_edu_bd_user中查找用户信息
				    		if(tmpTebuDo != null)
				    			rbt.setPubOrg(tmpTebuDo.getOrgName());
				    		//发布人
				    		rbt.setPublisher(tebnsDo.getOwnerUserName());
				    		//通知主题
				    		rbt.setTitle(tebmnDo.getTitle());
				    		//发布日期
				    		if(tebmnDo.getCreateTime() != null) {
				    			rbt.setPubDate(tebmnDo.getCreateTime());
				    		}
				    		//是否有附件
				    		if(tebmnDo.getAmFlag() != null)
				    			rbt.setAmFlag(tebmnDo.getAmFlag());
				    		else
				    			rbt.setAmFlag(0);
				    		//是否阅读
				    		rbt.setReadFlag(0);
				    		if(tebnsDo.getReadCount() != null) {
				    			if(tebnsDo.getReadCount() > 0)
				    				rbt.setReadFlag(1);
				    		}
				    		//条件判断
							boolean isAdd = true;
							int[] flIdxs = new int[2];
							//判断标题名称（判断索引0）
							if(title != null) {
								if(rbt.getTitle().indexOf(title) == -1)
									flIdxs[0] = -1;
							}
							//判断阅读标记（判断索引1）
							if(readFlag != -1) {
								if(readFlag != rbt.getReadFlag())
									flIdxs[1] = -1;
							}
							//总体条件判断
							for(j = 0; j < flIdxs.length; j++) {
								if(flIdxs[j] == -1) {
									isAdd = false;
									break;
								}
							}
							//是否满足条件
							if(isAdd)
								rbBulletins.add(rbt);				    		
				    	}
				    }
				}
			}
			else {
				codes[0] = 2013;
				logger.info("查询数据记录失败");
			}
		}
		else {                   //发布的消息通知
			//查询消息通知状态记录列表以接收用户名
		    List<TEduBdNoticeStatusDo> tebnsDoList = db2Service.getMsgNoticeStatusBySendUserName(userName);
			if(tebnsDoList != null) {
				Map<String, Integer> bIdRcvUserNameToFlagMap = new HashMap<>();
				for(i = 0; i < tebnsDoList.size(); i++) {
					TEduBdNoticeStatusDo tebnsDo = tebnsDoList.get(i);
					//查询消息通知记录以通知id
				    TEduBdMsgNoticeDo tebmnDo = db2Service.getMsgNoticeById(tebnsDo.getBulletinId());
				    if(tebmnDo != null) {
				    	if(tebnsDo.getOwnerUserName().equals(tebnsDo.getRcvUserName())) {
				    		RbBulletins rbt = new RbBulletins();
				    		//通知id
				    		rbt.setBulletinId(tebmnDo.getId());
				    		//通知类型
				    		rbt.setAnnounceType(0);
				    		if(tebmnDo.getAnnounceType() != null) {
				    			rbt.setAnnounceType(tebmnDo.getAnnounceType());
				    		}
				    		//发布单位					    
				    		TEduBdUserDo tmpTebuDo = db2Service.getBdUserInfoByUserName(tebnsDo.getOwnerUserName());  //从数据源ds2的数据表t_edu_bd_user中查找用户信息
				    		if(tmpTebuDo != null)
				    			rbt.setPubOrg(tmpTebuDo.getOrgName());
				    		//发布人
				    		rbt.setPublisher(tebnsDo.getOwnerUserName());
				    		//通知主题
				    		rbt.setTitle(tebmnDo.getTitle());
				    		//发布日期
				    		if(tebmnDo.getCreateTime() != null) {
				    			rbt.setPubDate(tebmnDo.getCreateTime());
				    		}
				    		//是否有附件
				    		if(tebmnDo.getAmFlag() != null)
				    			rbt.setAmFlag(tebmnDo.getAmFlag());
				    		else
				    			rbt.setAmFlag(0);
				    		//是否阅读
				    		rbt.setReadFlag(0);
				    		if(tebnsDo.getReadCount() != null) {
				    			if(tebnsDo.getReadCount() > 0)
				    				rbt.setReadFlag(1);
				    		}
				    		//条件判断
							boolean isAdd = true;
							int[] flIdxs = new int[2];
							//判断标题名称（判断索引0）
							if(title != null) {
								if(rbt.getTitle().indexOf(title) == -1)
									flIdxs[0] = -1;
							}
							//判断阅读标记（判断索引1）
							if(readFlag != -1) {
								if(readFlag != rbt.getReadFlag())
									flIdxs[1] = -1;
							}
							//总体条件判断
							for(j = 0; j < flIdxs.length; j++) {
								if(flIdxs[j] == -1) {
									isAdd = false;
									break;
								}
							}
							//是否满足条件
							if(isAdd)
								rbBulletins.add(rbt);
				    	}
				    	else {
				    		String key = tebnsDo.getBulletinId() + "," + tebnsDo.getRcvUserName();
				    		bIdRcvUserNameToFlagMap.put(key, 1);
				    	}
				    }
				}
				//接收单位
				for(i = 0; i < rbBulletins.size(); i++) {
					String rcvOgrNames = "";
					for(String curKey : bIdRcvUserNameToFlagMap.keySet()) {
						if(curKey.indexOf(rbBulletins.get(i).getBulletinId()) != -1) {
							String[] keys = curKey.split(",");
							logger.info("curKey:"+curKey);
							logger.info("keys:"+keys.length);
							if(keys.length<2) {
								continue;
							}
							//从数据源ds2的数据表t_edu_bd_user中查找用户信息
						    TEduBdUserDo tmpTebuDo = db2Service.getBdUserInfoByUserName(keys[1]);
						    if(tmpTebuDo.getOrgName() != null) {
						    	if(rcvOgrNames.indexOf(tmpTebuDo.getOrgName()) == -1) {
						    		if(rcvOgrNames.length() > 0)
						    			rcvOgrNames += (";" + tmpTebuDo.getOrgName());
						    		else
						    			rcvOgrNames += tmpTebuDo.getOrgName();
						    	}
						    }
						}
					}
					rbBulletins.get(i).setRcvOrgName(rcvOgrNames);
				}
			}
			else {
				codes[0] = 2013;
				logger.info("查询数据记录失败");
			}
		}
		//拍讯
		SortList<RbBulletins> sortList = new SortList<RbBulletins>();  
    	sortList.Sort(rbBulletins, "getPubDate", "desc");		
    	//去掉发布日期时分秒
    	for(i = 0; i < rbBulletins.size(); i++) {
    		String pubDate = rbBulletins.get(i).getPubDate();
    		String[] pubDates = pubDate.split(" ");
    		rbBulletins.get(i).setPubDate(pubDates[0]);
    	}
		//时戳
		rbbDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<RbBulletins> pageBean = new PageBean<RbBulletins>(rbBulletins, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		rbbDto.setPageInfo(pageInfo);
		
		//设置数据
		rbbDto.setRbBulletins(pageBean.getCurPageData()==null?new ArrayList<RbBulletins>():pageBean.getCurPageData());
		//消息ID
		rbbDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rbbDto;
	}		
	
	// 通知列表模型函数
	public RbBulletinsDTO appModFunc(String token, String userName, String startPubDate, String endPubDate, String title, String readFlag, String page, String pageSize, Db1Service db1Service , Db2Service db2Service, int[] codes) {
		RbBulletinsDTO rbbDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//阅读标识，0:未读，1:已读
			int curReadFlag = -1;
			if(readFlag != null)
				curReadFlag = Integer.parseInt(readFlag);
			// 通知列表模型函数
			rbbDto = rbBulletins(token, userName, startPubDate, endPubDate, title, curReadFlag, db1Service, db2Service, codes);
		}
		else {    //模拟数据
			//模拟数据函数
			rbbDto = SimuDataFunc();
		}		

		return rbbDto;
	}
}