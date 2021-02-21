package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmSaveUserInfoAppMod;

public class FileWRCommSys {
	private static final Logger logger = LogManager.getLogger(AmSaveUserInfoAppMod.class.getName());
	
	//写Json文件
    public static void WriteBinaryFile(String strJson, String strFileName)
    {
    	String strNmJson = "";  
        try  
        {  
        	DataOutputStream outfileinfo = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(strFileName)));  
            strNmJson = JsonFormatTool.formatJson(strJson);
            outfileinfo.write(strNmJson.getBytes());
            outfileinfo.close();
        } 
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }
    
    //写二进制文件
  	public static void WriteBinaryFile(byte[] FileCont, String strFileInfo, String strFileName)
    {
    	String fileName = strFileName, strNmJson = "";  
        try  
        {  
        	if(FileCont != null) {
        		logger.info("文件长度: " + FileCont.length + " 字节");
        		//保存日志文件
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));  
                out.write(FileCont);
                out.close();
                
                logger.info("*************ftp开始");
                //ByteArrayOutputStream os = new ByteArrayOutputStream();
                //os.write(strNmJson.getBytes(), 0, 0);
                //os.writeTo(outStream);
                FtpUtil.ftpPicServer(strFileName, FileCont);
                logger.info("*************ftp结束");
        	}
        	if(strFileInfo == null || strFileInfo.isEmpty())
        		return ;
            //保存日志文件信息
            int idx = fileName.lastIndexOf(".");
            String strLogFileInfoName = "";
            if(idx != -1)
            	strLogFileInfoName = fileName.substring(0, idx) + ".json";
            else
            	strLogFileInfoName = fileName + ".json";
            FileOutputStream outStream = new FileOutputStream(strLogFileInfoName);
            DataOutputStream outfileinfo = new DataOutputStream(new BufferedOutputStream(outStream));  
            strNmJson = JsonFormatTool.formatJson(strFileInfo);
            outfileinfo.write(strNmJson.getBytes());
            outfileinfo.close();
            
           
            
        } 
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }
    
    //按行读取文件
    public static List<String> ReadFileByRow(String filePathName) {
    	List<String> strLineList = null;
    	// 读取文件内容
		InputStreamReader isr = null;
		try {
			try {
				isr = new InputStreamReader(new FileInputStream(filePathName), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		
		if (isr != null) {
			BufferedReader br = new BufferedReader(isr);
			String str = null;
			strLineList = new ArrayList<>();
			try {
				while ((str = br.readLine()) != null) {
					strLineList.add(str);
				}
				br.close();
				isr.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
    	
    	return strLineList;
    }
    
    // 读取二进制文件
  	public static byte[] ReadBinaryFile(String filePath) throws IOException {
  		InputStream in = null;
  		BufferedInputStream buffer = null;
  		DataInputStream dataIn = null;
  		ByteArrayOutputStream bos = null;
  		DataOutputStream dos = null;
  		byte[] bArray = null;
  		File file = new File(filePath);
 		if (file.exists()) {
 			try {
 				in = new FileInputStream(filePath);
 				buffer = new BufferedInputStream(in);
 				dataIn = new DataInputStream(buffer);
 				bos = new ByteArrayOutputStream();
 				dos = new DataOutputStream(bos);
 				byte[] buf = new byte[2048];
 				while (true) {
 					int len = dataIn.read(buf);
 					if (len < 0)
 						break;
 					dos.write(buf, 0, len);
 				}
 				bArray = bos.toByteArray();

 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 				return null;

 			} finally {
 				if (in != null)
 					in.close();
 				if (dataIn != null)
 					dataIn.close();
 				if (buffer != null)
 					buffer.close();
 				if (bos != null)
 					bos.close();
 				if (dos != null)
 					dos.close();
 			}
 		}

  		return bArray;
  	}
}
