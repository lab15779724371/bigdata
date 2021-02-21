package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMsgNoticeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletinDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletinDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletins;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletinsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.SignInConfirms;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.SignInInfo;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//通知详情应用模型
public class RbBulletinDetAppMod {
	private static final Logger logger = LogManager.getLogger(RbBulletinDetAppMod.class.getName());
	
	//通知列表应用模型
	private RbBulletinsAppMod rbbAppMod = new RbBulletinsAppMod();
	
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String bulletinId = "256510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String title = "上海市食品药品监督管理局关于不合格食品风险控制情况的通告（2018年第20期）";
	String pubOrg = "上海市教委";
	String pubDate = "2018-09-05";
	String content = "<p style=\"text-align:left;\"><span>2018年&nbsp; 第41号</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　国家市场监督管理总局发布的《关于28批次食品不合格情况的通告》（2018年第25号），涉及本市2家食品经营企业2批次不合格食品。现将不合格食品风险控制情况通告如下：</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　一、上海黄金搭档生物科技有限公司在天猫（网站）销售的年轻态牌劲骨胶囊</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　（一）食品名称：年轻态牌劲骨胶囊；商标：/；规格型号：600mg/粒；生产日期：2017-10-30；标称生产企业：无锡健特药业有限公司；不合格项目：霉菌和酵母。</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　（二）经上海市徐汇区市场监督管理局调查，上海黄金搭档生物科技有限公司销售上述年轻态牌劲骨胶囊的情况为：进货720盒,销售193盒（其中用于检测2盒），库存527盒。</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　（三）上海市徐汇区市场监督管理局已责令上海黄金搭档生物科技有限公司对上述不合格食品立即停售、下架和召回，并对上海黄金搭档生物科技有限公司进行立案调查。</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　二、1号店自营鹤王旗舰店（经营者为武汉京东金德贸易有限公司）在1号店（网站）销售的鹤王牌阿胶浆</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　（一）食品名称:鹤王牌阿胶浆;商标：鹤王;规格型号：20克/瓶;生产日期：2018-04-12;标称生产企业：山东鹤王生物工程有限公司;不合格项目：蛋白质.</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　（二）经上海市食品药品监督管理局执法总队对1号店平台运营方上海京东才奥电子商务有限公司调查，“1号店自营鹤王旗舰店”为武汉京东金德贸易有限公司在1号店平台的入驻商家,上海京东才奥电子商务有限公司能够提供上述产品销售企业的《营业执照》、《食品经营许可证》复印件,并对上述产品的进货票据进行了查验。上海市食品药品监督管理局执法总队责令上海京东才奥电子商务有限公司停售、下架和召回不合格食品并限期提供整改报告。</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:left;\"><span>　　广大消费者如发现经营上述不合格食品的违法行为，可拨打食品药品监管部门12331热线电话投诉举报。</span></p><p style=\"text-align:left;\"><span><br></span></p><pstyle=\"text-align:left;\"><span>　　特此通告。</span></p><p style=\"text-align:left;\"><span><br></span></p><p style=\"text-align:right;\"><span>　　上海市食品药品监督管理局</span></p><p style=\"text-align:right;\"><span><br></span></p><p style=\"text-align:right;\"><span>　　2018年9月4日</span></p><p style=\"text-align:left;\"><span>&nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span></p><p style=\"text-align:left;\"><span>（转自上海市食品药品监督管理局网站）</span></p>";
	int amFlag = 1;
	String readUsers = "jizheng,jizheng01";
	String unreadUsers = "admin,admin001";
	String amName = "2018年12月09日未排菜学校名单.xls", amUrl = "http://192.168.1.241:10101/biOptAnl/2018年12月09日未排菜学校名单.xls";	
	String lastBulletinId = "366510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String lastTitle = "本市2018年第11轮网络餐饮服务监测结果";
	String nextBulletinId = "496510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String nextTitle = "关于杨浦中学食堂检查结果通知报告";	
	
