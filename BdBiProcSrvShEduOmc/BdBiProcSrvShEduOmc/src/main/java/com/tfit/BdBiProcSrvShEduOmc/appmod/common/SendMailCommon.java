package com.tfit.BdBiProcSrvShEduOmc.appmod.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.appmod.ms.SendTestMailAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.DishEmailDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SendMailAcceUtils;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

/**
 * 发送邮件工具类
 * 
 * @author tp
 *
 */
public class SendMailCommon {
	private static final Logger logger = LogManager.getLogger(SendTestMailAppMod.class.getName());
	
  	/**
  	 * 五级预警-未排菜预警
  	 * @param db2Service 
  	 * @param mailTitle 邮件标题
  	 * @param email 邮件
  	 * @param closingDate 截止日期
  	 * @param dishDate 排菜日期
  	 * @param departmentName (全市或者管理部门)
  	 * @param dataList 未排菜数据列表
  	 * @param allWarnAccount 预警总数 
  	 * @return
  	 */
	public static boolean sendNoDishMail(Db2Service db2Service, 
			String mailTitle,String email,String closingDate,String dishDate,String departmentName,Map<String,List<DishEmailDto>> dataMap,int allWarnAccount) {
		String mailCont = "";
		if(CommonUtil.isEmpty(mailTitle))
			mailTitle = "未排菜预警";
		int sortNo = 1;
		if(dataMap !=null && dataMap.size() >0) {
			StringBuilder content = new StringBuilder("<html><head></head><body>");
			//table样式
			content.append("<style type=\"text/css\">\r\n" + 
					" \r\n" + 
					"                table{\r\n" + 
					"			border-collapse: collapse;\r\n" + 
					"		}\r\n" + 
					"		table,th,td{\r\n" + 
					"			border:1px,solid;\r\n" + 
					"		}\r\n" + 
					" \r\n" + 
					"</style>");
			//邮件第一部分
			content.append("<div id=\"u23776\" class=\"ax_default label\">\r\n" + 
					"              <div id=\"u23776_div\" class=\"\"></div>\r\n" + 
					"              <div id=\"u23776_text\" class=\"text \">\r\n" + 
					"                <p><span>尊敬的用户您好！</span></p><p><span>&nbsp;&nbsp; &nbsp;&nbsp; </span>"
					+ "<span style=\"color:#1E1E1E;\">截止</span><span style=\"color:#0000FF;\">"+closingDate+"，</span><span>供餐日期</span>"
							+ "<span style=\"color:#0000FF;\">#"+dishDate+"#</span><span>，</span><span style=\"color:#0000FF;\">"+departmentName+"</span>"
							+ "<span>排菜未上报学校</span><span style=\"color:#0000FF;\">"+allWarnAccount+"</span><span>所，名单如下：</span></p>" + 
					"              </div>" + 
					"            </div>" + 
					""); 
			//table部分
			content.append("&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;");
			content.append("<table  border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size=14px;color:#0000FF;\">");
			content.append("<tr style=\"color:black;\"><th>序号</th><th>管理部门</th><th>学制</th><th>学校名称</th><th>未排菜日期</th><th>截止时间</th></tr>");
			int index = 0;
			List<DishEmailDto> dishList = new ArrayList<>();
			SortList<DishEmailDto> sortList = new SortList<DishEmailDto>(); 
			for(Map.Entry<String, List<DishEmailDto>> entry : dataMap.entrySet()) {
				if(entry.getValue()==null || entry.getValue().size() ==0) {
					continue;
				}
				index = 0;
				//按学制排序
				dishList = entry.getValue();
				if(dishList == null) {
					dishList = new ArrayList<>();
				}
		    	 
		    	sortList.Sort(dishList, "getSchTypeId", "asc", "Integer");
		    	
				for (DishEmailDto data : dishList) {
					content.append("<tr align='center' style=\"\" >");
					content.append("<td>" + sortNo++ + "</td>"); //序号
					if("全市".equals(departmentName)) {
						//合并行
						if(index == 0) {
							content.append("<td rowspan=\""+dishList.size()+"\">" + entry.getKey()+"("+entry.getValue().size()+"所)" + "</td>"); //学制
							index ++;
						}
					}else {
						//不合并行
						content.append("<td>" + entry.getKey() + "</td>"); //管理部门
					}
					content.append("<td>" + data.getSchType() + "</td>"); //学制
					content.append("<td>" + data.getSchName() + "</td>"); //学校名称
					content.append("<td>" + data.getNoDishDate() + "</td>"); //未排菜日期
					content.append("<td>" + data.getClosingDate() + "</td>"); //截止日期
					content.append("</tr>");
				}
			}
			
			content.append("</table>");
			content.append("</body></html>");
			mailCont=content.toString();
		}
		if(sortNo == 1){
			StringBuilder content = new StringBuilder("<html><head></head><body>");
			//邮件第一部分
			content.append("<div id=\"u22153\" class=\"ax_default label\">\r\n" + 
					"              <div id=\"u22153_div\" class=\"\"></div>\r\n" + 
					"              <div id=\"u22153_text\" class=\"text \">\r\n" + 
					"                <p><span>尊敬的用户您好！</span></p><p><span>&nbsp;&nbsp; &nbsp;&nbsp; </span>"+ 
					"<span style=\"color:#1E1E1E;\">截止</span><span style=\"color:#0000FF;\">"+closingDate+"，</span>"+ 
					"<span>供餐日期</span><span style=\"color:#0000FF;\">#"+dishDate+"#</span><span>，"+ 
					"</span><span style=\"color:#0000FF;\">"+departmentName+"</span><span>所有学校都已完成排菜操作。</span></p>"+
					"              </div>" + 
					"            </div>" + 
					"</span>"+
					"            <!-- Unnamed (图片) -->\r\n" + 
					"            <div id=\"u22154\" style=\"margin-left:20%\">\r\n" + 
					"              <img id=\"u22154_img\" style=\"with:80px;height:80px\" src=\""+SpringConfig.repfile_srvdn+"/CheckMark.png\"/>\r\n" + 
					"            </div>\r\n" +
					""); 
			mailCont=content.toString();
		}
		if(mailCont != null) {  	    	   
			//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
			//查询邮件服务记录以用户名
			//admin
			TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName("shsjw");
			if(tebmsDo != null) {
				//发送邮件
				boolean flag = SendMailAcceUtils.sendMailByHtml(tebmsDo, mailTitle, mailCont,email, true);
				if(!flag)  {
					logger.info("发送失败，尝试重新发送============================================");
					flag = sendMailByHtmlRecursion(tebmsDo, mailTitle, mailCont,email, true);
				}
				
				return flag;
			}
			else {
				logger.error("未设置邮箱");
				return false;
			}
		}
		return false;
	}
	
