package com.tfit.BdBiProcSrvShEduOmc.appmod.assess;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessData;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessListDataDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.ExpAssessDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveAssessService;
import com.tfit.BdBiProcSrvShEduOmc.util.DictConvertUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;


/**
 * @Description: 导出考核评价列表
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-14
 */
public class ExpAssessListMod {
    private static final Logger logger = LogManager.getLogger(ExpAssessListMod.class.getName());

    //项目点排菜详情列表应用模型
    //private PpDishUseDetsAppMod epddAppMod = new PpDishUseDetsAppMod();
    //考核评价数据应用模型
    private AssessListMod assessListMod = new AssessListMod();

    //是否为真实数据标识
    private static boolean isRealData = true;
    //页号、页大小和总页数
    int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
    //报表文件资源路径
    String repFileResPath = "/expAssessList/";
    //导出列名数组
   /* String[] colNames = {"序号","日期", "所在地","管理部门","项目点名称","学制", "办学性质", "团餐公司", "供餐", "不供餐原因", "排菜", "用料确认",
            "指派", "配送", "验收", "留样"};*/

    String[] colNames = {"序号","考评周期", "学校名称","所在地","管理部门","考评得分"};
    //变量数据初始化
    String startDate = "2019-12-02";
    String endDate = "2019-12-08";
    String area = null;
    String managementDepartment = null;
    int sendFlag = -1;
    String expFileUrl = "test1.txt";

    //模拟数据函数
    private ApiResponse<ExpAssessDTO> SimuDataFunc() {
        //列表元素设置
        ExpAssessDTO expAssessDTO = new ExpAssessDTO();
        //赋值
        expAssessDTO.setStartDate(startDate);
        expAssessDTO.setEndDate(endDate);
        expAssessDTO.setArea(area);
        expAssessDTO.setManagementDepartment(managementDepartment);
        expAssessDTO.setSendFlag(sendFlag);
        expAssessDTO.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
        return new ApiResponse<>(expAssessDTO);
    }

