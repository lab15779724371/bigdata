package com.tfit.BdBiProcSrvShEduOmc.appmod.bks;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStovePbVids;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStovePbVidsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//3.7.2 - 明厨亮灶视频回放列表应用模型
public class BriKitStovePbVidsAppMod {
	private static final Logger logger = LogManager.getLogger(BriKitStovePbVidsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化
	String[] ppName_Array = {"上海市天山中学（食堂）", "上海市天山中学（食堂）", "上海市紫薇幼儿园（食堂）", "上海市紫薇幼儿园（食堂）", "上师大附属小学（食堂）", "上师大附属小学（食堂）"};
	String[] camId_Array = {"1", "2", "3", "4", "5", "6"};
	String[] camIp_Array = {"10.1.21.3", "10.1.21.4", "10.1.21.5", "10.1.21.6", "10.1.21.7", "10.1.21.8"};
	String[] vidSrcName_Array = {"上海市天山中学（食堂）-厨房 01", "上海市天山中学（食堂）-厨房 02", "上海市紫薇幼儿园（食堂）-厨房 01", "上海市紫薇幼儿园（食堂）-厨房 02", "上师大附属小学（食堂）-厨房 01", "上师大附属小学（食堂）-厨房 02"};
	String[] vidUrl_Array = {"/briKitStovePbVids/1.mp4", "/briKitStovePbVids/2.mp4", "/briKitStovePbVids/3.mp4", "/briKitStovePbVids/4.mp4", "/briKitStovePbVids/5.mp4", "/briKitStovePbVids/6.mp4"};

	//模拟数据函数
	private BriKitStovePbVidsDTO SimuDataFunc() {
		BriKitStovePbVidsDTO bksrvDto = new BriKitStovePbVidsDTO();
		//时戳
		bksrvDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//明厨亮灶视频回放列表模拟数据
		List<BriKitStovePbVids> briKitStovePbVids = new ArrayList<>();
		//赋值
		for (int i = 0; i < ppName_Array.length; i++) {
			BriKitStovePbVids bksrv = new BriKitStovePbVids();
			bksrv.setPpName(ppName_Array[i]);
			bksrv.setCamId(camId_Array[i]);
			bksrv.setCamIp(camIp_Array[i]);
			bksrv.setVidSrcName(vidSrcName_Array[i]);
			bksrv.setVidUrl(SpringConfig.video_srvdn + vidUrl_Array[i]);
			briKitStovePbVids.add(bksrv);
		}
		//设置数据
		bksrvDto.setBriKitStovePbVids(briKitStovePbVids);
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
	
	// 明厨亮灶视频回放列表模型函数
	public BriKitStovePbVidsDTO appModFunc(String ppName, String startSubTime, String endSubTime, String distName, String prefCity, String province, Db1Service db1Service) {
		BriKitStovePbVidsDTO bksrvDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			bksrvDto = SimuDataFunc();
		}		

		return bksrvDto;
	}
}