	//模拟数据函数
	private RbBulletinDetDTO SimuDataFunc() {
		RbBulletinDetDTO rbdDto = new RbBulletinDetDTO();
		//时戳
		rbdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//通知详情模拟数据
		RbBulletinDet rbBulletinDet = new RbBulletinDet();
		//赋值
		rbBulletinDet.setBulletinId(bulletinId);
		rbBulletinDet.setTitle(title);
		rbBulletinDet.setPubOrg(pubOrg);
		rbBulletinDet.setPubDate(pubDate);
		rbBulletinDet.setContent(content);
		rbBulletinDet.setAmFlag(amFlag);
		rbBulletinDet.setReadUsers(readUsers);
		rbBulletinDet.setUnreadUsers(unreadUsers);
		List<RbUlAttachment> amInfos = new ArrayList<>();
		RbUlAttachment rua = new RbUlAttachment();
		rua.setAmName(amName);
		rua.setAmUrl(amUrl);
		amInfos.add(rua);
		rbBulletinDet.setAmInfos(amInfos);
		rbBulletinDet.setLastBulletinId(lastBulletinId);
		rbBulletinDet.setLastTitle(lastTitle);
		rbBulletinDet.setNextBulletinId(nextBulletinId);
		rbBulletinDet.setNextTitle(nextTitle);
		//设置数据
		rbdDto.setRbBulletinDet(rbBulletinDet);
		//消息ID
		rbdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rbdDto;
	}
	
	//获取通知列表数据
	private List<RbBulletins> getRbBulletins(String token, String userName, Db1Service db1Service , Db2Service db2Service, int[] codes) {
		List<RbBulletins> rbBulletins = null;
		String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
		RbBulletinsDTO pdlDto = rbbAppMod.appModFunc(token, userName, null, null, null, null, strCurPageNum, strPageSize, db1Service , db2Service, codes);
		if(pdlDto != null) {
			int i, totalCount = pdlDto.getPageInfo().getPageTotal();
			int pageCount = 0;
			rbBulletins = new ArrayList<>();
			if(totalCount % pageSize == 0)
				pageCount = totalCount/pageSize;
			else
				pageCount = totalCount/pageSize + 1;
			//第一页数据
			if(pdlDto.getRbBulletins() != null) {
				rbBulletins.addAll(pdlDto.getRbBulletins());			
			}
			//后续页数据
			for(i = curPageNum+1; i <= pageCount; i++) {
				strCurPageNum = String.valueOf(i);
				RbBulletinsDTO curGpoDto = rbbAppMod.appModFunc(token, userName, null, null, null, null, strCurPageNum, strPageSize, db1Service , db2Service, codes);
				if(curGpoDto.getRbBulletins() != null) {
					rbBulletins.addAll(curGpoDto.getRbBulletins());
				}
			}
		}
		
		return rbBulletins;
	}
	