    //生成导出EXCEL文件
    public boolean expAssessExcel(String pathFileName, List<AssessData> result, String colNames[]) {
        boolean retFlag = true;
        Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        Sheet sheet = null;
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        if(result!=null && result.size() > AppModConfig.maxPageSize) {
            logger.info("导出记录数超限：" + result.size());
            return false;
        }
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
//                OutputStream outputStream = null;
//                try {
//                    outputStream = new FileOutputStream(excelPath);
//                } catch (FileNotFoundException e) {
//                    logger.info("导出出现异常：" +e.getMessage());
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//                if(outputStream != null) {
//                    try {
//                        wb.write(outputStream);
//                    } catch (IOException e) {
//                        logger.info("导出出现异常：" +e.getMessage());
//                        // TODO 自动生成的 catch 块
//                        e.printStackTrace();
//                    }
//                    try {
//                        outputStream.flush();
//                    } catch (IOException e) {
//                        logger.info("导出出现异常：" +e.getMessage());
//                        // TODO 自动生成的 catch 块
//                        e.printStackTrace();
//                    }
//                    try {
//                        outputStream.close();
//                    } catch (IOException e) {
//                        // TODO 自动生成的 catch 块
//                        e.printStackTrace();
//                    }
//                }
//                else
//                    retFlag = false;
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
            int startRowIdx = 0;
            int startColumnIdx = 0;
            String[] colVals = new String[colNames.length];
            // 添加样式
            Row row = null;
            Cell cell = null;
            // 创建第一行
            row = (Row) sheet.createRow(startRowIdx);
            //获取excell单元风格
            CellStyle style = AppModConfig.getExcellCellStyle(wb);
            for (int i = 0; i < colNames.length; i++) {
                cell = row.createCell(i);
                try {
                    logger.info(colNames[i] + " ");
                    colVals[i] = new String(colNames[i].getBytes(), "utf-8");
                    cell.setCellValue(colNames[i]);
                    cell.setCellStyle(style);
                } catch (UnsupportedEncodingException e) {
                    logger.info("导出出现异常：" +e.getMessage());
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }

            logger.info("导出开始写入数据..." );
           // 循环写入行数据
            startRowIdx++;
            for (int i = 0; i < result.size(); i++) {
                startColumnIdx = 0;
                row = (Row) sheet.createRow(i + startRowIdx);row.createCell(startColumnIdx++).setCellValue(i+1);
                row.createCell(startColumnIdx++).setCellValue(result.get(i).getEvaluatePeriod());   //考评周期
                row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolName());    //学校名称
                //row.createCell(startColumnIdx++).setCellValue( result.get(i).getArea());         //所在地
                row.createCell(startColumnIdx++).setCellValue( AppModConfig.distIdToNameMap.get(result.get(i).getArea()));         //所在地
                //row.createCell(startColumnIdx++).setCellValue( result.get(i).getManagementDepartment());   //管理部门
                row.createCell(startColumnIdx++).setCellValue( DictConvertUtil.mapToOrgName(result.get(i).getManagementDepartment()) );   //管理部门
                //row.createCell(startColumnIdx++).setCellValue(result.get(i).getComprehensiveEvaluationResults()); //考评得分
                row.createCell(startColumnIdx++).setCellValue(new BigDecimal(result.get(i).getComprehensiveEvaluationResults()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
               }

            // 创建文件流
//            OutputStream stream = null;
//            try {
//                stream = new FileOutputStream(excelPath);
//            } catch (FileNotFoundException e) {
//                logger.info("导出出现异常：" +e.getMessage());
//                // TODO 自动生成的 catch 块
//                e.printStackTrace();
//            }
//
//            if (stream != null) {
                // 写入数据
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    wb.write(os);
                    FtpUtil.ftpServer(pathFileName, os,repFileResPath);
                } catch (IOException e) {
                    logger.info("导出出现异常：" +e.getMessage());
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
                // 关闭文件流
//                try {
//                    stream.close();
//                } catch (IOException e) {
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//            } else
//                retFlag = false;
        }

        return retFlag;
    }

    //导出考核评价列表模型函数
    public ApiResponse<ExpAssessDTO> appModFunc(HttpServletRequest request, AssessData assessData, Db2Service db2Service, DbHiveAssessService dbHiveAssessService) {
        ExpAssessDTO expAssessDTO = new ExpAssessDTO();

        if (isRealData) { // 真实数据
            //assessData.setPage("-1");
            //assessData.setPageSize("-1");
            AssessListDataDTO assessListDataDTO = assessListMod.expAssessModFunc( request,  dbHiveAssessService,  db2Service);
          /*  PpDishUseDetsDTO pddDto = epddAppMod.appModFunc(token, ppDishDets,
                    db1Service, db2Service, saasService, dbHiveDishService);*/
            //AssessListDataDTO assessListDataDTO = new AssessListDataDTO();
            if(assessListDataDTO != null) {
                List<AssessData> expExcelList = assessListDataDTO.getAssessListData();
                //生成导出EXCEL文件
                String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
                String pathFileName = SpringConfig.base_dir + repFileName;

                logger.info("导出文件路径：" + pathFileName);
                if(expExcelList ==null) {
                    expExcelList = new ArrayList<>();
                }
                logger.info("导出记录数：" + expExcelList.size());
                boolean flag = expAssessExcel(pathFileName, expExcelList, colNames);
                if(flag) {
                    //移动文件到其他目录
                    //AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
                    //导出信息
//                    expAssessDTO.setStartDate(startDate);
//                    expAssessDTO.setEndDate(endDate);
//                    expAssessDTO.setArea(area);
//                    expAssessDTO.setManagementDepartment(managementDepartment);
                    expAssessDTO.setSendFlag(sendFlag);

                    expAssessDTO.setStartDate(request.getParameter("startDateAssess"));
                    expAssessDTO.setEndDate(request.getParameter("endDateAssess"));
                    expAssessDTO.setArea(request.getParameter("area"));
                    expAssessDTO.setManagementDepartment(request.getParameter("managementDepartment"));
                    expFileUrl = SpringConfig.repfile_srvdn + repFileName;
                    logger.info("导出文件URL：" + expFileUrl);
                    expAssessDTO.setExpFileUrl(expFileUrl);
                }
            }
        } else { // 模拟数据
            // 模拟数据函数
            return SimuDataFunc();
    }
        return new ApiResponse<>(expAssessDTO);
 }
}
