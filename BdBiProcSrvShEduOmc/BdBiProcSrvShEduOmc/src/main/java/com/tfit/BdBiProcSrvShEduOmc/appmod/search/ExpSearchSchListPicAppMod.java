package com.tfit.BdBiProcSrvShEduOmc.appmod.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.ExpSearchBySchool;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.ExpSearchBySchoolDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//3.2.5.	导出业务监管数据汇总统计报表
public class ExpSearchSchListPicAppMod {
	private static final Logger logger = LogManager.getLogger(ExpSearchSchListPicAppMod.class.getName());
	
    /**
     * 应急指挥一键查询-学校详情
     */
	SearchBySchoolAppMod searchBySchoolAppMod = new SearchBySchoolAppMod();
	
    /**
     * 应急指挥一键查询-学校信息列表
     */
	SearchSchListAppMod searchSchListAppMod = new SearchSchListAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expSearchSchList/";
	//导出列名数组
	String[] colNames = {"序号","所在地", "学制", "学校", "地址", "法人代表", "联系人","联系人电话"};	
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String ppName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String rsFlag = null;
	String schType = null;
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpSearchBySchoolDTO SimuDataFunc() {
		ExpSearchBySchoolDTO eppGsPlanOptsDTO = new ExpSearchBySchoolDTO();
		//设置返回数据
		eppGsPlanOptsDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpSearchBySchool ExpSearchBySchool = new ExpSearchBySchool();
		//赋值
		ExpSearchBySchool.setStartDate(startDate);
		ExpSearchBySchool.setEndDate(endDate);
		ExpSearchBySchool.setDistName(distName);
		ExpSearchBySchool.setPrefCity(prefCity);
		ExpSearchBySchool.setProvince(province);
		
		
		ExpSearchBySchool.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		eppGsPlanOptsDTO.setExpSearchBySchool(ExpSearchBySchool);
		//消息ID
		eppGsPlanOptsDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return eppGsPlanOptsDTO;
	}
	