	// 通知详情模型函数
	public RbBulletinDetDTO appModFunc(String token, String bulletinId, String userName, String distName, String prefCity, String province, Db1Service db1Service, Db2Service db2Service, int[] codes) {
		RbBulletinDetDTO rbdDto = null;
		if(isRealData) {       //真实数据		
  			if(bulletinId != null) {      //用户名（账号）
  				//获取通知列表数据
  				List<RbBulletins> rbBulletins = getRbBulletins(token, userName, db1Service , db2Service, codes);
  				if(userName == null) {   //接收用户通知详情
  					//查询消息通知记录以通知id
  					TEduBdMsgNoticeDo tebmnDo = db2Service.getMsgNoticeById(bulletinId);
  					rbdDto = new RbBulletinDetDTO();
  					//时戳
  					rbdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  					//账号详情模拟数据
  					RbBulletinDet rbBulletinDet = new RbBulletinDet();
  					//赋值  				
  					rbBulletinDet.setBulletinId(tebmnDo.getId());                    //通知ID
  					rbBulletinDet.setTitle(tebmnDo.getTitle());                      //标题
  					TEduBdUserDo tmpTebuDo = db2Service.getBdUserInfoByUserName(tebmnDo.getUserName());  //从数据源ds2的数据表t_edu_bd_user中查找用户信息
  					if(tmpTebuDo != null)
  						rbBulletinDet.setPubOrg(tmpTebuDo.getOrgName());             //发布单位                 
  					rbBulletinDet.setPublisher(tebmnDo.getUserName());               //发布人
  					rbBulletinDet.setPubDate(tebmnDo.getCreateTime());               //发布日期
  					if(tebmnDo.getAnnCont() != null)
  						rbBulletinDet.setContent(new String(tebmnDo.getAnnCont(), 0, tebmnDo.getAnnCont().length));                  //公告内容
  					else
  						rbBulletinDet.setContent("");
  					rbBulletinDet.setAmFlag(tebmnDo.getAmFlag());                    //是否有附件标识，0:无，1:有
  					//查询消息通知状态记录列表以通知ID和发布用户名
  					List<TEduBdNoticeStatusDo> tebnsDoList = db2Service.getMsgNoticeStatusBybIdSendUser(bulletinId, tebmnDo.getUserName());
  					if(tebnsDoList != null) {
  						String readUsers = "", unreadUsers = "";
  						for(int i = 0; i < tebnsDoList.size(); i++) {
  							TEduBdNoticeStatusDo tebnsDo = tebnsDoList.get(i); 
  							if(!tebnsDo.getOwnerUserName().equals(tebnsDo.getRcvUserName())) {
  								int readCount = 0;
  								if(tebnsDo.getReadCount() != null)
  									readCount = tebnsDo.getReadCount();
  								if(readCount > 0) {   //已读
  									if(readUsers.length() > 0)
  										readUsers += ("," + tebnsDo.getRcvUserName());     //已读用户，多个用户时用”,”隔开
  									else
  										readUsers += tebnsDo.getRcvUserName();             //已读用户，多个用户时用”,”隔开
  								}
  								else {  //未读
  									if(unreadUsers.length() > 0)
  										unreadUsers += ("," + tebnsDo.getRcvUserName());   //未读用户，多个用户时用”,”隔开
  									else
  										unreadUsers += tebnsDo.getRcvUserName();           //未读用户，多个用户时用”,”隔开
  								}
  							}
  						}
  						rbBulletinDet.setReadUsers(readUsers);
  						rbBulletinDet.setUnreadUsers(unreadUsers);
  					}
  					//附件信息
  					List<RbUlAttachment> amInfos = new ArrayList<>();
  					if(tebmnDo.getAmInfo() != null) {
  						String[] strAmInfos = tebmnDo.getAmInfo().split(",");
  						if(strAmInfos.length > 0 && strAmInfos.length%2 == 0) {
  							for(int i = 0; i < strAmInfos.length/2; i++) {
  								RbUlAttachment rua = new RbUlAttachment();
  								rua.setAmName(strAmInfos[2*i]);
  								rua.setAmUrl(SpringConfig.repfile_srvdn+strAmInfos[2*i+1]);
  								amInfos.add(rua);
  							}
  						}
  					}
  					rbBulletinDet.setAmInfos(amInfos);
  				    //上一条和下一条记录信息
  				    for(int i = 0; i < rbBulletins.size(); i++) {
  				    	if(rbBulletins.get(i).getBulletinId().equals(bulletinId)) {
  				    		int pre_i = i-1, next_i = i+1;
  				    		if(pre_i >= 0) {
  				    			logger.info("上一条消息ID：" + rbBulletins.get(pre_i).getBulletinId());
  				    			rbBulletinDet.setLastBulletinId(rbBulletins.get(pre_i).getBulletinId());           //上一条通知ID
  		  						rbBulletinDet.setLastTitle(rbBulletins.get(pre_i).getTitle());                     //上一条通知标题
  				    		}
  				    		if(next_i < rbBulletins.size()) {
  				    			logger.info("下一条消息ID：" + rbBulletins.get(next_i).getBulletinId());
  				    			rbBulletinDet.setNextBulletinId(rbBulletins.get(next_i).getBulletinId());          //下一条通知ID
  		  						rbBulletinDet.setNextTitle(rbBulletins.get(next_i).getTitle());                    //下一条通知标题  
  				    		}
  				    	}
  				    }
  					//设置数据
  					rbdDto.setRbBulletinDet(rbBulletinDet);
  					//消息ID
  					rbdDto.setMsgId(AppModConfig.msgId);
  					AppModConfig.msgId++;
  					// 消息id小于0判断
  					AppModConfig.msgIdLessThan0Judge();
  				}
  				else {   //发送用户通知详情
 					//查询消息通知记录以通知id
  					TEduBdMsgNoticeDo tebmnDo = db2Service.getMsgNoticeById(bulletinId);
  					rbdDto = new RbBulletinDetDTO();
  					//时戳
  					rbdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  					//账号详情模拟数据
  					RbBulletinDet rbBulletinDet = new RbBulletinDet();
  					//赋值  				
  					rbBulletinDet.setBulletinId(tebmnDo.getId());                    //通知ID
  					if(tebmnDo.getAnnounceType() != null)
  						rbBulletinDet.setAnnounceType(tebmnDo.getAnnounceType());    //通知类型
  					rbBulletinDet.setTitle(tebmnDo.getTitle());                      //标题
  					TEduBdUserDo tmpTebuDo = db2Service.getBdUserInfoByUserName(tebmnDo.getUserName());  //从数据源ds2的数据表t_edu_bd_user中查找用户信息
  					if(tmpTebuDo != null)
  						rbBulletinDet.setPubOrg(tmpTebuDo.getOrgName());             //发布单位                 
  					rbBulletinDet.setPublisher(tebmnDo.getUserName());               //发布人
  					rbBulletinDet.setPubDate(tebmnDo.getCreateTime());               //发布日期
  					if(tebmnDo.getAnnCont() != null)
  						rbBulletinDet.setContent(new String(tebmnDo.getAnnCont(), 0, tebmnDo.getAnnCont().length));                  //公告内容
  					else
  						rbBulletinDet.setContent("");
  					rbBulletinDet.setAmFlag(tebmnDo.getAmFlag());                    //是否有附件标识，0:无，1:有
  					//查询消息通知状态记录列表以通知ID和发布用户名
  					List<TEduBdNoticeStatusDo> tebnsDoList = db2Service.getMsgNoticeStatusBybIdSendUser(bulletinId, tebmnDo.getUserName());
  					if(tebnsDoList != null) {
  						String readUsers = "", unreadUsers = "";
  						for(int i = 0; i < tebnsDoList.size(); i++) {
  							TEduBdNoticeStatusDo tebnsDo = tebnsDoList.get(i); 
  							if(!tebnsDo.getOwnerUserName().equals(tebnsDo.getRcvUserName())) {
  								int readCount = 0;
  								if(tebnsDo.getReadCount() != null)
  									readCount = tebnsDo.getReadCount();
  								if(readCount > 0) {   //已读
  									if(readUsers.length() > 0)
  										readUsers += ("," + tebnsDo.getRcvUserName());     //已读用户，多个用户时用”,”隔开
  									else
  										readUsers += tebnsDo.getRcvUserName();             //已读用户，多个用户时用”,”隔开
  								}
  								else {  //未读
  									if(unreadUsers.length() > 0)
  										unreadUsers += ("," + tebnsDo.getRcvUserName());   //未读用户，多个用户时用”,”隔开
  									else
  										unreadUsers += tebnsDo.getRcvUserName();           //未读用户，多个用户时用”,”隔开
  								}
  							}
  						}
  						rbBulletinDet.setReadUsers(readUsers);
  						rbBulletinDet.setUnreadUsers(unreadUsers);
  					}
  					//附件信息
  					List<RbUlAttachment> amInfos = new ArrayList<>();
  					if(tebmnDo.getAmInfo() != null) {
  						String[] strAmInfos = tebmnDo.getAmInfo().split(",");
  						if(strAmInfos.length > 0 && strAmInfos.length%2 == 0) {
  							for(int i = 0; i < strAmInfos.length/2; i++) {
  								RbUlAttachment rua = new RbUlAttachment();
  								rua.setAmName(strAmInfos[2*i]);
  								rua.setAmUrl(SpringConfig.repfile_srvdn+strAmInfos[2*i+1]);
  								amInfos.add(rua);
  							}
  						}
  					}
  					rbBulletinDet.setAmInfos(amInfos);
  					//上一条和下一条记录信息
  				    for(int i = 0; i < rbBulletins.size(); i++) {
  				    	if(rbBulletins.get(i).getBulletinId().equals(bulletinId)) {
  				    		int pre_i = i-1, next_i = i+1;
  				    		if(pre_i >= 0) {
  				    			logger.info("上一条消息ID：" + rbBulletins.get(pre_i).getBulletinId());
  				    			rbBulletinDet.setLastBulletinId(rbBulletins.get(pre_i).getBulletinId());           //上一条通知ID
  		  						rbBulletinDet.setLastTitle(rbBulletins.get(pre_i).getTitle());                     //上一条通知标题
  				    		}
  				    		if(next_i < rbBulletins.size()) {
  				    			logger.info("下一条消息ID：" + rbBulletins.get(next_i).getBulletinId());
  				    			rbBulletinDet.setNextBulletinId(rbBulletins.get(next_i).getBulletinId());          //下一条通知ID
  		  						rbBulletinDet.setNextTitle(rbBulletins.get(next_i).getTitle());                    //下一条通知标题  
  				    		}
  				    	}
  				    }
  				    //签到信息
  				    if(tebmnDo.getAnnounceType() != null) {
  				    	if(tebmnDo.getAnnounceType() == 1) {
  				    		if(tebnsDoList != null) {
  				    			SignInInfo signInInfo = new SignInInfo();
  				    			List<SignInConfirms> signInConfirms = new ArrayList<>();
  		  						int totalPartPn = 0, actPn = 0, noPartPn = 0;
  		  						for(int i = 0; i < tebnsDoList.size(); i++) {
  		  							TEduBdNoticeStatusDo tebnsDo = tebnsDoList.get(i); 
  		  							if(!tebnsDo.getOwnerUserName().equals(tebnsDo.getRcvUserName())) {
  		  								SignInConfirms sic = new SignInConfirms();
  		  								totalPartPn++;
  		  								if(tebnsDo.getSignFlag() == null || tebnsDo.getSignFlag() == 0) {    //未签到
  		  									noPartPn++;
  		  								}
  		  								else if(tebnsDo.getSignFlag() == 1) {    //已签到
  		  									actPn++;
		  								}
  		  								sic.setUserName(tebnsDo.getRcvUserName());
  		  								if(tebnsDo.getSignFlag() != null)
  		  									sic.setSignFlag(tebnsDo.getSignFlag());
  		  								signInConfirms.add(sic);
  		  							}
  		  						}
  		  						signInInfo.setTotalPartPn(totalPartPn);
  		  						signInInfo.setActPn(actPn);
  		  						signInInfo.setNoPartPn(noPartPn);
  		  						signInInfo.setSignInConfirms(signInConfirms);
  		  						rbBulletinDet.setSignInInfo(signInInfo);
  		  					}
  				    	}
  				    }
  					//设置数据
  					rbdDto.setRbBulletinDet(rbBulletinDet);
  					//消息ID
  					rbdDto.setMsgId(AppModConfig.msgId);
  					AppModConfig.msgId++;
  					// 消息id小于0判断
  					AppModConfig.msgIdLessThan0Judge();
  				}
  			}
  			else {
  				logger.info("访问接口参数非法！");
  			}
		}
		else {    //模拟数据
			//模拟数据函数
			rbdDto = SimuDataFunc();
		}		

		return rbdDto;
	}
}
