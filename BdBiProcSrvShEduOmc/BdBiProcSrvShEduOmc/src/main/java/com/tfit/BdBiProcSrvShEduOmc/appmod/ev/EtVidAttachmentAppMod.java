package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.tfit.BdBiProcSrvShEduOmc.client.UUIDUtil;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidAttachment;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidAttachmentDTO;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FileWRCommSys;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;

//视频附件上传应用模型
public class EtVidAttachmentAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidAttachmentAppMod.class.getName());
	
	//文件资源路径
	String fileResPath = "/etVidAttachment/";
	
	//视频附件上传应用模型函数
  	public EtVidAttachmentDTO appModFunc(String fileName, HttpServletRequest request) {
  		EtVidAttachmentDTO evaDto = null;
  		boolean flag = false;
  		//按参数形式处理
  		if(request != null) {
  			String pubFileName = "", filePathName = "", fileType = "", amUrl = null, orginFileName = "";
  			try {
  				StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
  	            Iterator<String> iterator = req.getFileNames();  	            
  	            while (iterator.hasNext())  {
  	            	MultipartFile file = req.getFile(iterator.next());
  	            	byte[] contbuf = IOUtils.toByteArray(file.getInputStream());
  	            	orginFileName = file.getName();
  	            	int idx = orginFileName.lastIndexOf(".");
  	            	if(idx != -1) {
  	            		fileType = orginFileName.substring(idx+1);
  	            	}
  	            	pubFileName = UUIDUtil.getMD5(Base64.encodeBase64String(contbuf)+BCDTimeUtil.convertBCDFrom(null), false) + "." + fileType;
  	            	filePathName = SpringConfig.base_dir + fileResPath + pubFileName;
  	            	logger.info("视频附件名：" + filePathName + "，附件大小：" + contbuf.length);
  	            	//AppModConfig.WriteBinaryFile(contbuf, null, filePathName);
  	            	FileWRCommSys.WriteBinaryFile(Base64.decodeBase64(contbuf), null, filePathName);
  	            	//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(filePathName, SpringConfig.tomcatSrvDirs[0] + fileResPath);					
  	            	amUrl = SpringConfig.video_srvdn + fileResPath + pubFileName;
  	            	flag = true;
  	            	
  	            	//FtpUtil.ftpPicServer(filePathName, Base64.decodeBase64(contbuf));
  	            	break;
  	            }
  	            //保存文件
  	            if(flag) {
  	            	
  	            }
  	        }
  	        catch (Exception e){
  	            e.printStackTrace();
  	        }
  			//设置响应数据
  			if(flag) {
  				evaDto = new EtVidAttachmentDTO();
  				evaDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  				EtVidAttachment etVidAttachment = new EtVidAttachment();
  				etVidAttachment.setAmName(orginFileName);
  				etVidAttachment.setAmUrl(amUrl);
  				evaDto.setEtVidAttachment(etVidAttachment);
  				evaDto.setMsgId(AppModConfig.msgId);
  				AppModConfig.msgId++;
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return evaDto;
  	}
}
