package com.tfit.BdBiProcSrvShEduOmc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * BCD 时间格式工具类
 * 
 * <pre>
 * 1、日期格式：年，月，日，周，总共5字节，具体为
 *  年：0000-9999，占用2字节，范围0-9999，如20 16表示2016年，
 *  月：01-12，占用1字节，范围01-12，如02表示2月，
 *  日：01-31，占用1字节，范围01-31，如09表示9日，
 *  周：01-07，占用1字节，范围01-07，01表示星期一，07表示星期日，
 *  日期范围从0年1月1日到9999年12月31日，
 *  如2016年4月30日星期六，则日期格式为：20 16 04 30 06，即2016043006
 * 2、时间格式：时，分，秒，毫秒，总共4字节，具体为
 *   时：00-23，占用1字节，范围00-23，
 *   分：00-59，占用1字节，范围00-59，
 *   秒：00-59，占用1字节，范围00-59，
 *   毫秒：00-99，，占用1字节，范围00-99，
 *  时间范围从00时00分00秒00毫秒到23时59分59秒99毫秒，
 *  如22时59分59秒99毫秒，则时间格式为：22 59 59 99，即22595999
 * 3、如2016年4月30日星期六22时59分59秒99毫秒，则日期时间格式为
 *  201604300622595999
 *
 * </pre>
 */
public class BCDTimeUtil {
	private static final Logger logger = LogManager.getLogger(BCDTimeUtil.class.getName());
	
    public static final DateTimeFormatter BCDTimeFormatter = DateTimeFormat.forPattern("yyyyMMddeeHHmmssSS");
    public static final DateTimeFormatter BCDTimeFormatterNew = DateTimeFormat.forPattern("yyyyMMddeeHHmmssSSS");

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    
    public static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HHmmss");
    
    public static final DateTimeFormatter dateTimeSecFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public static final DateTimeFormatter monthDayFormatter = DateTimeFormat.forPattern("M/d");

    public static DateTime convertTimeStrToDateTime(String timeStr) {
        if(timeStr == null || timeStr.length() <= 0) {
            return null;
        }
        return DateTime.parse(timeStr, dateTimeFormatter);
    }
    
    public static DateTime convertDateStrToDate(String dateStr){
        if(dateStr == null || dateStr.length() <= 0){
            return null;
        }
        return DateTime.parse(dateStr, dateFormatter);
    }
    
    public static DateTime convertDateStrToDate(String dateStr,String strDateFromat){
        if(dateStr == null || dateStr.length() <= 0){
            return null;
        }
        return DateTime.parse(dateStr, DateTimeFormat.forPattern(strDateFromat));
    }
    
    /**
     * 将时间转换为BCD格式
     * @param dateTime
     * @return "yyyyMMddeeHHmmssSSS"
     */
    public static String convertBCDFrom(DateTime dateTime){
        if (dateTime == null) {
            dateTime = DateTime.now();
        }

        return dateTime.toString(BCDTimeFormatterNew);
    }

    /**
     * 将BCD格式转换为时间对象
     * @param timeBCD time in BCD format
     * @return "yyyyMMddeeHHmmssSS"
     */
    public static DateTime convertBCDTo(String timeBCD){
        if (StringUtils.isBlank(timeBCD)) {
            return DateTime.now();
        }
        if (timeBCD.length() == 18) {
            return DateTime.parse(timeBCD, BCDTimeFormatter);
        }
        if (timeBCD.length() == 19) {
            return DateTime.parse(timeBCD, BCDTimeFormatterNew);
        }

        return DateTime.now();
    }

    /**
     * 返回给定时间的时分秒
     * @return HHmmss
     */
    public static String timeFormatter(DateTime dateTime){
        if (dateTime == null){
            dateTime = DateTime.now();
        }
        return dateTime.toString(timeFormatter);
    }

    /**
     * 返回当前时间的时分秒
     * @return HHmmss
     */
    public static String timeFormatter(){
        return timeFormatter(null);
    }

