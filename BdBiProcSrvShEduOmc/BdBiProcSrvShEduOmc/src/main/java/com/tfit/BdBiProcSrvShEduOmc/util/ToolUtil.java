package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tfit.BdBiProcSrvShEduOmc.config.DataSourceConn;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;

//工具公共类
public class ToolUtil {
	private static final Logger logger = LogManager.getLogger(ToolUtil.class.getName());
	public List<String> fullTimeList(String fullTime,String frequency,String interval) throws ParseException {
		List<String> timeList=new ArrayList<String>();
		int n=Integer.parseInt(frequency);
		for(int i=0;i<n;i++) {
			long millisecond=i*Integer.parseInt(interval)*60*1000;
			timeList.add(getAddMillisecond(fullTime,millisecond).substring(0,16));
		}
		return timeList;
	}
	
	//解决 java.net.SocketException: Broken pipe 问题
	public  JdbcTemplate getJdbcTemplateHive(JdbcTemplate jdbcTemplateHive,JdbcTemplate jdbcTemplateHive2,DataSource dataSourceHive,String table) {
		JdbcTemplate jdbcTemplateHiveTemp = jdbcTemplateHive;
		if (jdbcTemplateHiveTemp == null) {
			if (jdbcTemplateHive2 != null) {
				jdbcTemplateHiveTemp = jdbcTemplateHive2;
			} else {
				jdbcTemplateHiveTemp = null;
			}
		}
		boolean hive1IsBreak = false;
		if (jdbcTemplateHiveTemp != null) {
			String sql = "select 1 from " + table + " limit 0";
			try {
				jdbcTemplateHiveTemp.queryForMap(sql);
			} catch (Exception ex) {
				if (ex.getMessage().contains("java.net.SocketException: Broken pipe")) {
					logger.info("-------------------------------------------------------------------新线程启动hive");
					jdbcTemplateHiveTemp = jdbcTemplateHive2;
					logger.info(
							"***********************************************************************catch jdbcTemplateHive2"
									+ ex.getMessage());
				}
				hive1IsBreak = true;
			}
		}

		if (hive1IsBreak) {
			// 如果hive1连接有问题，测试hive2
			if (jdbcTemplateHiveTemp != null) {
				String sql = "select 1 from " + table + " limit 0";
				try {
					jdbcTemplateHiveTemp.queryForMap(sql);
				} catch (Exception ex) {
					if (ex.getMessage().contains("java.net.SocketException: Broken pipe")) {

						logger.info(
								"***********************************************************************catch jdbcTemplateHive2"
										+ ex.getMessage());
						// 重新加载hive1
						//重新加载hive1
						String url = "172.18.14.30";
						String uri = "";
						try {
							uri = jdbcTemplateHiveTemp.getDataSource().getConnection().getClientInfo().getProperty("Uri");
							logger.info("uri:"+uri);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						if(CommonUtil.isNotEmpty(uri) && uri.indexOf("172.18.14.35") >=0) {
							url = "172.18.14.31";
						}
						dataSourceHive = new DataSourceConn().getDataSource(url);
						jdbcTemplateHiveTemp = new JdbcTemplate(dataSourceHive);
					}
					hive1IsBreak = true;
				}
			}
		}
		logger.info("***********************************************************************当前连接hive库："
				+ jdbcTemplateHiveTemp.getDataSource().toString());

		return jdbcTemplateHiveTemp;
	}

	public String departmentName(String departmentId) {
		String compDep = "";
		if ("0".equals(departmentId)) {
			compDep = "外籍人子女学校";
		} else if ("1".equals(departmentId)) {
			compDep = "黄浦区教育局";
		} else if ("2".equals(departmentId)) {
			compDep = "嘉定区教育局";
		} else if ("3".equals(departmentId)) {
			compDep = "宝山区教育局";
		} else if ("4".equals(departmentId)) {
			compDep = "浦东新区教育局";
		} else if ("5".equals(departmentId)) {
			compDep = "松江区教育局";
		} else if ("6".equals(departmentId)) {
			compDep = "金山区教育局";
		} else if ("7".equals(departmentId)) {
			compDep = "青浦区教育局";
		} else if ("8".equals(departmentId)) {
			compDep = "奉贤区教育局";
		} else if ("9".equals(departmentId)) {
			compDep = "崇明区教育局";
		} else if ("10".equals(departmentId)) {
			compDep = "静安区教育局";
		} else if ("11".equals(departmentId)) {
			compDep = "徐汇区教育局";
		} else if ("12".equals(departmentId)) {
			compDep = "长宁区教育局";
		} else if ("13".equals(departmentId)) {
			compDep = "普陀区教育局";
		} else if ("14".equals(departmentId)) {
			compDep = "虹口区教育局";
		} else if ("15".equals(departmentId)) {
			compDep = "杨浦区教育局";
		} else if ("16".equals(departmentId)) {
			compDep = "闵行区教育局";
		} else if ("20".equals(departmentId)) {
			compDep = "市属中职校";
		} else if ("21".equals(departmentId)) {
			compDep = "其他";
		}
		return compDep;
	}

	public String departmentId(String orgName) {
		String compDep = "";
		if ("外籍人子女学校".equals(orgName)) {
			compDep = "0";
		} else if ("黄浦区教育局".equals(orgName)) {
			compDep = "1";
		} else if ("嘉定区教育局".equals(orgName)) {
			compDep = "2";
		} else if ("宝山区教育局".equals(orgName)) {
			compDep = "3";
		} else if ("浦东新区教育局".equals(orgName)) {
			compDep = "4";
		} else if ("松江区教育局".equals(orgName)) {
			compDep = "5";
		} else if ("金山区教育局".equals(orgName)) {
			compDep = "6";
		} else if ("青浦区教育局".equals(orgName)) {
			compDep = "7";
		} else if ("奉贤区教育局".equals(orgName)) {
			compDep = "8";
		} else if ("崇明区教育局".equals(orgName)) {
			compDep = "9";
		} else if ("静安区教育局".equals(orgName)) {
			compDep = "10";
		} else if ("徐汇区教育局".equals(orgName)) {
			compDep = "11";
		} else if ("长宁区教育局".equals(orgName)) {
			compDep = "12";
		} else if ("普陀区教育局".equals(orgName)) {
			compDep = "13";
		} else if ("虹口区教育局".equals(orgName)) {
			compDep = "14";
		} else if ("杨浦区教育局".equals(orgName)) {
			compDep = "15";
		} else if ("闵行区教育局".equals(orgName)) {
			compDep = "16";
		} else if ("市属中职校".equals(orgName)) {
			compDep = "20";
		} else if ("其他".equals(orgName)) {
			compDep = "21";
		}
		return compDep;
	}

	public List<LinkedHashMap<String, Object>> mapSort(List<LinkedHashMap<String, Object>> sortMap, String sortParm) {
		List<LinkedHashMap<String, Object>> list = sortMap;
		Collections.sort(list, new Comparator<LinkedHashMap<String, Object>>() {
			public int compare(LinkedHashMap<String, Object> o1, LinkedHashMap<String, Object> o2) {
				String name1 = (String) o1.get(sortParm);// name1是从你list里面拿出来的一个
				String name2 = (String) o2.get(sortParm); // name1是从你list里面拿出来的第二个name
				return name1.compareTo(name2);
			}
		});
		return list;
	}

	public List<LinkedHashMap<String, Object>> mapIndex(List<LinkedHashMap<String, Object>> sortMap, int curPage) {
		List<LinkedHashMap<String, Object>> list = new ArrayList();
		int sortId = curPage;
		for (LinkedHashMap<String, Object> linkMap : sortMap) {
			sortId += 1;
			linkMap.put("sortId", sortId);
			list.add(linkMap);
		}
		return list;
	}

	// 时间按照毫秒增加
	public String getAddMillisecond(String time, Long millisecond) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = strToDateLong(time);
		Date afterDate = new Date(date.getTime() + millisecond);
		return sdf.format(afterDate);
	}

