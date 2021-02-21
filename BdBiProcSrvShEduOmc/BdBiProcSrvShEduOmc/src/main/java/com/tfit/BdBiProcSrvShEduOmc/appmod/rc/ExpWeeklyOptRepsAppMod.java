package com.tfit.BdBiProcSrvShEduOmc.appmod.rc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.tfit.BdBiProcSrvShEduOmc.client.HdfsRWClient;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.ExpDayReps;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.ExpDayRepsDTO;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;


//周运营报告列表应用模型
public class ExpWeeklyOptRepsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpWeeklyOptRepsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;	
	//报表文件资源路径
	String repFileResPath = "/expDishSumInfo/";	
	
	// 周运营报告列表模型函数
	public ExpDayRepsDTO appModFunc(String token, String repType,String repId,String prefCity, String province) {
		
		ExpDayRepsDTO expDayRepsDTO = null;
		if(isRealData) {       //真实数据
			
			expDayRepsDTO = new ExpDayRepsDTO();
			// 时戳
			expDayRepsDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
			
			//hdfs中的文件位置
			String srcPathFileName = "/edu_week_report/shanghai/";
			if(CommonUtil.isNotEmpty(repType)) {
				if("1".equals(repType)) {
					srcPathFileName = "/edu_day_report/shanghai/";
				}else if("3".equals(repType)) {
					srcPathFileName = "/edu_month_report/shanghai/";
				}
			}
			srcPathFileName += repId;
			//需要下载到服务器的磁盘位置
			String localPathName = SpringConfig.tomcatSrvDirs[1]+repFileResPath;
			
			String newData = repId;
			//去除多余的_
			if(repId.indexOf("__") >= 0 ) {
				newData = repId.replaceAll("__", "_");
			}
			String [] datas = newData.split("_");
			
			DateTime startDt = BCDTimeUtil.convertDateStrToDate(datas[2],"yyyyMMdd");
			DateTime endDt = BCDTimeUtil.convertDateStrToDate(datas[3],"yyyyMMdd");
			
			String localFileName = startDt.toString("yyyy年MM月dd日") + "-" +  endDt.toString("dd日")+"周操作数据报告"+"("+datas[1]+")";
			if(CommonUtil.isNotEmpty(repType)) {
				if("1".equals(repType)) {
					localFileName = startDt.toString("yyyy年MM月dd日")+"操作数据报告"+"("+datas[1]+")";
				}else if("3".equals(repType)) {
					localFileName = startDt.toString("yyyy年MM月")+"操作数据报告"+"("+datas[1]+")";
				}
			}
			
			HdfsRWClient.hdfsFileDownloadToFTP(srcPathFileName, localPathName,localFileName+".xls");
			
			ExpDayReps expDishSumInfo = new ExpDayReps();
			expDishSumInfo.setDistName("");
			expDishSumInfo.setRepType(repType);
			expDishSumInfo.setRepId(repId);
			
			//导出文件URL
			String expFileUrl = SpringConfig.repfile_srvdn + repFileResPath+localFileName+".xls";
			//logger.info("导出文件URL：" + expFileUrl);
			expDishSumInfo.setExpFileUrl(expFileUrl);
			
			// 设置数据
			expDayRepsDTO.setData(expDishSumInfo);
			// 消息ID
			expDayRepsDTO.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
		}		

		return expDayRepsDTO;
	}
}