    /**
     * 将时间转换为一般格式
     * @param dateTime
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String convertNormalFrom(DateTime dateTime){
        if (dateTime == null) {
            dateTime = DateTime.now();
        }

        return dateTime.toString(dateTimeFormatter);
    }
    
    /**
     * 将时间转换为月天格式
     * @param dateTime
     * @return "M/d"
     */
    public static String convertMonthDayForm(DateTime dateTime){
        if (dateTime == null) {
            dateTime = DateTime.now();
        }

        return dateTime.toString(monthDayFormatter);
    }
    
    /**
     * 将日期转换为一般格式
     * @param dateTime
     * @return "yyyy-MM-dd"
     */
    public static String convertNormalDate(DateTime dateTime){
        if (dateTime == null) {
            dateTime = DateTime.now();
        }

        return dateTime.toString(dateFormatter);
    }
    
    /**
     * 将LONG时间转换为一般格式
     * @param dateTime
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String convertNormalFromLong(long second)
    {	
    	DateTime dateTime = new DateTime(second*1000); 
    	String dtResult = dateTime.toString(dateTimeFormatter);
        return dtResult;
    }

    /**
     * 将时间转换为一般格式
     * @param milliseconds
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String convertNormalFrom(long milliseconds){
        return new DateTime(milliseconds).toString(dateTimeFormatter);
    }

    public static DateTime getDateTimeFromms(long mseconds){
		DateTime date = new DateTime(mseconds);  
        return date;
	}
    
    /**
     * 判断消息是否过期
     * @param createTime 消息发送到平台上的时间
     * @return
     */
    public static boolean ifMsgExpire(float createTime){
        DateTime devTime = new DateTime((long)createTime * 1000);
        int minutes = Minutes.minutesBetween(devTime,DateTime.now()).getMinutes();

        return (minutes > ParserConstants.MINUTES_HEARTBEAT_EXPIRE);
    }