	// 获取下周一时间
	public String getNextWeekMonday(String task_time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = strToDateLong(task_time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(getThisWeekMonday(date));
		cal.add(Calendar.DATE, 7);
		return sdf.format(cal.getTime());
	}

	public Date getThisWeekMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 获得当前日期是一个星期的第几天
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// 获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		return cal.getTime();
	}

	// 获取body数据
	public String GetBodyJsonReq(HttpServletRequest request, boolean isUtf8Code) {
		BufferedReader br;
		StringBuilder sb = null;
		String reqBody = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			if (isUtf8Code) {
				reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
			} else
				reqBody = sb.toString();
			logger.info("接收Body内容：" + reqBody);
			return reqBody;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// List遍历相同数据合并(map里面某个key相同则合并数据)
	public List<LinkedHashMap<String, Object>> mapGroupBy(List<AppCommonDao> sortMap, String compareKev,
			String sumKey) {
		List<LinkedHashMap<String, Object>> countList = new ArrayList<LinkedHashMap<String, Object>>();// 用于存放最后的结果
		for (int i = 0; i < sortMap.size(); i++) {
			String groupKey = sortMap.get(i).getCommonMap().get(compareKev).toString();

			int flag = 0;// 0为新增数据，1为增加count
			for (int j = 0; j < countList.size(); j++) {
				String groupKey_ = countList.get(j).get(compareKev).toString();

				if (groupKey.equals(groupKey_)) {
					int cpcj_sum = Integer.parseInt(String.valueOf(sortMap.get(i).getCommonMap().get(sumKey)))
							+ Integer.parseInt(String.valueOf(countList.get(j).get(sumKey)));
					countList.get(j).put(sumKey, cpcj_sum);
					flag = 1;
					continue;
				}
			}
			if (flag == 0) {
				countList.add(sortMap.get(i).getCommonMap());
			}
		}
		for (LinkedHashMap<String, Object> map : countList) {
			System.out.println(map);
		}

		return countList;
	}

	// List遍历相同数据合并(map里面某个key相同则合并数据)
	public List<LinkedHashMap<String, Object>> mapBigDecimalGroupBy(List<AppCommonDao> sortMap, String compareKev,
			String compareKev1, String sumKey) {
		List<LinkedHashMap<String, Object>> countList = new ArrayList<LinkedHashMap<String, Object>>();// 用于存放最后的结果

		for (int i = 0; i < sortMap.size(); i++) {
			String groupKey = sortMap.get(i).getCommonMap().get(compareKev).toString();
			String groupKey1 = sortMap.get(i).getCommonMap().get(compareKev1).toString();

			int flag = 0;// 0为新增数据，1为增加count
			for (int j = 0; j < countList.size(); j++) {
				String groupKey_ = countList.get(j).get(compareKev).toString();
				String groupKey1_ = countList.get(j).get(compareKev1).toString();
				if (groupKey.equals(groupKey_) && groupKey1.equals(groupKey1_)) {
					BigDecimal sum = ((BigDecimal) sortMap.get(i).getCommonMap().get(sumKey))
							.add((BigDecimal) countList.get(j).get(sumKey));
					countList.get(j).put(sumKey, sum);
					flag = 1;
					continue;
				}
			}
			if (flag == 0) {
				countList.add(sortMap.get(i).getCommonMap());
			}
		}
		return countList;
	}

	// List遍历相同数据合并(map里面某个key相同则合并数据)
	public List<LinkedHashMap<String, Object>> mapGroupBy(List<AppCommonDao> sortMap, String compareKev,
			String compareKev1, String sumKey) {
		List<LinkedHashMap<String, Object>> countList = new ArrayList<LinkedHashMap<String, Object>>();// 用于存放最后的结果

		for (int i = 0; i < sortMap.size(); i++) {
			String groupKey = sortMap.get(i).getCommonMap().get(compareKev).toString();
			String groupKey1 = sortMap.get(i).getCommonMap().get(compareKev1).toString();

			int flag = 0;// 0为新增数据，1为增加count
			for (int j = 0; j < countList.size(); j++) {
				String groupKey_ = countList.get(j).get(compareKev).toString();
				String groupKey1_ = countList.get(j).get(compareKev1).toString();
				if (groupKey.equals(groupKey_) && groupKey1.equals(groupKey1_)) {
					int sum = ((int) sortMap.get(i).getCommonMap().get(sumKey)) + ((int) countList.get(j).get(sumKey));
					countList.get(j).put(sumKey, sum);
					flag = 1;
					continue;
				}
			}
			if (flag == 0) {
				countList.add(sortMap.get(i).getCommonMap());
			}
		}
		return countList;
	}

	public String currentTime() {
		Date currentDate = new Date();
		SimpleDateFormat currentft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = currentft.format(currentDate);
		return currentTime;
	}

	// 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	public Date strToDateLong(String strDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date strtodate = new Date();
		strtodate = formatter.parse(strDate);
		return strtodate;
	}

