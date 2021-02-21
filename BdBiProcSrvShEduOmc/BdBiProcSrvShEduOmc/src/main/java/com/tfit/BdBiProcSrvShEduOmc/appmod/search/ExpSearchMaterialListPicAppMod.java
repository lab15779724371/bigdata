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

import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.search.AppTEduMaterialDishD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//3.2.5.	导出业务监管数据汇总统计报表
public class ExpSearchMaterialListPicAppMod {
	private static final Logger logger = LogManager.getLogger(ExpSearchMaterialListPicAppMod.class.getName());
	
    /**
     * 应急指挥一键查询-学校详情
     */
	SearchByMaterialAppMod searchBySchoolAppMod = new SearchByMaterialAppMod();
	
    /**
     * 应急指挥一键查询-学校信息列表
     */
	SearchMaterialListAppMod searchSchListAppMod = new SearchMaterialListAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expSearchMaterialListPic/";
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
	private ExpCommonDTO SimuDataFunc() {
		//列表元素设置
		ExpCommonDTO expCommonDTO = new ExpCommonDTO();
		//赋值
		expCommonDTO.setStartDate(startDate);
		expCommonDTO.setEndDate(endDate);
		expCommonDTO.setDistName(distName);
		expCommonDTO.setPrefCity(prefCity);
		expCommonDTO.setProvince(province);
		
		
		expCommonDTO.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);

		return expCommonDTO;
	}
	
	//生成导出EXCEL文件
	public boolean ExpSearchBySchoolPicZip(String token,String pathFileName, Map<String,String> inputMap,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService,HttpServletRequest request, HttpServletResponse response) { 
		
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		String startDate = inputMap.get("startDate");
		String endDate = inputMap.get("endDate");
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
		
		AppTEduMaterialDishD inputObj = CommonUtil.map2Object(inputMap, AppTEduMaterialDishD.class);
		if(inputObj == null) {
			//return new ApiResponse<>(IOTRspType.Param_VisitFrmErr, IOTRspType.Param_VisitFrmErr.getMsg()); 
		}
		
		List<AppTEduMaterialDishD> searchMaterialList = dbHiveService.getAppTEduMaterialDishDList(listYearMonth, startDate, endDateAddOne,inputObj, -1, -1);
		//获取学校编号集合，方便后续处理
		List<String> schoolIds = null;
		if(searchMaterialList ==null || searchMaterialList.size()==0) {
			return false;
		}else {
			schoolIds = searchMaterialList.stream().map(AppTEduMaterialDishD::getSchoolId).collect(Collectors.toList());
		}		
		/**
		 * ⑥配送单明细
		 */
		List<CaMatSupDets> caMatSupDets = new ArrayList<>();
		//分页总数
		try {
			
			//获取列表  
			caMatSupDets = dbHiveService.getCaMatSupDetsList(listYearMonth, startDate, endDateAddOne,inputObj.getSchoolId() , 
					null,schoolIds, null, null,null, null, inputObj.getWareBatchNo(), 
					-1, -1, -1, 
					null, null, null,inputObj.getSupplyName(),inputObj.getMaterialName());
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
	public ApiResponse<ExpCommonDTO> appModFunc(String token,Map<String,String> inputMap, Db1Service db1Service,
			 Db2Service db2Service, SaasService saasService ,DbHiveService dbHiveService,
			 HttpServletRequest request, HttpServletResponse response) {
		
		String startDate = inputMap.get("startDate");
		String endDate = inputMap.get("endDate");
		String distName = inputMap.get("distName"); 
		String prefCity = inputMap.get("prefCity");
		String province = inputMap.get("province");
		
		ExpCommonDTO expCommonDTO = new ExpCommonDTO();
		if (isRealData) { // 真实数据
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			
			//生成导出EXCEL文件
			String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
			String pathFileName = SpringConfig.tomcatSrvDirs[1] + repFileName;
			logger.info("导出文件路径：" + pathFileName);
			boolean flag = ExpSearchBySchoolPicZip(token, pathFileName,inputMap, db1Service, db2Service, saasService,dbHiveService,request,response);
			if(flag) {
				//导出信息
				expCommonDTO.setStartDate(startDate);
				expCommonDTO.setEndDate(endDate);
				expCommonDTO.setDistName(AppModConfig.distIdToNameMap.get(distName));
				expCommonDTO.setPrefCity(prefCity);
				expCommonDTO.setProvince(province);
				//导出文件URL
				String expFileUrl = SpringConfig.repfile_srvdn + repFileName;
				logger.info("导出文件URL：" + expFileUrl);
				expCommonDTO.setExpFileUrl(expFileUrl);
			}
			
		} else { // 模拟数据
			// 模拟数据函数
			expCommonDTO = SimuDataFunc();
		}

		return new ApiResponse<>(expCommonDTO);
	}
}