	//生成导出EXCEL文件
	public boolean ExpSearchBySchoolPicZip(String token,String pathFileName, String startDate,String endDate,String distName,String schName,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService,HttpServletRequest request, HttpServletResponse response) { 
		
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		if(startDate==null || startDate.split("-").length < 2) {
    		startDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	if((endDate==null || endDate.split("-").length < 2)&& startDate!=null) {
    		endDate = startDate;
    	}else if (endDate==null || endDate.split("-").length < 2) {
    		endDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	
    	String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];
    	
		//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		
		
		/**
		 * 一：获取数据
		 */
		//1.获取学校列表（所有取数的基数）
		List<SearchSch> searchSchList  = dbHiveService.getSchList(schName, distName,null, null,null, null);
		//获取学校编号集合，方便后续处理
		List<String> schoolIds = null;
		if(searchSchList ==null || searchSchList.size()==0) {
			return false;
		}else {
			schoolIds = searchSchList.stream().map(SearchSch::getSchoolId).collect(Collectors.toList());
		}
		
		//2.三、关联学校详情(根据学校信息全部获取，根据每个学校组织数据)
				
		/**
		 * ⑥配送单明细
		 */
		List<CaMatSupDets> caMatSupDets = new ArrayList<>();
		//分页总数
		try {
			//获取列表  
			caMatSupDets = dbHiveService.getCaMatSupDetsList(listYearMonth, startDate, endDateAddOne, null, 
					distName,schoolIds, null, null,null, null, null, -1, -1, -1, 
					null, null, null,null,null);
			//移除所有未null的值
			//caMatSupDets.removeAll(Collections.singleton(null));
			
			
			
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
		
		List<String> picList = new ArrayList<String>();
		String key = "";
		//key : 学校名称+供应商名称+图片类型（配送单图片、检疫证图片） value:图片地址集合
		Map<String,List<String>> picMap = new HashMap<String,List<String>>();
		List <String> ledgerMasterIdList = new ArrayList<String>();
		if(caMatSupDets !=null && caMatSupDets.size()>0) {
			for(CaMatSupDets caMatSupDetTemp:caMatSupDets) {
				//同一组图片只取一次
				if(ledgerMasterIdList.indexOf(caMatSupDetTemp.getLedgerMasterId()) >=0) {
					continue;
				}
				
				//配送单图片集合
				if(caMatSupDetTemp.getGsBillPicUrls() != null && caMatSupDetTemp.getGsBillPicUrls().size() >0) {
					ledgerMasterIdList.add(caMatSupDetTemp.getLedgerMasterId());
					key = caMatSupDetTemp.getSchName()+"/"+caMatSupDetTemp.getSupplierName()+"_"+caMatSupDetTemp.getMatUseDate().replace("/", "-")+"_"+"配送单图片";
					picList = new ArrayList<String>();
					if(picMap.get(key)!=null) {
						picList = picMap.get(key);
					}
					picList.addAll(caMatSupDetTemp.getGsBillPicUrls());
					picMap.put(key, picList);
				}
				
				//验收图片集合
				if(caMatSupDetTemp.getQaCertPicUrls() != null && caMatSupDetTemp.getQaCertPicUrls().size() >0) {
					ledgerMasterIdList.add(caMatSupDetTemp.getLedgerMasterId());
					key = caMatSupDetTemp.getSchName()+"/"+caMatSupDetTemp.getSupplierName()+"_"+caMatSupDetTemp.getMatUseDate().replace("/", "-")+"_"+"检疫证图片";
					picList = new ArrayList<String>();
					if(picMap.get(key)!=null) {
						picList = picMap.get(key);
					}
					picList.addAll(caMatSupDetTemp.getQaCertPicUrls());
					picMap.put(key, picList);
				}
			}
		}
		
		try {
            String downloadFilename = "DistributionPics.zip";//文件的名称
            downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            	
            for(Map.Entry<String, List<String>> entry : picMap.entrySet()) {
            	if (entry.getValue() != null && entry.getValue().size() > 0) {
            		for(int i = 0;i<entry.getValue().size();i++) {
            			 URL url = new URL(entry.getValue().get(i));
                         zos.putNextEntry(new ZipEntry(entry.getKey()+(i+1)+".jpg"));
                         //FileInputStream fis = new FileInputStream(new File(files[i])); 
                         InputStream fis = url.openConnection().getInputStream();  
                         byte[] buffer = new byte[1024];    
                         int r = 0;    
                         while ((r = fis.read(buffer)) != -1) {    
                             zos.write(buffer, 0, r);    
                         }    
                         fis.close();  
            		}
            	}
            }
            zos.flush();    
            zos.close();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
		
		return true;
    }
	
	
	public void download(HttpServletRequest request, HttpServletResponse response){
		 
        try {
            String downloadFilename = "中文.zip";//文件的名称
            downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            		
            String[] files = new String[]{"http://uploadpic-cdn.sunshinelunch.com/app/fujian-1560985055375.jpg",
            		"http://uploadpic-cdn.sunshinelunch.com/app/fujian-1560985055754.jpg",
            		"http://uploadpic-cdn.sunshinelunch.com/app/fujian-1560985055578.jpg",
            		"http://uploadpic-cdn.sunshinelunch.com/app/fujian-1560985055132.jpg",
            		"http://uploadpic-cdn.sunshinelunch.com/app/fujian-1560985055919.jpg",
            		"http://uploadpic-cdn.sunshinelunch.com/app/fujian-1560985056096.jpg"
            		};
            for (int i=0;i<files.length;i++) {
                URL url = new URL(files[i]);
               zos.putNextEntry(new ZipEntry(i+".jpg"));
               //FileInputStream fis = new FileInputStream(new File(files[i])); 
               InputStream fis = url.openConnection().getInputStream();  
               byte[] buffer = new byte[1024];    
               int r = 0;    
               while ((r = fis.read(buffer)) != -1) {    
                   zos.write(buffer, 0, r);    
               }    
               fis.close();  
              } 
            zos.flush();    
            zos.close();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
	} 
	
	
	//导出项目点排菜详情列表模型函数
	public ExpSearchBySchoolDTO appModFunc(String token,String startDate,String endDate, String schName,
			String distName, String prefCity, String province, Db1Service db1Service,
			 Db2Service db2Service, SaasService saasService ,DbHiveService dbHiveService,
			 HttpServletRequest request, HttpServletResponse response) {
		ExpSearchBySchoolDTO eppGsPlanOptsDTO = null;
		if (isRealData) { // 真实数据
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			
			//生成导出EXCEL文件
			String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
			String pathFileName = SpringConfig.tomcatSrvDirs[1] + repFileName;
			logger.info("导出文件路径：" + pathFileName);
			boolean flag = ExpSearchBySchoolPicZip(token, pathFileName, startDate, endDate,distName,schName, db1Service, db2Service, saasService,dbHiveService,request,response);
			if(flag) {
				eppGsPlanOptsDTO = new ExpSearchBySchoolDTO();
				ExpSearchBySchool expSearchBySchool = new ExpSearchBySchool();
				//时戳
				eppGsPlanOptsDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
				//导出信息
				expSearchBySchool.setStartDate(startDate);
				expSearchBySchool.setEndDate(endDate);
				expSearchBySchool.setDistName(AppModConfig.distIdToNameMap.get(distName));
				expSearchBySchool.setPrefCity(prefCity);
				expSearchBySchool.setProvince(province);
				//导出文件URL
				String expFileUrl = SpringConfig.repfile_srvdn + repFileName;
				logger.info("导出文件URL：" + expFileUrl);
				expSearchBySchool.setExpFileUrl(expFileUrl);
				eppGsPlanOptsDTO.setExpSearchBySchool(expSearchBySchool);
				//消息ID
				eppGsPlanOptsDTO.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			
		} else { // 模拟数据
			// 模拟数据函数
			eppGsPlanOptsDTO = SimuDataFunc();
		}

		return eppGsPlanOptsDTO;
	}
}
