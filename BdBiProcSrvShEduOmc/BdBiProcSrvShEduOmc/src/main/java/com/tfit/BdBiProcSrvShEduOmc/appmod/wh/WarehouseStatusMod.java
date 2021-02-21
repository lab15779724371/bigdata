package com.tfit.BdBiProcSrvShEduOmc.appmod.wh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: 五级预警  已读 未读 消息 数据处理模型
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-18
 */
public class WarehouseStatusMod {

    // 是否为真实数据标识
    private static boolean isRealData = true;

    // 页号、页大小和总页数
    int curPageNum = 1, pageTotal = 1, pageSize = 20;
    ObjectMapper objectMapper = new ObjectMapper();
    // 资源路径
    //String fileResPath = "/amSaveUserInfo/";

    public String appModFunc(HttpServletRequest request,Integer page,Integer pageSize, Integer readsStatus,Db1Service db1Service, Db2Service db2Service) {
        // 固定Dto层
        AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
        AppCommonData appCommonData = new AppCommonData();
        List<AppCommonDao> sourceDao = null;
        AppCommonDao pageTotal = null;
        List<LinkedHashMap<String, Object>> dataList = new ArrayList();
        LinkedHashMap<String, Object> data =new LinkedHashMap<String, Object>();
        // 业务操作
        try {
            //授权码
            String token =request.getHeader("Authorization");
            //验证授权
            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
            if (verTokenFlag) {
                //分页
                if (page != null) {
                    this.curPageNum = page;
                }
                if (pageSize != null) {
                    this.pageSize = pageSize;
                }
                Integer startNum = (curPageNum - 1) * (this.pageSize);

                TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
                String userAccount = userINfo.getUserAccount();

                sourceDao=db2Service.getWarnTitleByReadStatus(readsStatus,userAccount,null);
                List<AppCommonDao> resultDao=sourceDao.subList(startNum, sourceDao.size() >(startNum + pageSize)?(startNum + pageSize):sourceDao.size());
                for(int i=0;i<resultDao.size();i++) {
                    dataList.add(resultDao.get(i).getCommonMap());
                }

                data.put("pageTotal", sourceDao.size());
                data.put("curPageNum", curPageNum);

                appCommonData.setData(data);
                appCommonData.setDataList(dataList);
                appCommonExternalModulesDto.setData(appCommonData);
                // 以上业务逻辑层修改
                // 固定返回
                appCommonExternalModulesDto.setResCode(IOTRspType.Success.getCode().toString());
                appCommonExternalModulesDto.setResMsg("查询成功");
            } else {
                appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {
            appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
            appCommonExternalModulesDto.setResMsg(e.getMessage());
        }

        String strResp = null;
        try {
            strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
            strResp = new ToolUtil().rmExternalStructure(strResp,"dataList");
        } catch (Exception e) {
            strResp = new ToolUtil().getInitJson();
        }
        return strResp;
    }



    public String getWarnListByTitleId(HttpServletRequest request,String titleId,Integer page,Integer pageSize, Db1Service db1Service, Db2Service db2Service) {
        // 固定Dto层
        AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
        AppCommonData appCommonData = new AppCommonData();
        List<AppCommonDao> sourceDao = null;
        AppCommonDao pageTotal = null;
        List<LinkedHashMap<String, Object>> dataList = new ArrayList();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        // 业务操作
        try {
            //授权码
            String token = request.getHeader("Authorization");
            //验证授权
            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);
            if (verTokenFlag) {
                //分页                                                                                                  
                if (page != null) {
                    this.curPageNum = page;
                }
                if (pageSize != null) {
                    this.pageSize = pageSize;
                }
                Integer startNum = (curPageNum - 1) * (this.pageSize);

                TEduBdUserDo userINfo = db2Service.getBdUserInfoByToken(token);
                sourceDao = db2Service.getWarnListByTitleId(titleId);
                List<AppCommonDao> resultDao=sourceDao.subList(startNum, sourceDao.size() >(startNum + pageSize)?(startNum + pageSize):sourceDao.size());
                for (int i = 0; i < resultDao.size(); i++) {
                    dataList.add(resultDao.get(i).getCommonMap());
                }

                data.put("pageTotal", sourceDao.size());
                data.put("curPageNum", curPageNum);
                
                String templateContent = "";
                List<RbUlAttachment> amInfos = new ArrayList<>();
                List<AppCommonDao> appCommonDaoList =  db2Service.getWarnTitleById(titleId);
                if(appCommonDaoList != null && appCommonDaoList.size() > 0 && appCommonDaoList.get(0) !=null) {
                	byte[] annCont = appCommonDaoList.get(0).getCommonMap().get("annCont")==null?null:(byte[])appCommonDaoList.get(0).getCommonMap().get("annCont");
                	if(annCont != null)
                		templateContent = new String(annCont, 0, annCont.length);                  //公告内容
                	
                	//附件信息
            		if(appCommonDaoList.get(0).getCommonMap().get("amInfo") != null) {
            			String[] strAmInfos = appCommonDaoList.get(0).getCommonMap().get("amInfo").toString().split(",");
            			if(strAmInfos.length > 0 && strAmInfos.length%2 == 0) {
            				for(int i = 0; i < strAmInfos.length/2; i++) {
            					RbUlAttachment rua = new RbUlAttachment();
            					rua.setAmName(strAmInfos[2*i]);
            					rua.setAmUrl(SpringConfig.repfile_srvdn+strAmInfos[2*i+1]);
            					amInfos.add(rua);
            				}
            			}
            		}
                }
                
                
                
                data.put("templateContent", templateContent);
                data.put("amInfos", amInfos);

                appCommonData.setData(data);
                appCommonData.setDataList(dataList);
                appCommonExternalModulesDto.setData(appCommonData);
                // 以上业务逻辑层修改
                // 固定返回
                appCommonExternalModulesDto.setResCode(IOTRspType.Success.getCode().toString());
                appCommonExternalModulesDto.setResMsg("查询成功");
            } else {
                appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {
            appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
            appCommonExternalModulesDto.setResMsg(e.getMessage());
        }

        String strResp = null;
        try {
            strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
            strResp = new ToolUtil().rmExternalStructure(strResp, "dataList");
        } catch (Exception e) {
            strResp = new ToolUtil().getInitJson();
        }
        return strResp;
    }
}