	// 计算时间差
	public long getDistanceTime(String startTime, String endTime, long parameter) throws ParseException {
		Date dateStartTime = strToDateLong(startTime);
		Date dateEndTime = strToDateLong(endTime);
		long resLong = 0;
		long time1 = dateStartTime.getTime();
		long time2 = dateEndTime.getTime();

		long diff;
		diff = time2 - time1;

		resLong = (diff / (parameter));
		return resLong;
	}

	// 获取上月1号日期
	public String lastMonth(String endTime) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currdate = strToDateLong(endTime + " 00:00:00");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currdate);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		String res = formatter.format(calendar.getTime()).substring(0, 10);
		return res;

	}

	// 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	public static String dateToStrLong(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	// 处理sql中的年月问题
	public StringBuffer yearMonth(List<String> listYearMonth, StringBuffer sb) {
		String[] arrYearMonth;
		if (listYearMonth.size() > 0) {
			sb.append("AND (");
			for (String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  " + arrYearMonth[0]);
				sb.append(" and month >= " + arrYearMonth[1]);
				sb.append(" and month <= " + arrYearMonth[2] + ") ");

				if (!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
					sb.append(" or ");
				}
			}

			sb.append(") ");
		}
		return sb;
	}

	// 处理sql中的年月问题
	public String yearMonth(List<String> listYearMonth, String sb) {
		String[] arrYearMonth;
		if (listYearMonth.size() > 0) {
			sb += "AND (";
			for (String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb += " (year =  " + arrYearMonth[0];
				sb += " and month >= " + arrYearMonth[1];
				sb += " and month <= " + arrYearMonth[2] + ") ";

				if (!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
					sb += " or ";
				}
			}

			sb += ") ";
		}
		return sb;
	}

	// 排序
	public List<AppCommonDao> getSortMap(List<AppCommonDao> sortMap, String compareKev, String parameter) {
		List<AppCommonDao> listMap = sortMap;
		if ("desc".equals(parameter)) {
			for (int i = 0; i < listMap.size(); i++) {
				for (int j = 0; j < listMap.size() - i - 1; j++) {
					int num1 = (int) listMap.get(j).getCommonMap().get(compareKev);
					int num2 = (int) listMap.get(j + 1).getCommonMap().get(compareKev);
					if (num2 > num1) {
						LinkedHashMap<String, Object> temp = listMap.get(j).getCommonMap();
						LinkedHashMap<String, Object> temp1 = listMap.get(j + 1).getCommonMap();
						LinkedHashMap<String, Object> commonMap1 = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry1 : temp1.entrySet()) {
							commonMap1.put(entry1.getKey(), entry1.getValue());
						}
						listMap.get(j).setCommonMap(commonMap1);

						LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry : temp.entrySet()) {
							commonMap.put(entry.getKey(), entry.getValue());
						}
						listMap.get(j + 1).setCommonMap(commonMap);
					}
				}

			}
		}

		return listMap;
	}

	// 排序
	public List<AppCommonDao> getSortBigDecimalMap(List<AppCommonDao> sortMap, String compareKev, String parameter) {
		List<AppCommonDao> listMap = sortMap;
		if ("desc".equals(parameter)) {
			for (int i = 0; i < listMap.size(); i++) {
				for (int j = 0; j < listMap.size() - i - 1; j++) {
					BigDecimal num1 = (BigDecimal) listMap.get(j).getCommonMap().get(compareKev);
					BigDecimal num2 = (BigDecimal) listMap.get(j + 1).getCommonMap().get(compareKev);
					if (num2.compareTo(num1) == 1) {
						LinkedHashMap<String, Object> temp = listMap.get(j).getCommonMap();
						LinkedHashMap<String, Object> temp1 = listMap.get(j + 1).getCommonMap();
						LinkedHashMap<String, Object> commonMap1 = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry1 : temp1.entrySet()) {
							commonMap1.put(entry1.getKey(), entry1.getValue());
						}
						listMap.get(j).setCommonMap(commonMap1);
						LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry : temp.entrySet()) {
							commonMap.put(entry.getKey(), entry.getValue());
						}
						listMap.get(j + 1).setCommonMap(commonMap);
					}
				}

			}
		}

		return listMap;
	}

	// List<map> 按照某列从大到小排序
	public List<LinkedHashMap<String, Object>> getSortBigDecimalMapFunc(List<LinkedHashMap<String, Object>> listMap,
			String compareKev, String parameter) {
		if ("desc".equals(parameter)) {
			for (int i = 0; i < listMap.size(); i++) {
				int flag = 0;
				for (int j = 0; j < listMap.size() - i - 1; j++) {
					BigDecimal num = (BigDecimal) listMap.get(j).get(compareKev);
					BigDecimal num1 = (BigDecimal) listMap.get(j + 1).get(compareKev);
					if (num1.compareTo(num) == 1) {
						LinkedHashMap<String, Object> temp = listMap.get(j);
						LinkedHashMap<String, Object> temp1 = listMap.get(j + 1);
						LinkedHashMap<String, Object> commonMap1 = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry1 : temp1.entrySet()) {
							commonMap1.put(entry1.getKey(), entry1.getValue());
						}
						listMap.set(j, commonMap1);
						LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry : temp.entrySet()) {
							commonMap.put(entry.getKey(), entry.getValue());
						}
						listMap.set(j + 1, commonMap);
					}
				}

			}
		}

		return listMap;
	}

	// List<map> 按照某列从大到小排序
	public List<LinkedHashMap<String, Object>> getSortMapFunc(List<LinkedHashMap<String, Object>> listMap,
			String compareKev, String parameter) {
		if ("desc".equals(parameter)) {
			for (int i = 0; i < listMap.size(); i++) {
				int flag = 0;
				for (int j = 0; j < listMap.size() - i - 1; j++) {
					int num = (int) listMap.get(j).get(compareKev);
					int num1 = (int) listMap.get(j + 1).get(compareKev);
					if (num1 > num) {
						LinkedHashMap<String, Object> temp = listMap.get(j);
						LinkedHashMap<String, Object> temp1 = listMap.get(j + 1);
						LinkedHashMap<String, Object> commonMap1 = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry1 : temp1.entrySet()) {
							commonMap1.put(entry1.getKey(), entry1.getValue());
						}
						listMap.set(j, commonMap1);
						LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
						for (Entry<String, Object> entry : temp.entrySet()) {
							commonMap.put(entry.getKey(), entry.getValue());
						}
						listMap.set(j + 1, commonMap);
					}
				}

			}
		}

		return listMap;
	}

	// 默认报错返回的json
	public String getInitJson() {
		String nowTime = currentTime();
		String initJson = "{\"time\":" + nowTime
				+ ",\"code\":\"2001\",\"status\":\"error\",\"msgInfo\":\"未知程序错误\",\"data\":[]}";
		return initJson;
	}

	// 接口返回数据，去除多余结构
	public String rmStructure(String strResp) {
		return strResp.replace("[{\"commonMap\":{", "[{").replace("}},{\"commonMap\":{", "},{").replace("}}]}", "}]}");
	}

	// 接口返回数据，去除多余结构
	public String rmExternalStructure(String strResp) {
		return strResp.replace("\"data\":{\"data\"", "\"data\"").replace("},\"dataList\"", ",\"dataList\"");
	}

	// 接口返回数据，去除多余结构,更改dataList名称
	public String rmExternalStructure(String strResp, String dataList) {
		return strResp.replace("\"data\":{\"data\"", "\"data\"").replace("},\"dataList\"", ",\"" + dataList + "\"");
	}

	// 接口返回数据，去除多余结构,更改dataList名称
	public String rmExternalStructure(String strResp, String dataList, String externalList) {
		return strResp.replace("\"data\":{\"data\"", "\"data\"").replace("},\"dataList\"", ",\"" + dataList + "\"")
				.replace(",\"externalList\":[", ",\"" + externalList + "\":[");
	}

	// 生成导出EXCEL文件
	public boolean expExcel(String pathFileName, List<LinkedHashMap<String, Object>> dataList, String[] headerList) {
		boolean retFlag = true;
		Workbook wb = null;
		String excelPath = pathFileName, fileType = "";
		File file = new File(excelPath);
		Sheet sheet = null;
		int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
		if (idx1 != -1)
			fileType = excelPath.substring(idx1 + 1);
		else if (idx2 != -1)
			fileType = excelPath.substring(idx2 + 1);
		// 创建工作文档对象
		if (!file.exists()) { // excel文件不存在
			if (fileType.equals("xls")) {
				wb = new HSSFWorkbook();
			} else if (fileType.equals("xlsx")) {
				wb = new XSSFWorkbook();
			} else {
				retFlag = false;
			}
			// 创建sheet对象
			if (retFlag) {
				sheet = (Sheet) wb.createSheet("sheet1");
				OutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(excelPath);
				} catch (FileNotFoundException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				if (outputStream != null) {
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
				} else
					retFlag = false;
			}

		} else { // excel文件已存在
			if (fileType.equals("xls")) {
				wb = new HSSFWorkbook();
			} else if (fileType.equals("xlsx")) {
				wb = new XSSFWorkbook();
			} else {
				retFlag = false;
			}
		}
		// 创建sheet对象
		if (sheet == null && retFlag) {
			sheet = (Sheet) wb.createSheet("sheet1");
		}
		// 写excel文件数据
		if (sheet != null && retFlag) {
			logger.info("****retFlag 03");
			Integer startRowIdx = 0;

			// 加粗字体
			CellStyle style = AppModConfig.getExcellCellStyle(wb);

			// 浮点型，2位数字格式化格式（2位有效数字+%+边框）
			CellStyle cellStyleFloat = wb.createCellStyle();
			cellStyleFloat.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
			cellStyleFloat.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyleFloat.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyleFloat.setBorderRight(CellStyle.BORDER_THIN);
			cellStyleFloat.setBorderTop(CellStyle.BORDER_THIN);

			// 表格头部样式（加粗字体+边框）
			CellStyle cellStyleHeadBorder = AppModConfig.getExcellCellStyle(wb);
			cellStyleHeadBorder.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyleHeadBorder.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyleHeadBorder.setBorderRight(CellStyle.BORDER_THIN);
			cellStyleHeadBorder.setBorderTop(CellStyle.BORDER_THIN);

			// 边框
			CellStyle styleBorder = AppModConfig.getExcellCellStyleBorder(wb);
			styleBorder.setWrapText(true);// 先设置为自动换行

			// 文字水平垂直居中（表格文字）
			CellStyle styleVerAliCenter = AppModConfig.getExcellCellStyleBorder(wb); // 样式对象
			// 设置单元格的背景颜色为淡蓝色
			styleVerAliCenter.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			styleVerAliCenter.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 垂直
			styleVerAliCenter.setAlignment(CellStyle.ALIGN_CENTER); // 水平
			logger.info("****retFlag 04");
			int columnIndex = 0;
			sheet.setColumnWidth(columnIndex++, 100 * 20);
			sheet.setColumnWidth(columnIndex++, 100 * 100);

			// 获取详情数据
			List<LinkedHashMap<String, Object>> reslutList = new ArrayList<>();
			if (dataList != null) {
				reslutList = dataList;
			}
			// 填充数据到Excel
			startRowIdx = creatOrderTable(pathFileName, excelPath, fileType, sheet, startRowIdx++, style, null,
					styleBorder, cellStyleFloat, cellStyleHeadBorder, styleVerAliCenter, reslutList, headerList);

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
					wb.write(stream);
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

	// 创建数据excel
	private Integer creatOrderTable(String pathFileName, String excelPath, String fileType, Sheet sheet,
			int startRowIdx, CellStyle style, String[] colNames, CellStyle styleBorder, CellStyle cellStyleFloat,
			CellStyle cellStyleHeadBorder, CellStyle styleVerAliCenter, List<LinkedHashMap<String, Object>> reslutList,
			String[] headerList) {
		String[] ColNames;
		Row row;
		Cell cell;
		int rowCount = 0;
		if (reslutList != null && reslutList.size() > 0) {
			// 循环写入行数据
			rowCount = reslutList.size();
			for (int i = 0; i < reslutList.size(); i++) {
				int columIndex = 0;
				if (reslutList.get(i) == null) {
					continue;
				}

				LinkedHashMap<String, Object> dateDiashList = reslutList.get(i);
				// 第一次循环拿去列值
				if (i == 0) {
					ColNames = headerList;
					String[] colVals = new String[ColNames.length];
					creatTableHead(sheet, startRowIdx++, colVals, style, ColNames, cellStyleHeadBorder);
				}

				row = (Row) sheet.createRow(i + startRowIdx);

				cell = row.createCell(columIndex++);
				cell.setCellValue(i + 1); // 序号
				cell.setCellStyle(styleBorder);

				for (Entry<String, Object> searchDatereslutList : dateDiashList.entrySet()) {

					if (searchDatereslutList.getValue() != null) {
						// object类型 自动类型识别
						if (searchDatereslutList.getValue() instanceof Integer) {
							int value = ((Integer) searchDatereslutList.getValue()).intValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						} else if (searchDatereslutList.getValue() instanceof String) {
							String value = (String) searchDatereslutList.getValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						} else if (searchDatereslutList.getValue() instanceof Double) {
							double value = ((Double) searchDatereslutList.getValue()).doubleValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						} else if (searchDatereslutList.getValue() instanceof Float) {
							float value = ((Float) searchDatereslutList.getValue()).floatValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						} else if (searchDatereslutList.getValue() instanceof Long) {
							long value = ((Long) searchDatereslutList.getValue()).longValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						} else if (searchDatereslutList.getValue() instanceof Boolean) {
							boolean value = ((Boolean) searchDatereslutList.getValue()).booleanValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						} else if (searchDatereslutList.getValue() instanceof BigDecimal) {
							float value = ((BigDecimal) searchDatereslutList.getValue()).floatValue();
							cell = row.createCell(columIndex++);
							cell.setCellValue(value);
							cell.setCellStyle(styleBorder);
						}
					}
				}
			}
		}

		return startRowIdx + rowCount + 1;
	}

	private void creatTableHead(Sheet sheet, int startRowIdx, String[] colVals, CellStyle style, String[] areColNames,
			CellStyle cellStyleHeadBorder) {

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
				sheet.setColumnWidth(i + 2, 100 * 30);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
}