  	/**
  	 * 五级预警-未验收预警
  	 * @param db2Service 
  	 * @param mailTitle 邮件标题
  	 * @param email 邮件
  	 * @param closingDate 截止日期
  	 * @param dishDate 验收日期
  	 * @param departmentName (全市或者管理部门)
  	 * @return
  	 */
	public static boolean sendNoAcceptMail(Db2Service db2Service,
										   String mailTitle,String email,String closingDate,String dishDate,String departmentName,Map<String,List<DishEmailDto>> dataMap,int allWarnAccount) {
		String mailCont = "";
		if(CommonUtil.isEmpty(mailTitle))
			mailTitle = "未验收预警";
		int sortNo = 1;
		if(dataMap !=null && dataMap.size() >0) {
			StringBuilder content = new StringBuilder("<html><head></head><body>");
			//table样式
			content.append("<style type=\"text/css\">\r\n" + 
					" \r\n" + 
					"                table{\r\n" + 
					"			border-collapse: collapse;\r\n" + 
					"		}\r\n" + 
					"		table,th,td{\r\n" + 
					"			border:1px,solid;\r\n" + 
					"		}\r\n" + 
					" \r\n" + 
					"</style>");
			//邮件第一部分
			content.append("<div id=\"u23776\" class=\"ax_default label\">\r\n" + 
					"              <div id=\"u23776_div\" class=\"\"></div>\r\n" + 
					"              <div id=\"u23776_text\" class=\"text \">\r\n" + 
					"                <p><span>尊敬的用户您好！</span></p><p><span>&nbsp;&nbsp; &nbsp;&nbsp; </span>"
					+ "<span style=\"color:#1E1E1E;\">截止</span><span style=\"color:#0000FF;\">"+closingDate+"，</span><span>供餐日期</span>"
							+ "<span style=\"color:#0000FF;\">#"+dishDate+"#</span><span>，</span><span style=\"color:#0000FF;\">"+departmentName+"</span>"
							+ "<span>验收未上报学校</span><span style=\"color:#0000FF;\">"+allWarnAccount+"</span><span>所，名单如下：</span></p>" + 
					"              </div>" + 
					"            </div>" + 
					""); 
			//table部分
			content.append("&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;");
			content.append("<table  border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size=14px;color:#0000FF;\">");
			content.append("<tr style=\"color:black;\"><th>序号</th><th>管理部门</th><th>学制</th><th>学校名称</th><th>未验收日期</th><th>截止时间</th></tr>");
			int index = 0;
			List<DishEmailDto> dishList = new ArrayList<>();
			SortList<DishEmailDto> sortList = new SortList<DishEmailDto>(); 
			for(Map.Entry<String, List<DishEmailDto>> entry : dataMap.entrySet()) {
				if(entry.getValue()==null || entry.getValue().size() ==0) {
					continue;
				}
				index = 0;
				//按学制排序
				dishList = entry.getValue();
				if(dishList == null) {
					dishList = new ArrayList<>();
				}
		    	 
		    	sortList.Sort(dishList, "getSchTypeId", "asc", "Integer");
		    	
				for (DishEmailDto data : dishList) {
					content.append("<tr align='center' style=\"\" >");
					content.append("<td>" + sortNo++ + "</td>"); //序号
					if("全市".equals(departmentName)) {
						//合并行
						if(index == 0) {
							content.append("<td rowspan=\""+dishList.size()+"\">" + entry.getKey()+"("+entry.getValue().size()+"所)" + "</td>"); //学制
							index ++;
						}
					}else {
						//不合并行
						content.append("<td>" + entry.getKey() + "</td>"); //管理部门
					}
					content.append("<td>" + data.getSchType() + "</td>"); //学制
					content.append("<td>" + data.getSchName() + "</td>"); //学校名称
					content.append("<td>" + data.getNoDishDate() + "</td>"); //未验收日期
					content.append("<td>" + data.getClosingDate() + "</td>"); //截止日期
					content.append("</tr>");
				}
			}
			
			content.append("</table>");
			content.append("</body></html>");
			mailCont=content.toString();
		}
		if(sortNo == 1){
			StringBuilder content = new StringBuilder("<html><head></head><body>");
			//邮件第一部分
			content.append("<div id=\"u22153\" class=\"ax_default label\">\r\n" + 
					"              <div id=\"u22153_div\" class=\"\"></div>\r\n" + 
					"              <div id=\"u22153_text\" class=\"text \">\r\n" + 
					"                <p><span>尊敬的用户您好！</span></p><p><span>&nbsp;&nbsp; &nbsp;&nbsp; </span>"+ 
					"<span style=\"color:#1E1E1E;\">截止</span><span style=\"color:#0000FF;\">"+closingDate+"，</span>"+ 
					"<span>供餐日期</span><span style=\"color:#0000FF;\">#"+dishDate+"#</span><span>，"+ 
					"</span><span style=\"color:#0000FF;\">"+departmentName+"</span><span>所有学校都已完成验收操作。</span></p>"+
					"              </div>" + 
					"            </div>" + 
					"</span>"+
					"            <!-- Unnamed (图片) -->\r\n" + 
					"            <div id=\"u22154\" style=\"margin-left:20%\">\r\n" + 
					"              <img id=\"u22154_img\" style=\"with:80px;height:80px\" src=\""+SpringConfig.repfile_srvdn+"/CheckMark.png\"/>\r\n" + 
					"            </div>\r\n" +
					""); 
			mailCont=content.toString();
		}
		if(mailCont != null) {  	    	   
			//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
			//查询邮件服务记录以用户名
			//admin
			TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName("shsjw");
			if(tebmsDo != null) {
				//发送邮件
				boolean flag = SendMailAcceUtils.sendMailByHtml(tebmsDo, mailTitle, mailCont,email, true);
				
				//上线着急，没时间详测此段逻辑，目前任务每次发送都成功
				/*if(!flag)  {
					logger.info("发送失败，尝试重新发送============================================");
					flag = sendMailByHtmlRecursion(tebmsDo, mailTitle, mailCont,email, true);
				}*/
				
				return flag;
			}
			else {
				logger.error("未设置邮箱");
				return false;
			}
		}
		return false;
	}
	