    //时间格式转换：M/d/yyyy HH:mm:ss转成yyyy.MM.dd HH:mm:ss
    public static String cvtDateTimeFormat1(String strInDateTime, boolean isIgnoreTime) {
    	String newDateTime = null;
    	SimpleDateFormat sdf1 = null, sdf2 = null;
    	int idx = strInDateTime.indexOf(" ");
    	logger.info("strInDateTime = " + strInDateTime + ", idx = " + idx + ", isIgnoreTime = " + isIgnoreTime);
    	if(isIgnoreTime || idx == -1) {
    		if(idx != -1)
    			strInDateTime = strInDateTime.substring(0, idx);
    		sdf1 = new SimpleDateFormat("M/d/yyyy");
        	sdf2 = new SimpleDateFormat("yyyy.MM.dd");
    	}
    	else {
    		sdf1 = new SimpleDateFormat("M/d/yyyy HH:mm:ss");
    		sdf2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    	}
    	try {
			Date date = sdf1.parse(strInDateTime);
			newDateTime = sdf2.format(date);
			if(isIgnoreTime) {
				idx = newDateTime.indexOf(" ");
				if(idx != -1) {
					newDateTime = newDateTime.substring(0, idx);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return newDateTime;
    }
    
    //时间格式转换：yyyy-MM-dd HH:mm:ss转成 M/d/yyyy HH:mm:ss
    public static String cvtDateTimeFormat2(String strInDateTime, boolean isIgnoreTime) {
    	String newDateTime = null;
    	SimpleDateFormat sdf1 = null, sdf2 = null;
    	int idx = strInDateTime.indexOf(" ");
    	if(isIgnoreTime || idx == -1) {
    		sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        	sdf2 = new SimpleDateFormat("M/d/yyyy");
    	}
    	else {
    		sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		sdf2 = new SimpleDateFormat("M/d/yyyy HH:mm:ss");
    	}
    	try {
			Date date = sdf1.parse(strInDateTime);
			newDateTime = sdf2.format(date);
			if(isIgnoreTime) {
				idx = newDateTime.indexOf(" ");
				if(idx != -1) {
					newDateTime = newDateTime.substring(0, idx);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return newDateTime;
    }
    
    //时间格式转换：yyyy-MM-dd HH:mm:ss转成 yyyy.MM.dd HH:mm:ss
    public static String cvtDateTimeFormat3(String strInDateTime, boolean isIgnoreTime) {
    	String newDateTime = null;
    	SimpleDateFormat sdf1 = null, sdf2 = null;
    	int idx = strInDateTime.indexOf(" ");
    	if(isIgnoreTime || idx == -1) {
    		sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        	sdf2 = new SimpleDateFormat("yyyy.MM.dd");
    	}
    	else {
    		sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		sdf2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    	}
    	try {
			Date date = sdf1.parse(strInDateTime);
			newDateTime = sdf2.format(date);
			if(isIgnoreTime) {
				idx = newDateTime.indexOf(" ");
				if(idx != -1) {
					newDateTime = newDateTime.substring(0, idx);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return newDateTime;
    }
    
    //获取本月日期天集合
    public static String[] getThisMonthDays(String date) {
    	String[] tsMonthDays = null;
    	String dt = date + " 12:00:00";
    	DateTime dateTime = dateTimeFormatter.parseDateTime(dt);
    	int year = dateTime.getYear();
    	int thisMonth = dateTime.getMonthOfYear();
    	int curDay = dateTime.getDayOfMonth();
    	tsMonthDays = new String[curDay];
    	for(int i = 1; i <= curDay; i++) {
    		tsMonthDays[i-1] = String.format("%d-%02d-%02d", year, thisMonth, i);
    	}
    	
    	return tsMonthDays;
    }
    
    //获取本周日期天集合
    public static String[] getThisWeekDays(String date) {
    	String[] tsWeekDays = null;
    	String dt = date + " 12:00:00";
    	DateTime dateTime = dateTimeFormatter.parseDateTime(dt);
    	int curDayWeek = dateTime.getDayOfWeek();
    	DateTime startWeekDt = dateTime.minusDays(curDayWeek);
    	tsWeekDays = new String[curDayWeek];
    	for(int i = 1; i <= curDayWeek; i++) {
    		DateTime curWeekDt = startWeekDt.plusDays(i);
    		tsWeekDays[i-1] = String.format("%d-%02d-%02d", curWeekDt.getYear(), curWeekDt.getMonthOfYear(), curWeekDt.getDayOfMonth());
    	}
    	
    	return tsWeekDays;
    }
    
    //获取前N天的日期以当前日期（参数为0表示当天日期），格式：xxxx-xx-xx
    public static String getAgoDayDate(int agoDayCount,String format) {
    	String agoDate = new DateTime().minusDays(agoDayCount).toString(format);
    	return agoDate;
    }
    
    /**
     * 将java.sql.Timestamp对象转化为String字符串
     * @param time
     *            要格式的java.sql.Timestamp对象
     * @param strFormat
     *            输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
     * @return 表示日期的字符串
     */
    public static String cvtSqlDateToStr(java.sql.Timestamp time, String strFormat) {
        DateFormat df = new SimpleDateFormat(strFormat);
        String str = df.format(time);
        return str;
    }
    
    /**
     * 将String字符串转换为java.sql.Timestamp格式日期,用于数据库保存
     * @param strDate
     *            表示日期的字符串
     * @param dateFormat
     *            传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
     * @return java.sql.Timestamp类型日期对象（如果转换失败则返回null）
     */
    public static java.sql.Timestamp cvtStrToSqlDate(String strDate, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        java.util.Date date = null;
        try {
                date = sf.parse(strDate);
        } catch (ParseException e) {
                e.printStackTrace();
        }
        java.sql.Timestamp dateSQL = new java.sql.Timestamp(date.getTime());
        return dateSQL;
    }
    
    public static boolean isValidDate(String date, String strFormat) {
    	boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，如strFormat为yyyy/MM/dd
    	SimpleDateFormat format = new SimpleDateFormat(strFormat);
    	try {
    		// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
    		format.setLenient(false);
    		format.parse(date);
    	} catch (ParseException e) {
    		// e.printStackTrace();
    		// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
    		convertSuccess = false;
    	} 
    	return convertSuccess;
    }
}
