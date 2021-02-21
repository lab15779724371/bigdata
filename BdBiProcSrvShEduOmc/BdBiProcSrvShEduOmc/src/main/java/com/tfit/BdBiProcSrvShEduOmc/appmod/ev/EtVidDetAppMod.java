package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//教育培训视频详情应用模型
public class EtVidDetAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidDetAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//变量数据初始化
	String vidId = "319d4ab7-5f60-4e99-9f32-495f024a0286";
	String createTime = "2019-06-10 15:28:33";
	String vidName = "操作视频演示";
	String subTitle = "操作视频演示001";
	int vidCategory = 0;
	String thumbUrl = "http://ygwc-test.tfitsoft.com/repFile/8dc4ba83c51.png";
    String vidUrl = "http://ygwc-test.tfitsoft.com/repFile/8dc4ba83c51.mp4";
	String vidDescrCont = "操作视频演示";
	String lastUpdateTime = "2019-06-10 15:28:33";
	
	//模拟数据函数
	private EtVidDetDTO SimuDataFunc() {
		EtVidDetDTO evdDto = new EtVidDetDTO();
		//时戳
		evdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//教育培训视频详情模拟数据
		EtVidDet etVidDet = new EtVidDet();
		//赋值
		etVidDet.setVidId(vidId);
		etVidDet.setCreateTime(createTime);
		etVidDet.setVidName(vidName);
		etVidDet.setSubTitle(subTitle);
		etVidDet.setVidCategory(vidCategory);
		etVidDet.setThumbUrl(thumbUrl);
		etVidDet.setVidUrl(vidUrl);
		etVidDet.setVidDescrCont(vidDescrCont);
		etVidDet.setLastUpdateTime(lastUpdateTime);
		//设置数据
		evdDto.setEtVidDet(etVidDet);
		//消息ID
		evdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return evdDto;
	}
	
	// 教育培训视频详情模型函数
	public EtVidDetDTO appModFunc(String token, String vidId, String distName, String prefCity, String province, Db1Service db1Service, Db2Service db2Service) {
		EtVidDetDTO evdDto = null;
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			if(vidId != null) {
				evdDto = new EtVidDetDTO();
				//时戳
				evdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//教育培训视频详情
				EtVidDet etVidDet = null;
				//获取教育视频以记录ID
				TEduBdEtvidLibDo tebelDo = db2Service.getTEduBdEtvidLibDoById(vidId);
				//赋值
				if(tebelDo != null) {
					etVidDet = new EtVidDet();
					etVidDet.setVidId(tebelDo.getId());                                                                         //视频ID，视频记录唯一标识
					etVidDet.setCreateTime(tebelDo.getCreateTime());                                                            //创建时间
					etVidDet.setVidName(tebelDo.getVidName());                                                                  //视频名称	
					etVidDet.setSubTitle(tebelDo.getSubTitle());                                                                //副标题
					etVidDet.setVidCategory(tebelDo.getVidCategory());                                                          //视频分类，0:系统操作，1:食品安全，2:政策法规
					etVidDet.setThumbUrl(SpringConfig.video_srvdn + tebelDo.getThumbUrl());                                     //缩略图图片URL
					etVidDet.setVidUrl(SpringConfig.video_srvdn + tebelDo.getVidUrl());                                         //视频URL
					if(tebelDo.getVidDescrCont() != null)
						etVidDet.setVidDescrCont(new String(tebelDo.getVidDescrCont(), 0, tebelDo.getVidDescrCont().length));       //视频描述内容
					else
						etVidDet.setVidDescrCont("");
					etVidDet.setLastUpdateTime(tebelDo.getLastUpdateTime());                                                    //最近更新时间
				}
				//设置数据
				evdDto.setEtVidDet(etVidDet);
				//消息ID
				evdDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			evdDto = SimuDataFunc();
		}		

		return evdDto;
	}
}