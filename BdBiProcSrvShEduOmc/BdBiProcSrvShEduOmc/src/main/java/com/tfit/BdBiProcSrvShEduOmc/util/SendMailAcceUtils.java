package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.mail.util.MailSSLSocketFactory;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ms.SendTestMailAppMod;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;

/**
 * 发送邮件工具类
 * 
 * @author tp
 *
 */
public class SendMailAcceUtils {
	private static final Logger logger = LogManager.getLogger(SendTestMailAppMod.class.getName());
	
	/**
     * 发送带附件的邮件
     * 
     * @param receive
     *            收件人
     * @param subject
     *            邮件主题
     * @param msg
     *            邮件内容
     * @param filename
     *            附件地址
     * @param fileType 0：本地 文件1：url方式   默认是0：本地文件
     *           附件类型
     * @return
     * @throws GeneralSecurityException
     */
    public static boolean sendMail(TEduBdMailSrvDo tebmsDo, String mailTitle, String mailCont, String[] amFileNames, String[] amOutNames,
    		String[] rcvMailUsers, boolean isShowDebug, int txtFrmType,String fileType) {
        boolean retFlag = false;
        // 发件人电子邮箱
        String from = tebmsDo.getEmail();
        if(from == null)
        	return retFlag;
        // 发件人电子邮箱密码
        String pass = tebmsDo.getPassword();
        // 指定发送邮件的主机为 smtp.qq.com
        String host = tebmsDo.getSendServer();                   // 邮件服务器
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        //设置端口号
        properties.put("mail.smtp.port", String.valueOf(tebmsDo.getSendSrvPortNo()));                                         // 端口号
        //设置授权使能
        properties.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		if(sf == null)
			return retFlag;
        sf.setTrustAllHosts(true);
        //设置加密方式
        if(tebmsDo.getSendSrvPort() != null) {
        	if(tebmsDo.getSendSrvPort() == 0) {
        		properties.put("mail.smtp.ssl.enable", "true");                           // 设置是否使用ssl安全连接 ---一般都使用
        		properties.put("mail.smtp.ssl.socketFactory", sf);
        	}
        	else if(tebmsDo.getSendSrvPort() == 1) {
        		properties.put("mail.smtp.starttls.enable", "true");                      // 设置是否使用starttls安全连接 ---一般都使用
        		properties.put("mail.smtp.starttls.socketFactory", sf);
        	}
        }
        else {
        	properties.put("mail.smtp.ssl.enable", "true");     
        	properties.put("mail.smtp.ssl.socketFactory", sf);
        }        
        if(isShowDebug)
        	properties.put("mail.debug", "true");
        // 获取默认session对象
        Session session = Session.getInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {        // qq邮箱服务器账户、第三方登录授权码
                return new PasswordAuthentication(from, pass);                 // 发件人邮件用户名、密码
            }
        });
        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));
            // Set To: 头部头字段
            // 设置收件人邮箱地址 
	        if(rcvMailUsers == null)
	        	message.setRecipient(Message.RecipientType.TO, new InternetAddress(tebmsDo.getEmail()));   //一个收件人
	        else {
	        	if(rcvMailUsers.length > 0) {
	        		InternetAddress[] ias = new InternetAddress[rcvMailUsers.length];
	        		for(int i = 0; i < rcvMailUsers.length; i++) {
	        			ias[i] = new InternetAddress(rcvMailUsers[i]);
	        		}
	        		message.setRecipients(Message.RecipientType.TO, ias);    //多个收件人
	        	}
	        	else
	        		message.setRecipient(Message.RecipientType.TO, new InternetAddress(tebmsDo.getEmail()));   //一个收件人
	        }
            // Set Subject: 主题文字
	        if(mailTitle != null)
	        	message.setSubject(mailTitle);
            // 创建消息部分
            BodyPart messageBodyPart = new MimeBodyPart();
            // 消息
            if(mailCont == null)
            	mailCont = "";
            if(txtFrmType == 0) {    //纯文本格式
            	messageBodyPart.setText(mailCont);
            }
            else if(txtFrmType == 1) {   //html格式
            	messageBodyPart.setContent(mailCont, "text/html; charset=utf-8");
            }
            // 创建多重消息
            Multipart multipart = new MimeMultipart();
            // 设置文本消息部分
            multipart.addBodyPart(messageBodyPart);
            //发送附件部分
            if(amFileNames != null) {
            	if(amFileNames.length > 0) {
            		for(int i = 0; i < amFileNames.length; i++) {
            			if(amFileNames[i] == null)
            				continue ;
            			// 附件部分
            			messageBodyPart = new MimeBodyPart();
            			// 设置要发送附件的文件路径
            			logger.info("amFileNames[" + i + "] = " + amFileNames[i]);
            			if(CommonUtil.isNotEmpty(fileType) && "1".equals(fileType)) {
							try {
								URL url = new URL(amFileNames[i]);
								DataSource source=new URLDataSource(url);
	            				messageBodyPart.setDataHandler(new DataHandler(source));
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
            			}else {
            				DataSource source = new FileDataSource(amFileNames[i]);
            				messageBodyPart.setDataHandler(new DataHandler(source));
            			}
            			
            			// 处理附件名称中文（附带文件路径）乱码问题
            			int idx = amFileNames[i].lastIndexOf("/");
            			String fileName = amFileNames[i];
            			if(amOutNames == null) {
            				if(idx != -1) {
            					fileName = amFileNames[i].substring(idx+1, amFileNames[i].length());
            					
            				}
            			}
            			else {
            				if(amOutNames.length == amFileNames.length) {
            					fileName = amOutNames[i];            					
            				}
            				else {
            					if(idx != -1) {
                					fileName = amFileNames[i].substring(idx+1, amFileNames[i].length());                					
                				}
            				}
            			}
            			logger.info("发送附件文件" + (i+1) + "：" + fileName);
            			messageBodyPart.setFileName(MimeUtility.encodeText(fileName));
            			multipart.addBodyPart(messageBodyPart);
            		}
            	}
            }
            // 发送完整消息
            message.setContent(multipart);
            // 发送消息
            Transport.send(message);
            retFlag = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return retFlag;
    }
    
    
	//发送邮件
	public static boolean sendMail(TEduBdMailSrvDo tebmsDo, String mailTitle, String mailCont, boolean isShowDebug) {
		boolean retFlag = false;
        Properties properties = new Properties();
        String strInfo = "", strVal = "";
        properties.put("mail.transport.protocol", "smtp");                                // 连接协议
        strVal = tebmsDo.getSendServer();
        strInfo += ("发件主机名：" + strVal + "\n");
        properties.put("mail.smtp.host", strVal);                                         // 主机名
        strVal = String.valueOf(tebmsDo.getSendSrvPortNo());
        strInfo += ("端口号：" + strVal + "\n");
        properties.put("mail.smtp.port", strVal);                                         // 端口号
        properties.put("mail.smtp.auth", "true");
        if(tebmsDo.getSendSrvPort() != null) {
        	if(tebmsDo.getSendSrvPort() == 0) {
        		strInfo += ("端口：" + "SSL" + "\n");
        		properties.put("mail.smtp.ssl.enable", "true");                           // 设置是否使用ssl安全连接 ---一般都使用
        	}
        	else if(tebmsDo.getSendSrvPort() == 1) {
        		strInfo += ("端口：" + "STARTTLS" + "\n");
        		properties.put("mail.smtp.starttls.enable", "true");                      // 设置是否使用starttls安全连接 ---一般都使用
        	}
        }
        else
        	properties.put("mail.smtp.ssl.enable", "true");                               // 设置是否使用ssl安全连接 ---一般都使用
        if(isShowDebug)
        	properties.put("mail.debug", "true");                                         // 设置是否显示debug信息 true 会在控制台显示相关信息
        logger.info(strInfo);
        // 得到回话对象
        Session session = Session.getInstance(properties);
        // 获取邮件对象
        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        
       
        
        try {
        	
        	 
			message.setFrom(new InternetAddress(tebmsDo.getEmail()));
			// 设置收件人邮箱地址 
	        //message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(tebmsDo.getEmail()), new InternetAddress("xxx@qq.com"), new InternetAddress("xxx@qq.com")});   //多个收件人
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(tebmsDo.getEmail()));//一个收件人
	        // 设置邮件标题
	        message.setSubject(mailTitle);
	        // 设置邮件内容
	        message.setText(mailCont);
	        // 得到邮差对象
	        Transport transport = null;
			try {
				transport = session.getTransport();
				// 连接自己的邮箱账户
		        try {
		        	transport.connect(tebmsDo.getEmail(), tebmsDo.getPassword());
		        	// 发送邮件
			        transport.sendMessage(message, message.getAllRecipients());
			        transport.close();
			        retFlag = true;
				} catch (MessagingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
			} catch (NoSuchProviderException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		} catch (AddressException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}        
        
        return retFlag;
    }
	
	//发送邮件
	public static boolean sendMailByHtml(TEduBdMailSrvDo tebmsDo, String mailTitle, String mailCont,String email, boolean isShowDebug) {
		boolean retFlag = false;
        Properties properties = new Properties();
        String strInfo = "", strVal = "";
        properties.put("mail.transport.protocol", "smtp");                                // 连接协议
        strVal = tebmsDo.getSendServer();
        strInfo += ("发件主机名：" + strVal + "\n");
        properties.put("mail.smtp.host", strVal);                                         // 主机名
        strVal = String.valueOf(tebmsDo.getSendSrvPortNo());
        strInfo += ("端口号：" + strVal + "\n");
        properties.put("mail.smtp.port", strVal);                                         // 端口号
        properties.put("mail.smtp.auth", "true");
        if(tebmsDo.getSendSrvPort() != null) {
        	if(tebmsDo.getSendSrvPort() == 0) {
        		strInfo += ("端口：" + "SSL" + "\n");
        		properties.put("mail.smtp.ssl.enable", "true");                           // 设置是否使用ssl安全连接 ---一般都使用
        	}
        	else if(tebmsDo.getSendSrvPort() == 1) {
        		strInfo += ("端口：" + "STARTTLS" + "\n");
        		properties.put("mail.smtp.starttls.enable", "true");                      // 设置是否使用starttls安全连接 ---一般都使用
        	}
        }
        else
        	properties.put("mail.smtp.ssl.enable", "true");                               // 设置是否使用ssl安全连接 ---一般都使用
        if(isShowDebug)
        	properties.put("mail.debug", "true");                                         // 设置是否显示debug信息 true 会在控制台显示相关信息
        logger.info(strInfo);
        // 得到回话对象
        Session session = Session.getInstance(properties);
        // 获取邮件对象
        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        
        try {
			message.setFrom(new InternetAddress(tebmsDo.getEmail()));
			// 设置收件人邮箱地址 
	        //message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(tebmsDo.getEmail()), new InternetAddress("xiefengyangcn@qq.com"), new InternetAddress("fengyang_xie@ssic.cn")});   //多个收件人
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));//一个收件人
	        // 设置邮件标题
	        message.setSubject(mailTitle);
	        // 设置邮件内容
	        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
	        Multipart mainPart = new MimeMultipart();
	        // 创建一个包含HTML内容的MimeBodyPart
	        BodyPart html = new MimeBodyPart();
	        // 设置HTML内容
	        html.setContent(mailCont, "text/html; charset=utf-8");
	        mainPart.addBodyPart(html);
	        // 将MiniMultipart对象设置为邮件内容
	        message.setContent(mainPart);
	        // 得到邮差对象
	        Transport transport = null;
			try {
				transport = session.getTransport();
				// 连接自己的邮箱账户
		        try {
		        	transport.connect(tebmsDo.getEmail(), tebmsDo.getPassword());
		        	// 发送邮件
			        transport.sendMessage(message, message.getAllRecipients());
			        transport.close();
			        retFlag = true;
				} catch (MessagingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
			} catch (NoSuchProviderException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		} catch (AddressException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}        
        
        return retFlag;
    }
}