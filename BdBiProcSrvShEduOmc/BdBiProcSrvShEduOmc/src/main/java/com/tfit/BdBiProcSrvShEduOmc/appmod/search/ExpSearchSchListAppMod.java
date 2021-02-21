package com.tfit.BdBiProcSrvShEduOmc.appmod.search;

import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.ExpSearchBySchool;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.ExpSearchBySchoolDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchBySchoolDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchBySchoolDetail;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchDateDishList;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchDish;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchLicense;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSupplier;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSupplyMatSup;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//3.2.5.	导出业务监管数据汇总统计报表
public class ExpSearchSchListAppMod {
	private static final Logger logger = LogManager.getLogger(ExpSearchSchListAppMod.class.getName());
	
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
	public boolean ExpSearchBySchoolExcel(String token,String pathFileName, String startDate,String endDate,String distName,String schName,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService) { 
		
		Map<String,UserSetColums>  userSetColumsMap =CommonUtil.getUserSetColumMap(token,"searchSchList", db2Service);
		
		boolean retFlag = true;
		Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        Sheet sheet = null;
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        if(idx1 != -1)
        	fileType = excelPath.substring(idx1+1);
        else if(idx2 != -1)
        	fileType = excelPath.substring(idx2+1);
        //创建工作文档对象   
        if (!file.exists()) {      //excel文件不存在
            if (fileType.equals("xls")) {
                wb = new HSSFWorkbook();                
            } else if(fileType.equals("xlsx")) {  
            	wb = new XSSFWorkbook();
            } else {
            	retFlag = false;
            }
            //创建sheet对象   
            if(retFlag) {
            	sheet = (Sheet) wb.createSheet("sheet1");  
            	OutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(excelPath);
				} catch (FileNotFoundException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				if(outputStream != null) {
					try {
						wb.write(outputStream);
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					try {
						outputStream.flush();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					try {
						outputStream.close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
				else
					retFlag = false;
            }
            
        } 
        else {       //excel文件已存在
            if (fileType.equals("xls")) {  
                wb = new HSSFWorkbook();                  
            } else if(fileType.equals("xlsx")) { 
                wb = new XSSFWorkbook();                  
            } else {  
            	retFlag = false;
            }  
        }
        //创建sheet对象  
        if (sheet == null && retFlag) {
            sheet = (Sheet) wb.createSheet("sheet1");  
        }
        //写excel文件数据
        if(sheet != null && retFlag) {
        	Integer startRowIdx = 0;
        	String[] colVals = new String[21];
        	
			//加粗字体
		  	CellStyle style = AppModConfig.getExcellCellStyle(wb);
		  	
		  	//居左+加粗字体格式
		  	CellStyle styleAlignLeft = AppModConfig.getExcellCellStyleAlignLeft(wb);
		  	
		  	//浮点型，2位数字格式化格式（2位有效数字+%+边框）
		  	CellStyle cellStyleFloat = wb.createCellStyle();    
		  	cellStyleFloat.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		  	cellStyleFloat.setBorderBottom(CellStyle.BORDER_THIN);
		  	cellStyleFloat.setBorderLeft(CellStyle.BORDER_THIN);
		  	cellStyleFloat.setBorderRight(CellStyle.BORDER_THIN);
		  	cellStyleFloat.setBorderTop(CellStyle.BORDER_THIN);
		  	
		  	//表格头部样式（加粗字体+边框）
		  	CellStyle cellStyleHeadBorder = AppModConfig.getExcellCellStyle(wb);    
		  	cellStyleHeadBorder.setBorderBottom(CellStyle.BORDER_THIN);
		  	cellStyleHeadBorder.setBorderLeft(CellStyle.BORDER_THIN);
		  	cellStyleHeadBorder.setBorderRight(CellStyle.BORDER_THIN);
		  	cellStyleHeadBorder.setBorderTop(CellStyle.BORDER_THIN);
		  	
		    //边框
		  	CellStyle styleBorder = AppModConfig.getExcellCellStyleBorder(wb);
		  	styleBorder.setWrapText(true);//先设置为自动换行   
		  	
		  	//文字水平垂直居中（表格文字）
		  	CellStyle styleVerAliCenter = AppModConfig.getExcellCellStyleBorder(wb);                  // 样式对象
	  		// 设置单元格的背景颜色为淡蓝色
		  	styleVerAliCenter.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		  	styleVerAliCenter.setVerticalAlignment(CellStyle.VERTICAL_CENTER);   // 垂直
		  	styleVerAliCenter.setAlignment(CellStyle.ALIGN_CENTER);              // 水平

	  		int columnIndex=0;
		  	sheet.setColumnWidth(columnIndex++, 100*60);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*70);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	sheet.setColumnWidth(columnIndex++, 100*200);
		  	sheet.setColumnWidth(columnIndex++, 100*200);
		  	sheet.setColumnWidth(columnIndex++, 100*50);
		  	
		  	//第一行
		  	//标题：查询结果报告
		  	String title = "查询结果报告";
		  	sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
		  	creatFullRow(sheet, startRowIdx++, colVals, style, title);
			//一、用餐日期：2018/12/06-2018/12/09
			title = "一、用餐日期："+startDate + " - " + endDate;
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			//二、关联学校：3所（含1个区）
			//放到创建学校列表内部创建
			
			//创建一行
			int statMode = 0;
			String [] schoolListColNames = {"序号","所在地", "学制", "学校", "地址", "法人代表", "联系人","联系人电话"};
			startRowIdx = creatSchoolListTable(token, pathFileName, startDate, endDate,schName,distName, db1Service, db2Service, saasService,dbHiveService,
					excelPath, fileType, sheet, startRowIdx++, colVals, style, statMode, schoolListColNames,
					styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,styleAlignLeft);
			
    		
			//三、关联学校详情
    		startRowIdx++;//和上一个表格之间留空格
			title = "三、关联学校详情";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			//获取学校详情数据
			SearchBySchoolDTO searchBySchoolDTO = searchBySchoolAppMod.appModFunc(token, schName, distName, prefCity, province, startDate, endDate, 
					 null, null, db1Service, db2Service, dbHiveService);
			
			List<SearchBySchoolDetail> schoolDetails = null;
			if(searchBySchoolDTO != null && searchBySchoolDTO.getSchoolDetails() !=null && searchBySchoolDTO.getSchoolDetails().size()>0) {
				schoolDetails = searchBySchoolDTO.getSchoolDetails();
			}
			
			if(schoolDetails != null && schoolDetails.size()>0) {
				int schoolSortNo = 0;
				//1.菜谱列表
				List<SearchDish> dishList;
				//2.学校列表
				List<SearchSch> schList;
				//3.证照信息列表
				List<SearchLicense> licenseList;
				//4.团餐公司信息列表
				List<SearchSupplier> rmcList;
				//5.供应商信息列表
				List<SearchSupplier> supplierList;
				//6.配送单明细列表
				List<SearchSupplyMatSup> supplyMatSupList;
				for(SearchBySchoolDetail schoolDetail : schoolDetails) {
					if(schoolDetail == null) {
						continue;
					}
					schoolSortNo = schoolSortNo +1;
					dishList = schoolDetail.getDishList();
					schList = schoolDetail.getSchList();
					licenseList = schoolDetail.getLicenseList();
					rmcList = schoolDetail.getRmcList();
					supplierList = schoolDetail.getSupplierList();
					supplyMatSupList = schoolDetail.getSupplyMatSupList();
					
					//1、学校1：上海市中芯实验学校
					title = (schoolSortNo) + "、学校"+schoolSortNo+"："+schoolDetail.getSchName();//---需要动态展示
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
					creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
					//-----------------循环开始
					if((userSetColumsMap ==null || userSetColumsMap.size()==0) ||
							   (userSetColumsMap != null && userSetColumsMap.size() > 0 && 
							userSetColumsMap.get("菜谱") != null && userSetColumsMap.get("菜谱").isChecked() )) {
						//①菜谱
						title = "①菜谱";
						sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
						creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
						
						//创建一行
						startRowIdx = creatDishTable(pathFileName,
								excelPath, fileType, sheet, startRowIdx++, colVals, style,null,
								styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,dishList);
					}
					
					if((userSetColumsMap ==null || userSetColumsMap.size()==0) ||
							   (userSetColumsMap != null && userSetColumsMap.size() > 0 && 
							userSetColumsMap.get("学校信息") != null && userSetColumsMap.get("学校信息").isChecked()) ) {
						//②学校信息
						startRowIdx++;//和上一个表格之间留空格
						title = "②学校信息";
						sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
						creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
						
						//列表
						statMode = 1;
						String [] schoolInfoColNames ={"序号","学校名称", "区", "地址", "统一社会信用代码证", "食品经营许可证", "主管部门","管理部门","学制","性质",
								"食品经营许可证主体","供餐模式","法人代表","联系人","联系电话"};
						startRowIdx = creatSchoolInfoTable(pathFileName,
								excelPath, fileType, sheet, startRowIdx++, colVals, style,schoolInfoColNames,
								styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,schList);
					}
					
					if((userSetColumsMap ==null || userSetColumsMap.size()==0) ||
					   (userSetColumsMap != null && userSetColumsMap.size() > 0 && 
							userSetColumsMap.get("证照信息") != null && userSetColumsMap.get("证照信息").isChecked() )) {
						//③证照信息
						startRowIdx++;//和上一个表格之间留空格
						title = "③人员证照信息：健康证"+schoolDetail.getHealthLicenseCount()+"个    A1证"+schoolDetail.getAoneLicenseCount()+"个   B证"+schoolDetail.getBlicenseCount()+"个   C证 "+schoolDetail.getClicenseCount()+"个   A2证"+schoolDetail.getAtwoLicenseCount()+"个";
						sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
						creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
						//列表
						statMode = 2;
						String [] licenseColNames ={"序号","姓名","证件类型", "证件号码", "发证日期", "有效日期", "证件状态", "关联团餐公司"};
						startRowIdx = creatLicenseListTable(pathFileName,
								excelPath, fileType, sheet, startRowIdx++, colVals, style,licenseColNames,
								styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,licenseList);
					
					}
					
					if((userSetColumsMap ==null || userSetColumsMap.size()==0) ||
							   (userSetColumsMap != null && userSetColumsMap.size() > 0 && 
									userSetColumsMap.get("团餐公司信息") != null && userSetColumsMap.get("团餐公司信息").isChecked() )) {
						//④团餐公司信息
						startRowIdx++;//和上一个表格之间留空格
						title = "④团餐公司信息";
						sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
						creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
						
						//列表
						statMode = 3;
						String [] rmcColNames ={"序号","团餐公司","服务起止时间", "统一社会信用代码证", "食品经营许可证", "食品经营许可证有效日期", "区", "地址","法人代表","联系人","联系电话"};
						startRowIdx = creatRmcListTable(pathFileName,
								excelPath, fileType, sheet, startRowIdx++, colVals, style,rmcColNames,
								styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,rmcList);
					}
					
					if((userSetColumsMap ==null || userSetColumsMap.size()==0) ||
							   (userSetColumsMap != null && userSetColumsMap.size() > 0 && 
									userSetColumsMap.get("供应商信息") != null && userSetColumsMap.get("供应商信息").isChecked() )) {
						//⑤供应商信息
						title = "⑤供应商信息";
						sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
						creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
						
						//创建一行
						statMode = 0;
						String [] supplierColNames ={"序号","供应商名称","统一社会信用代码证", "食品经营许可证", "食品经营许可证有效日期", "食品生产许可证", "食品流通许可证","地址","法人代表","联系人","联系电话"};
						startRowIdx = creatSupplierListTable(pathFileName,
								excelPath, fileType, sheet, startRowIdx++, colVals, style,supplierColNames,
								styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,supplierList);
					}
					
					if((userSetColumsMap ==null || userSetColumsMap.size()==0) ||
							   (userSetColumsMap != null && userSetColumsMap.size() > 0 && 
									userSetColumsMap.get("配送单明细") != null && userSetColumsMap.get("配送单明细").isChecked() )) {
						//⑥配送单明细
						startRowIdx++;//和上一个表格之间留空格
						title = "⑥配送单明细";
						sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
						creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
						
						//------------循环开始
						if(supplyMatSupList !=null && supplyMatSupList.size()>0) {
							String [] matColNames ={"序号","用料日期", "团餐公司", "收货日期", "验收日期", "配货批次号", "物料名称","标准名称",
									"规格","类别","数量","换算关系","换算后数量","批号","生产日期","保质期","是否验收","验收数量","配货单图片","检疫证图片"};
							
							for(SearchSupplyMatSup searchSupplyMatSup : supplyMatSupList) {
								if(searchSupplyMatSup == null) {
									continue;
								}
								startRowIdx++;//和上一个表格之间留空格
								
								//1.供应商1（2条）
								title = searchSupplyMatSup.getSupplierName()+"（"+searchSupplyMatSup.getMatSupCount()+"条）";
								sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
								creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
								
								//创建一行
								List<CaMatSupDets> caMatSupDets = searchSupplyMatSup.getCaMatSupDets();
								if(caMatSupDets !=null && caMatSupDets.size() >0) {
									startRowIdx = creatSupplyMatSupListTable(pathFileName,
											excelPath, fileType, sheet, startRowIdx++, colVals, style,matColNames,
											styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter,caMatSupDets);
								}
								
							}
							
						}
					}
					//循环结束
					//--------------------循环结束
				}
			}
			
			// 创建文件流
			OutputStream stream = null;
			try {
				stream = new FileOutputStream(excelPath);
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			if (stream != null) {
				// 写入数据
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,repFileResPath);
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				// 关闭文件流
				try {
					stream.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			} else
				retFlag = false;
        }
        
        return retFlag;
    }
	
	//创建学校
	private Integer creatSchoolListTable(String token, String pathFileName, String startDate, String endDate,String schName,String distName, Db1Service db1Service,
			Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService,  String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, int statMode, String[] areColNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,CellStyle styleAlignLeft) {
		Row row;
		Cell cell;
		SearchSchListDTO schDto = searchSchListAppMod.appModFunc(token, schName, distName, prefCity, province, null, null, db1Service, db2Service,
				 dbHiveService);
		
		//二、关联学校：3所（含1个区）
		startRowIdx++;//和上一个表格之间留空格
		String title = "二、关联学校："+0+"所（含"+0+"个区）";
		
		int rowCount = 0;
		if(schDto != null) {
			List<SearchSch> searchSchList = new ArrayList<SearchSch>();
			if(schDto !=null && schDto.getSearchSchList()!=null && schDto.getSearchSchList().size()>0) {
				searchSchList = schDto.getSearchSchList();	
			}
			
			title = "二、关联学校："+searchSchList.size()+"所（含"+schDto.getDistCount()+"个区）";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			creatTableHead(sheet, startRowIdx++, colVals, style, areColNames,cellStyleHeadBorder);
			
			// 循环写入行数据
			int columIndex = 0;
			rowCount = searchSchList.size();
			for (int i = 0; i < searchSchList.size(); i++) {
				columIndex = 0;
				
				row = (Row) sheet.createRow(i + startRowIdx);
				cell = row.createCell(columIndex++);
				cell.setCellValue(i+1); //序号
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(AppModConfig.distIdToNameMap.get(searchSchList.get(i).getDistName())); //所在地
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(searchSchList.get(i).getSchType());  //学制
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(searchSchList.get(i).getSchName());   //学校
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(searchSchList.get(i).getDetailAddr());    //地址
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(searchSchList.get(i).getLegalRep());//法人代表
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);  //联系人
				cell.setCellValue(searchSchList.get(i).getProjContact());
				cell.setCellStyle(cellStyleFloat);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(searchSchList.get(i).getPcMobilePhone());    //联系人电话
				cell.setCellStyle(styleBorder);
				
			}
		}else {
			creatTableHead(sheet, startRowIdx++, colVals, style, areColNames,cellStyleHeadBorder);
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 8));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
		}
		return startRowIdx + rowCount+1;
	}
	
	//创建学校详情
	private Integer creatSchoolInfoTable(String pathFileName, 
     String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, String[] colNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,
			List<SearchSch> schList) {
		Row row;
		Cell cell;
		creatTableHead(sheet, startRowIdx++, colVals, style, colNames,cellStyleHeadBorder);
		
		int rowCount = 0;
		if(schList != null && schList.size()>0) {
			// 循环写入行数据
			int columIndex = 0;
			rowCount = schList.size();
			for (int i = 0; i < schList.size(); i++) {
				columIndex = 0;
				
				row = (Row) sheet.createRow(i + startRowIdx);
				cell = row.createCell(columIndex++);
				cell.setCellValue(i+1); //序号
				cell.setCellStyle(styleVerAliCenter);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getSchName());   //学校
				cell.setCellStyle(styleBorder);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(AppModConfig.distIdToNameMap.get(schList.get(i).getDistName())); //区
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getDetailAddr());    //地址
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getUscc());    //统一社会信用代码证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getLicNo());    //食品经营许可证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getCompDep());    //主管部门
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getManagerDep());    //管理部门
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getSchType());  //学制
				cell.setCellStyle(styleBorder);

				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getSchProp());  //性质
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getFblMb());  //食品经营许可证主体
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getOptMode());  //供餐模式
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getLegalRep());//法人代表
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);  //联系人
				cell.setCellValue(schList.get(i).getProjContact());
				cell.setCellStyle(cellStyleFloat);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(schList.get(i).getPcMobilePhone());    //联系人电话
				cell.setCellStyle(styleBorder);
				
			}
		}
		return startRowIdx + rowCount+1;
	}
	
	//创建菜谱
	private Integer creatDishTable(String pathFileName, 
     String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, String[] colNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,
			List<SearchDish> dishList) {
		
		String [] dishColNames;
		List<String> dishColNameList = new ArrayList<String>();
		dishColNameList.add("项目点");
		dishColNameList.add("团餐公司");
		dishColNameList.add("菜单名称");
		dishColNameList.add("餐别");
		XSSFRichTextString textString = null;
		Row row;
		Cell cell;
		int rowCount = 0;
		if(dishList != null && dishList.size()>0) {
			// 循环写入行数据
			rowCount = dishList.size();
			for (int i = 0; i < dishList.size(); i++) {
				int columIndex = 0;
				if(dishList.get(i) == null) {
					continue;
				}
				
                List<SearchDateDishList> dateDiashList = dishList.get(i).getDateDiashList();
				//第一次循环拿去列值
				if(i == 0) {
					for(SearchDateDishList searchDateDishList: dateDiashList) {
						if(searchDateDishList !=null) {
							String mealName = "未供餐";
							if(searchDateDishList.getMealFlag() !=null && searchDateDishList.getMealFlag()==1) {
								mealName = "已供餐";
							}
							dishColNameList.add(searchDateDishList.getWeek()+" "+searchDateDishList.getDate()+" "+mealName);
						}
					}
					
					dishColNames = new String[dishColNameList.size()];
					dishColNames = dishColNameList.toArray(dishColNames);
					creatTableHead(sheet, startRowIdx++, colVals, style, dishColNames,cellStyleHeadBorder);
				}
				
				row = (Row) sheet.createRow(i + startRowIdx);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(dishList.get(i).getSchName());   //项目点
				cell.setCellStyle(styleBorder);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(dishList.get(i).getRmcName()); //团餐公司
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(dishList.get(i).getMenuName());    //菜单名称
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(dishList.get(i).getCaterType());    //餐别
				cell.setCellStyle(styleBorder);
				
				
				
				for(SearchDateDishList searchDateDishList: dateDiashList) {
					cell = row.createCell(columIndex++);
					String dish = "未排菜";
					if(searchDateDishList.getDishList() !=null && searchDateDishList.getDishList().size()>0 && 
							searchDateDishList.toString().indexOf("未排菜")<0) {
						String strDishList = searchDateDishList.getDishList().toString();
						dish = strDishList.substring(1,strDishList.length() -1).replace(" ", "").replaceAll(",", "\r\n");
					}
					textString = new XSSFRichTextString(dish);
					cell.setCellValue(textString);    //菜品
					cell.setCellStyle(styleBorder);
				}
			}
		}
		return startRowIdx + rowCount+1;
	}
	
	//创建证书
	private Integer creatLicenseListTable(String pathFileName, 
     String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, String[] colNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,
			List<SearchLicense> licenseList) {
		Row row;
		Cell cell;
		creatTableHead(sheet, startRowIdx++, colVals, style, colNames,cellStyleHeadBorder);
		
		int rowCount = 0;
		if(licenseList != null && licenseList.size()>0) {
			// 循环写入行数据
			int columIndex = 0;
			rowCount = licenseList.size();
			for (int i = 0; i < licenseList.size(); i++) {
				columIndex = 0;
				
				//{"序号","姓名","证件类型", "证件号码", "发证日期", "有效日期", "证件状态", "关联团餐公司"};
				row = (Row) sheet.createRow(i + startRowIdx);
				cell = row.createCell(columIndex++);
				cell.setCellValue(i+1); //序号
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(licenseList.get(i).getWrittenName());   //姓名
				cell.setCellStyle(styleBorder);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(licenseList.get(i).getLicName()); //证件类型
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(licenseList.get(i).getLicNo());    //证件号码
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(licenseList.get(i).getLicStartDate());    //发证日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(licenseList.get(i).getLicEndDate());    //有效日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue("1".equals(licenseList.get(i).getStat())?"有效":"逾期");    //证件状态
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(licenseList.get(i).getSupplierName());    //关联团餐公司
				cell.setCellStyle(styleBorder);
			}
		}
		return startRowIdx + rowCount+1;
	}

	//创建团餐公司
	private Integer creatRmcListTable(String pathFileName, 
     String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, String[] colNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,
			List<SearchSupplier> rmcList) {
		Row row;
		Cell cell;
		creatTableHead(sheet, startRowIdx++, colVals, style, colNames,cellStyleHeadBorder);
		
		int rowCount = 0;
		if(rmcList != null && rmcList.size()>0) {
			// 循环写入行数据
			int columIndex = 0;
			rowCount = rmcList.size();
			for (int i = 0; i < rmcList.size(); i++) {
				columIndex = 0;
				//rmcColNames ={"序号","团餐公司","服务起止时间", "统一社会信用代码证", "食品经营许可证", "食品经营许可证有效日期", "区", "地址","法人代表","联系人","联系电话"};
				row = (Row) sheet.createRow(i + startRowIdx);
				cell = row.createCell(columIndex++);
				cell.setCellValue(i+1); //序号
				cell.setCellStyle(styleVerAliCenter);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getSupplierName());   //团餐公司
				cell.setCellStyle(styleBorder);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getServiceDate()); //服务起止时间
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getUscc());    //统一社会信用代码证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getFblNo());    //食品经营许可证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getFblExpireDate());    //食品经营许可证有效日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(AppModConfig.distIdToNameMap.get(rmcList.get(i).getDistName()));    //区
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getDetailAddr());    //地址
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getLegalRep());//法人代表
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);  //联系人
				cell.setCellValue(rmcList.get(i).getContact());
				cell.setCellStyle(cellStyleFloat);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(rmcList.get(i).getMobilePhone());    //联系人电话
				cell.setCellStyle(styleBorder);
				
			}
		}
		return startRowIdx + rowCount+1;
	}
	
	//创建供应商
	private Integer creatSupplierListTable(String pathFileName, 
     String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, String[] colNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,
			List<SearchSupplier> supplierList) {
		Row row;
		Cell cell;
		creatTableHead(sheet, startRowIdx++, colVals, style, colNames,cellStyleHeadBorder);
		
		int rowCount = 0;
		if(supplierList != null && supplierList.size()>0) {
			// 循环写入行数据
			int columIndex = 0;
			rowCount = supplierList.size();
			for (int i = 0; i < supplierList.size(); i++) {
				columIndex = 0;
				
				//supplierColNames ={"序号","供应商名称","统一社会信用代码证", "食品经营许可证", "食品经营许可证有效日期", "食品生产许可证", "食品流通许可证","地址","法人代表","联系人","联系电话"};
				row = (Row) sheet.createRow(i + startRowIdx);
				cell = row.createCell(columIndex++);
				cell.setCellValue(i+1); //序号
				cell.setCellStyle(styleVerAliCenter);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getSupplierName());   //供应商名称
				cell.setCellStyle(styleBorder);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getUscc()); //统一社会信用代码证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getFblNo());    //食品经营许可证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getFblExpireDate());    //食品经营许可证有效日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getFplNo());    //食品生产许可证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getFcpNo());    //食品流通许可证
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getDetailAddr());    //地址
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getLegalRep());//法人代表
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);  //联系人
				cell.setCellValue(supplierList.get(i).getContact());
				cell.setCellStyle(cellStyleFloat);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(supplierList.get(i).getMobilePhone());    //联系人电话
				cell.setCellStyle(styleBorder);
				
			}
		}
		return startRowIdx + rowCount+1;
	}
	
	//创建配送单
	private Integer creatSupplyMatSupListTable(String pathFileName, 
     String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, String[] colNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter,
			List<CaMatSupDets> caMatSupDets) {
		
		Row row;
		Cell cell;
		creatTableHead(sheet, startRowIdx++, colVals, style, colNames,cellStyleHeadBorder);
		
		int rowCount = 0;
		XSSFRichTextString textString = null;
		if(caMatSupDets != null && caMatSupDets.size()>0) {
			// 循环写入行数据
			int columIndex = 0;
			rowCount = caMatSupDets.size();
			for (int i = 0; i < caMatSupDets.size(); i++) {
				if(caMatSupDets.get(i) == null ) {
					continue;
				}
				columIndex = 0;
				// matColNames ={"序号","用料日期", "团餐公司", "收货日期", "验收日期", "配货批次号", "物料名称","标准名称",
				//"规格","类别","数量","换算关系","换算后数量","批号","生产日期","保质期","是否验收","验收数量","配货单图片","检疫证图片"};
				row = (Row) sheet.createRow(i + startRowIdx);
				cell = row.createCell(columIndex++);
				cell.setCellValue(i+1); //序号
				cell.setCellStyle(styleVerAliCenter);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getMatUseDate());   //用料日期
				cell.setCellStyle(styleBorder);
					
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getRmcName()); //团餐公司
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue("");    //收货日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getAcceptDate());    //验收日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getDistrBatNumber());    //配货批次号
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getMatName());    //物料名称
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getStandardName());    //标准名称
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue("");  //规格
				cell.setCellStyle(styleBorder);

				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getMatClassify());  //类别
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getQuantity());  //数量
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getCvtRel());  //换算关系
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getCvtQuantity());//换算后数量
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);  //批号
				cell.setCellValue(caMatSupDets.get(i).getBatNumber());
				cell.setCellStyle(cellStyleFloat);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getProdDate());    //生产日期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getQaGuaPeriod());    //保质期
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(AppModConfig.acceptStatusIdToNameMap.get(caMatSupDets.get(i).getAcceptStatus()));    //是否验收
				cell.setCellStyle(styleBorder);
				
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(caMatSupDets.get(i).getAcceptNum());    //验收数量
				cell.setCellStyle(styleBorder);
				
				if(caMatSupDets.get(i).getGsBillPicUrls() !=null && caMatSupDets.get(i).getGsBillPicUrls().size()>0 ) {
					String gsBillPicUrls = "";
					for(String url:caMatSupDets.get(i).getGsBillPicUrls()) {
						gsBillPicUrls += url+"\r\n";
					}
					cell = row.createCell(columIndex++);
					textString = new XSSFRichTextString(gsBillPicUrls);
					cell.setCellValue(textString);    //配货单图片
					cell.setCellStyle(styleBorder);
				}else {
					cell = row.createCell(columIndex++);
					cell.setCellValue("-");    //配货单图片
					cell.setCellStyle(styleBorder);
				}
				if(caMatSupDets.get(i).getQaCertPicUrls() !=null && caMatSupDets.get(i).getQaCertPicUrls().size()>0 ) {
					String qaCertPicUrls = "";
					for(String url:caMatSupDets.get(i).getQaCertPicUrls()) {
						qaCertPicUrls += url+"\r\n";
					}
					
					cell = row.createCell(columIndex++);
					textString = new XSSFRichTextString(qaCertPicUrls);
					cell.setCellValue(textString);    //检疫证图片
					cell.setCellStyle(styleBorder);
				}else {
					cell = row.createCell(columIndex++);
					cell.setCellValue("-");    //检疫证图片
					cell.setCellStyle(styleBorder);
				}
				//row.setHeight(Short.parseShort(String.valueOf(maxCount*100)));
				
			}
		}
		return startRowIdx + rowCount+1;
	}
	
	private void creatTableHead(Sheet sheet, int startRowIdx, String[] colVals, CellStyle style, String[] areColNames,CellStyle cellStyleHeadBorder) {
		Row row;
		Cell cell;
		row = (Row) sheet.createRow(startRowIdx++);
		for (int i = 0; i < areColNames.length; i++) {
			cell = row.createCell(i);
			try {
				logger.info(areColNames[i] + " ");
				colVals[i] = new String(areColNames[i].getBytes(), "utf-8");
				cell.setCellValue(areColNames[i]);
				cell.setCellStyle(cellStyleHeadBorder);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}

	private void creatFullRow(Sheet sheet, int startRowIdx, String[] colVals, CellStyle style, String title) {
		Row row;
		Cell cell;
		row = (Row) sheet.createRow(startRowIdx++);
		row.createCell(0);
		cell = row.createCell(0);
		try {
			colVals[0] = new String(title.getBytes(), "utf-8");
			cell.setCellValue(title);
			cell.setCellStyle(style);
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	//导出项目点排菜详情列表模型函数
	public ExpSearchBySchoolDTO appModFunc(String token,String startDate,String endDate, String schName,
			String distName, String prefCity, String province, Db1Service db1Service,
			 Db2Service db2Service, SaasService saasService ,DbHiveService dbHiveService) {
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
			boolean flag = ExpSearchBySchoolExcel(token, pathFileName, startDate, endDate,distName,schName, db1Service, db2Service, saasService,dbHiveService);
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
