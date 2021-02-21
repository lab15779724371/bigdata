package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.alibaba.fastjson.JSON;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.GetUserInterfaceColumsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColumsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

/**
 * 常用的公共的逻辑处理方法
 * @author Administrator
 *
 */
public class CommonUtil {
	
	/**
	 * String 格式["",""……] 转换成List
	 * @param objects
	 * @return
	 */
	public static List<Object> changeStringToList(String objects) {
		List<Object> distNamesList = null;
		if(StringUtils.isNotEmpty(objects)) {
			distNamesList = (List<Object>)JSON.parse(objects);
		}
		return distNamesList;
	}	
	
	
	/**
	 * String 格式["",""……] 转换成Set
	 * @param objects
	 * @return
	 */
	public static Set<Object> changeStringToSet(String objects) {
		Set<Object> distNamesList = null;
		if(StringUtils.isNotEmpty(objects)) {
			distNamesList = (Set<Object>)JSON.parse(objects);
		}
		return distNamesList;
	}	
	
	public static List<String> getInputList(List<String> list) {
		String str = "";
		if(list!=null && list.size() >0) {
			str=list.get(0);
			str=str.replaceAll("[", "");
			if(list.size() >=2) {
				str=list.get(list.size()-1).replaceAll("]", "");
			}
		}
	    return list;
	}
	
	
	/**
	 * 文件流写入文件
	 * @param retFlag
	 * @param wb
	 * @param excelPath
	 * @return
	 */
	public static boolean commonExportExcel(boolean retFlag, Workbook wb, String excelPath) {
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
				ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(excelPath, os,null);
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
		return retFlag;
	}
	
	/**
	 * 根据开始日期、结束日期，获取开始日期和结束日期的年、月
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return 开始年份、开始月份、结束年份、结束月份的数组
	 */
	public static String[]  getYearMonthByDate(String startDate, String endDate) {
		
		String[] yearMonths = new String [4];
		String [] startDates = startDate.split("-");
    	String [] endDates = endDate.split("-");
    	
    	String startYearTemp = "";
    	String startMonthTemp = "";
    	if(startDates.length>=2) {
    		startYearTemp = startDates[0];
    		startMonthTemp = startDates[1];
    	}else {
    		startYearTemp = BCDTimeUtil.convertNormalDate(null).split("-")[0];
    		startMonthTemp = BCDTimeUtil.convertNormalDate(null).split("-")[1];
    	}
    	
    	
    	String endYearTemp = "";
    	String endMonthTemp = "";
    	if(endDates.length>=2) {
    		endYearTemp = endDates[0];
    		endMonthTemp = endDates[1];
    	}else {
    		endYearTemp = BCDTimeUtil.convertNormalDate(null).split("-")[0];
    		endMonthTemp = BCDTimeUtil.convertNormalDate(null).split("-")[1];
    	}
    	
    	if(startYearTemp.indexOf("0")==0) {
    		startYearTemp = startYearTemp.replaceFirst("0", "");
    	}
    	
    	if(endYearTemp.indexOf("0")==0) {
    		endYearTemp.replaceFirst("0", "");
    	}
    	
    	if(startMonthTemp.indexOf("0")==0) {
    		startMonthTemp =startMonthTemp.replaceFirst("0", "");
    	}
    	
    	if(endMonthTemp.indexOf("0")==0) {
    		endMonthTemp = endMonthTemp.replaceFirst("0", "");
    	}
    	
    	yearMonths[0]=startYearTemp;
    	yearMonths[1]=startMonthTemp;
    	yearMonths[2]=endYearTemp;
    	yearMonths[3]=endMonthTemp;
    	
    	return yearMonths;
	}