	public static boolean sendMailByHtmlRecursion(TEduBdMailSrvDo tebmsDo, String mailTitle, String mailCont,String email, boolean isShowDebug) {
		boolean flag = SendMailAcceUtils.sendMailByHtml(tebmsDo, mailTitle, mailCont,email, true);
		if(!flag) {
			logger.info("发送失败，尝试重新发送============================================");
			return sendMailByHtmlRecursion(tebmsDo, mailTitle, mailCont, email, isShowDebug);
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		Db2Service db2Service =null;
		/**
		 * ==================================邮件测试开始================================
		 */
		
		List<String> emailList = new ArrayList<String>();
		//emailList.add("xiefengyangcn@163.com");
		emailList.add("fengyang_xie@ssic.cn");
		//emailList.add("xiefengyangcn@qq.com");
		
		List<DishEmailDto> dataList = new ArrayList<>();
		for(int i =0;i<100;i++) {
	  		DishEmailDto data1 = new DishEmailDto("徐汇区教育局（6所）",-1,"幼儿园","盛大幼儿园","9月9日","2019年09月09日 17:00");
	  		dataList.add(data1);
		}
		DishEmailDto data2 = new DishEmailDto("徐汇区教育局（6所）",-1,"幼儿园","乌鲁木齐幼儿园","9月9日","2019年09月09日 17:00");
		dataList.add(data2);
		DishEmailDto data3 = new DishEmailDto("徐汇区教育局（6所）",-1,"高级中学","上海市向明高中（学生公寓长乐）","9月9日","2019年09月09日 17:00");
		dataList.add(data3);
		DishEmailDto data4 = new DishEmailDto("黄浦区教育局（3所）",-1,"初级中学","徐汇中学","9月9日","2019年09月09日 17:00");
		dataList.add(data4);
		DishEmailDto data5 = new DishEmailDto("黄浦区教育局（3所）",-1,"幼儿园","贝贝托儿所","9月9日","2019年09月09日 17:00");
		dataList.add(data5);
		String closingDate = "2019年09月10日 17:00";
		String dishDate = "2019/09/10";
		String departmentName = "全市";
	    String mailTitle = "未验收预警";
		for(String email : emailList) {
			//flag = SendMailCommon.sendNoDishMail(db2Service, mailTitle,email,closingDate,dishDate,departmentName,dataList);
//			boolean flag = SendMailCommon.sendNoAcceptMail(db2Service, mailTitle,email,closingDate,dishDate,departmentName,dataList);
			boolean flag = true;
			if(!flag) {
				logger.error("发送到"+email+"失败！");
			}
		}
			/**
		 * ==================================邮件测试结束================================
		 */

	}
}