	/**
	 * 指定日期加制定天数（返回值为yyyy-MM-dd格式）
	 * @param date yyyy-MM-dd格式时间
	 * @param addDay 增加的天数
	 * @return
	 */
	public static String dateAddDay(String date, int addDay) {
		String endDateAddOne;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		endDateAddOne = df.format(new Date());
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(df.parse(date));
			calendar.add(Calendar.DAY_OF_MONTH, addDay);//加一天
			endDateAddOne = df.format(calendar.getTime());
		} catch (ParseException e1) {
		}
		return endDateAddOne;
	}
	
	/**
	 * 指定日期加制定天数（返回值为yyyy-MM-dd格式）
	 * @param date yyyy-MM-dd格式时间
	 * @param addDay 增加的天数
	 * @return
	 */
	public static String dateAddDayByFormat(String date, int addDay,String format) {
		String endDateAddOne;
		DateFormat df = new SimpleDateFormat(format);
		endDateAddOne = df.format(new Date());
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(df.parse(date));
			calendar.add(Calendar.DAY_OF_MONTH, addDay);//加一天
			endDateAddOne = df.format(calendar.getTime());
		} catch (ParseException e1) {
		}
		return endDateAddOne;
	}
	
	/**
	 * 指定日期加制定天数（返回值为yyyy-MM-dd格式）
	 * @param date yyyy-MM-dd格式时间
	 * @param addDay 增加的天数
	 * @return
	 */
	public static String dateAddSecondByFormat(String date, int addDay,String format) {
		String endDateAddOne;
		DateFormat df = new SimpleDateFormat(format);
		endDateAddOne = df.format(new Date());
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(df.parse(date));
			calendar.add(Calendar.SECOND, addDay);//加一秒
			endDateAddOne = df.format(calendar.getTime());
		} catch (ParseException e1) {
		}
		return endDateAddOne;
	}

	/**
	 * 获取指定开始年份、月份、结束年份月份之间所有的年份月份结合
	 * 每个值的格式为：year_startMonth_endMonth
	 * @param startYear 开始年份
	 * @param startMonth 开始月份
	 * @param endYear 结束年份
	 * @param endMonth 结束月份
	 * @return
	 */
	public static List<String> getYearMonthList(String startYear, String startMonth, String endYear, String endMonth) {
		//获取年份+月份集合，方便查询
		int iStartYear = Integer.parseInt(startYear);
		int iEndYear = Integer.parseInt(endYear);
		int iStartMonth = Integer.parseInt(startMonth);
		int iEndMonth = Integer.parseInt(endMonth);
		
		//year_startMonth_endMonth
		List<String> listYearMonth = new ArrayList<>();
		String strYearMonth = "";
		for(int year =iStartYear;year<=iEndYear;year++) {
			//年份和开始月份
			if(year ==iStartYear) {
				//第一年，开始月份等于开始时间的月份
				strYearMonth = year+"_"+iStartMonth;
			}else if (year > iStartYear) {
				//非第一年，开始月份等于1
				strYearMonth = year+"_"+1;
			}
			
			//结束月份
			if(year == iEndYear) {
				//如果是最后一年，则结束月份等于结束日期的月份
				strYearMonth +="_"+iEndMonth;
			}else {
				//如果不是最后一年，则结束月份为12月
				strYearMonth +="_"+12;
			}
			
			if(strYearMonth!=null && !"".equals(strYearMonth) && strYearMonth.split("_").length>=3) {
				listYearMonth.add(strYearMonth);
			}
		}
		return listYearMonth;
	}
	
	/**
	 * 判断是否是整数（包含负数）
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str){
	    Pattern pattern = Pattern.compile("-?[0-9]+?");
	    Matcher isNum = pattern.matcher(str);
	    if( !isNum.matches() ){
	        return false;
	    }
	    return true;
	}
	
	/**
	 * 判断是否是数字（包含小数、整数、正数、负数）
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("-?[0-9]+(.[0-9]+)?");
	    Matcher isNum = pattern.matcher(str);
	    if( !isNum.matches() ){
	        return false;
	    }
	    return true;
	}


	/**
	 * 获取当前登录账号的主管部门，并转换为target值
	 * @param token
	 * @param db1Service
	 * @param db2Service
	 * @return
	 */
	public static Integer getTarget(String token, Db1Service db1Service, Db2Service db2Service) {
		//获取用户数据权限信息
		TEduBdUserDo eduBdUserDo = AppModConfig.getUserByToken(token, db2Service);
		//subLevelId : 0:其他 1:部属 2:市属 3:区属
		Integer subLevelId = -1;
		
		if(eduBdUserDo == null ) {
			return subLevelId;
		}

		String curOrgName = eduBdUserDo.getOrgName();
		
		if(StringUtils.isEmpty(curOrgName)) {
			return subLevelId;
		}
		if(curOrgName.equals("黄浦区教育局")  || curOrgName.equals("静安区教育局") || curOrgName.equals("徐汇区教育局") || curOrgName.equals("长宁区教育局") || curOrgName.equals("普陀区教育局") ||
		   curOrgName.equals("虹口区教育局")  || curOrgName.equals("杨浦区教育局") || curOrgName.equals("闵行区教育局") || curOrgName.equals("嘉定区教育局") || curOrgName.equals("宝山区教育局") ||
		   curOrgName.equals("浦东新区教育局") || curOrgName.equals("松江区教育局") ||curOrgName.equals("金山区教育局") ||curOrgName.equals("青浦区教育局") ||curOrgName.equals("奉贤区教育局") ||
		   curOrgName.equals("崇明区教育局")) {
			subLevelId = 3;
		}
		else if(curOrgName.equals("市水务局（海洋局）") || curOrgName.equals("市农委") || curOrgName.equals("市交通委") || 
				curOrgName.equals("市科委") || curOrgName.equals("市商务委") || curOrgName.equals("市经信委")|| curOrgName.equals("市教委")) {   
			subLevelId = 2;
		}
		else if(curOrgName.equals("教育部")) {
			subLevelId = 1;
		}
		
		
	  	
	  	Integer target = -1;
	  	if(subLevelId ==3 ) {
	  		target = 2;
	  	}else if (subLevelId ==2 && curOrgName.equals("市教委")) {
	  		target = 3;
	  	}
	  	
		return target;
	}
	
	 public static boolean isNotEmpty(String cs) {
	        return (StringUtils.isNotEmpty(cs) && !"null".equalsIgnoreCase(cs));
	 }
	 
	 public static boolean isEmpty(String cs) {
	        return (StringUtils.isEmpty(cs) || "null".equalsIgnoreCase(cs));
	 }
	 
	 /**
	  * 比较两个String类型时间的大小
	  * @param date1 String 类型时间1
	  * @param date2 String 类型时间1
	  * @return 0：时间相等 1：date1 小于date2 2：date1 大于date2 -1:异常
	  */
	 public static Integer compareStrDate(String date1,String date2,String format){
		 
			DateFormat dateFormat=new SimpleDateFormat(format);
			try {
				Date d1 = dateFormat.parse(date1);
				Date d2 = dateFormat.parse(date2);
				if(d1.equals(d2)){
					return 0;
				}else if(d1.before(d2)){
					return 1;
				}else if(d1.after(d2)){
					return 2;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("compareStrDate异常"+date1+","+date2);
			}
			
			return -1;
		}

	/**
	 * 获取两个String日期之间的日期集合
	 * @param startDate
	 * @param endDate
	 * @param dateFormat
	 * @return
	 */
	public static String[] getDatesArray(String startDate, String endDate,String dateFormat) {
		String[] dates;
		//开始时间和结束时间有一个为空，一个不为空，则开始时间和结束时间一致
		if((startDate==null || "".equals(startDate)) && (endDate!=null && !"".equals(endDate))) {
			startDate = endDate;
		}else if((endDate==null || "".equals(endDate)) && (startDate!=null && !"".equals(startDate))) {
			endDate = startDate;
		}
		
		// 按照当天日期获取数据
		if (startDate == null || endDate == null) { 
			dates = new String[1];
			dates[0] = BCDTimeUtil.convertNormalDate(null);
		} else { // 按照开始日期和结束日期获取数据
			DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDate);
			DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDate);
			int days = Days.daysBetween(startDt, endDt).getDays() + 1;
			dates = new String[days];
			for (int i = 0; i < days; i++) {
				dates[i] = startDt.plusDays(i).toString(dateFormat);
			}
		}
		
		return dates;
	}	
	
	/**
	 * 根据开始日期、结束日期获取年月的集合
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<String> getYearMonthList(String startDate, String endDate) {
		String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];
    	
    	int iStartYear = Integer.parseInt(startYear);
		int iEndYear = Integer.parseInt(endYear);
		int iStartMonth = Integer.parseInt(startMonth);
		int iEndMonth = Integer.parseInt(endMonth);
		
		//year_startMonth_endMonth
		List<String> listYearMonth = new ArrayList<>();
		String strYearMonth = "";
		for(int year =iStartYear;year<=iEndYear;year++) {
			//年份和开始月份
			if(year ==iStartYear) {
				//第一年，开始月份等于开始时间的月份
				strYearMonth = year+"_"+iStartMonth;
			}else if (year > iStartYear) {
				//非第一年，开始月份等于1
				strYearMonth = year+"_"+1;
			}
			
			//结束月份
			if(year == iEndYear) {
				//如果是最后一年，则结束月份等于结束日期的月份
				strYearMonth +="_"+iEndMonth;
			}else {
				//如果不是最后一年，则结束月份为12月
				strYearMonth +="_"+12;
			}
			
			if(strYearMonth!=null && !"".equals(strYearMonth) && strYearMonth.split("_").length>=3) {
				listYearMonth.add(strYearMonth);
			}
		}
		return listYearMonth;
	}
   /**
    * Map转换位String类型
    * @param map
    * @return
    */
   public static String getMapToString(Map<String,Object> map){
       Set<String> keySet = map.keySet();
       //将set集合转换为数组
       String[] keyArray = keySet.toArray(new String[keySet.size()]);
       //给数组排序(升序)
       Arrays.sort(keyArray);
       //因为String拼接效率会很低的，所以转用StringBuilder
       StringBuilder sb = new StringBuilder();
       for (int i = 0; i < keyArray.length; i++) {
           // 参数值为空，则不参与签名 这个方法trim()是去空格
           if ((String.valueOf(map.get(keyArray[i]))).trim().length() > 0) {
               sb.append(keyArray[i]).append(":").append(String.valueOf(map.get(keyArray[i])).trim());
           }
           if(i != keyArray.length-1){
               sb.append(",");
           }
       }
       return sb.toString();
   }
	   
   /**
    * 
    * String转map
    * @param str
    * @return
    */
   public static Map<String,String> getStringToMap(String strMap){
       //根据逗号截取字符串数组
       String[] arrMap = strMap.split(",");
       //创建Map对象
       Map<String,String> map = new HashMap<>();
       //循环加入map集合
       String[] arrTemp;
       for (int i = 0; i < arrMap.length; i++) {
           //根据":"截取字符串数组
    	   arrTemp = arrMap[i].split(":");
           //str2[0]为KEY,str2[1]为值
           map.put(arrTemp[0],arrTemp[1]);
       }
       return map;
   }

   /**
    * Map转成实体对象
    *
    * @param map   map实体对象包含属性
    * @param clazz 实体对象类型
    * @return
    */
   public static <T> T map2Object(Map<String, String> map, Class<T> clazz) {
       if (map == null) {
           return null;
       }
       T obj = null;
       try {
           obj = clazz.newInstance();

           Field[] fields = obj.getClass().getDeclaredFields();
           for (Field field : fields) {
               int mod = field.getModifiers();
               if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                   continue;
               }
               field.setAccessible(true);
               String filedTypeName = field.getType().getName();
               
               //判断是否是null或者空
               String filed = String.valueOf(map.get(field.getName()));
        	   if(!filedTypeName.equalsIgnoreCase("java.lang.String") && CommonUtil.isEmpty(filed)) {
        		   field.set(obj, null);
        		   continue;
        	   }
               
               if (filedTypeName.equalsIgnoreCase("java.util.date")) {
                   String datetimestamp = String.valueOf(map.get(field.getName()));
                   if (datetimestamp.equalsIgnoreCase("null")) {
                       field.set(obj, null);
                   } else {
                       field.set(obj, new Date(Long.parseLong(datetimestamp)));
                   }
               }else if (filedTypeName.equalsIgnoreCase("java.lang.Long")) {
            	   field.set(obj, map.get(field.getName())==null?null:Long.valueOf(map.get(field.getName()).toString()));
               }else if (filedTypeName.equalsIgnoreCase("java.lang.Integer")) {
            	   field.set(obj, map.get(field.getName())==null?null:Integer.valueOf(map.get(field.getName()).toString()));
               }else if (filedTypeName.equalsIgnoreCase("java.math.BigDecimal")) {
            	   field.set(obj, map.get(field.getName())==null?null:BigDecimal.valueOf(Double.valueOf(map.get(field.getName()).toString())));
               }else if (filedTypeName.equalsIgnoreCase("java.lang.Double")) {
            	   field.set(obj, map.get(field.getName())==null?null:Double.valueOf(map.get(field.getName()).toString()));
               }else {
                   field.set(obj, map.get(field.getName()));
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }
       return obj;
   }
   
	/**
	 * 根据当前用户和接口名称或者当前接口需要展示的列集合
	 * @param token
	 * @param db2Service
	 */
	public static  Map<String,UserSetColums> getUserSetColumMap(String token,String intefaceName,Db2Service db2Service) {
		Map<String,UserSetColums> userSetColumsMap = null;
		AddUserInterfaceColumsDTO auiDto = new GetUserInterfaceColumsAppMod().appModFunc(token, intefaceName, db2Service);
		try {
			if(auiDto !=null && auiDto.getUserColums() !=null ) {
				String userSetColumns = auiDto.getUserColums().getColumns();
				if(CommonUtil.isNotEmpty(userSetColumns)) {
					List<UserSetColums> userSetColumsList = com.tfit.BdBiProcSrvShEduOmc.util.JsonUtil.getListDTO(userSetColumns, UserSetColums.class);
					
					if(userSetColumsList != null && userSetColumsList.size() > 0) {
						userSetColumsMap = userSetColumsList.stream().collect(Collectors.toMap(UserSetColums::getLabel,(b)->b));
					}
				}
			}
		}catch(Exception ex) {
			
		}
		
		return userSetColumsMap;
	}
	
	/**
	 * 根据当前用户和接口名称或者当前接口需要展示的列集合
	 * @param token
	 * @param db2Service
	 */
	public static  List<UserSetColums> getUserSetColumList(String token,String intefaceName,Db2Service db2Service) {
		AddUserInterfaceColumsDTO auiDto = new GetUserInterfaceColumsAppMod().appModFunc(token, intefaceName, db2Service);
		List<UserSetColums> userSetColumsList = new ArrayList<>();
		try {
			if(auiDto !=null && auiDto.getUserColums() !=null ) {
				String userSetColumns = auiDto.getUserColums().getColumns();
				if(CommonUtil.isNotEmpty(userSetColumns)) {
					userSetColumsList = com.tfit.BdBiProcSrvShEduOmc.util.JsonUtil.getListDTO(userSetColumns, UserSetColums.class);
				}
			}
		}catch(Exception ex) {
			
		}
		
		return userSetColumsList;
	}
	
	/**

	 * 根据字段名称取值

	 * 

	 * @param obj 类名

	 * @param fieldName 属性名

	 * @return

	 */

	 public static Object getClassValue(Object obj, String fieldName) {

	  if (obj == null) {

	   return null;

	  }

	  try {

		   Class beanClass = obj.getClass();
	
		   Method[] ms = beanClass.getMethods();
	
		   for (int i = 0; i < ms.length; i++) {
	
		    // 非get方法不取
	
		    if (!ms[i].getName().startsWith("get")) {
	
		     continue;
	
		    }
	
		    Object objValue = null;
	
		    try {
	
		     objValue = ms[i].invoke(obj, new Object[] {});
	
		    } catch (Exception e) {
	
	//	     logger.info("反射取值出错：" + e.toString());
	
		     continue;
	
		    }
	
		    if (objValue == null) {
	
		     continue;
	
		    }
	
		    if (ms[i].getName().toUpperCase().equals(fieldName.toUpperCase())
	
		      || ms[i].getName().substring(3).toUpperCase().equals(fieldName.toUpperCase())) {
	
		     return objValue;
	
		    } else if (fieldName.toUpperCase().equals("SID") && (ms[i].getName().toUpperCase().equals("ID")
	
		      || ms[i].getName().substring(3).toUpperCase().equals("ID"))) {
	
		     return objValue;
	
		    }

	   }

	  } catch (Exception e) {

	   // logger.info("取方法出错！" + e.toString());

	  }

	  return null;

	 }
}
