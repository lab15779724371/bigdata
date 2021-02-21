package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamPapers;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamPapersDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//在线考试试卷列表应用模型
public class OeExamPapersAppMod {
	private static final Logger logger = LogManager.getLogger(OeExamPapersAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化	
	String[] epId_Array = {"319d4ab7-5f60-4e99-9f32-495f024a0286", "4c33693d-335e-41d1-8750-abf448a06303", "5d43693d-335e-41d1-8750-abf448a06303", "6e5d4ab7-5f60-4e99-9f32-495f024a0286", "7f63693d-335e-41d1-8750-abf448a06303", "8073693d-335e-41d1-8750-abf448a06303"};
	String[] epName_Array = {"试卷I", "试卷II", "试卷III", "试卷I", "试卷II", "试卷III"};
	int[] epCategory_Array = {1, 1, 1, 2, 2, 2};
	
	//模拟数据函数
	private OeExamPapersDTO SimuDataFunc() {
		OeExamPapersDTO oepDto = new OeExamPapersDTO();
		//时戳
		oepDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//在线考试试卷列表模拟数据
		List<OeExamPapers> oeExamPapers = new ArrayList<>();
		//赋值
		for (int i = 0; i < epId_Array.length; i++) {
			OeExamPapers oep = new OeExamPapers();
			oep.setEpId(epId_Array[i]);
			oep.setEpName(epName_Array[i]);
			oep.setEpCategory(epCategory_Array[i]);
			oeExamPapers.add(oep);
		}
		//设置数据
		oepDto.setOeExamPapers(oeExamPapers);
		// 分页
		PageBean<OeExamPapers> pageBean = new PageBean<OeExamPapers>(oeExamPapers, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		oepDto.setPageInfo(pageInfo);
		//消息ID
		oepDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return oepDto;
	}
	
	// 在线考试试卷列表函数
	OeExamPapersDTO oeExamPapers(int epCategory, Db1Service db1Service, Db2Service db2Service) {
		OeExamPapersDTO oepDto = null;
		//获取所有试卷
		List<TEduBdExamPaperDo> tebepDoList = db2Service.getAllTEduBdExamPaperDo();
		if(tebepDoList != null) {
			oepDto = new OeExamPapersDTO();
			//时戳
			oepDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//在线考试试卷列表模拟数据
			List<OeExamPapers> oeExamPapers = new ArrayList<>();
			//赋值
			for (int k = 0; k < tebepDoList.size(); k++) {
				OeExamPapers oep = new OeExamPapers();
				oep.setEpId(tebepDoList.get(k).getId());
				oep.setEpName(tebepDoList.get(k).getName());
				oep.setEpCategory(tebepDoList.get(k).getCategory());
				//条件判断
				boolean isAdd = true;
				int[] flIdxs = new int[1];
				//试卷分类，1:系统操作，2:食品安全，3:政策法规
				if(epCategory != -1) {
					if(oep.getEpCategory() != epCategory)
						flIdxs[0] = -1;
				}
				//总体条件判断
				for(int i = 0; i < flIdxs.length; i++) {
					if(flIdxs[i] == -1) {
						isAdd = false;
						break;
					}
				}
				//是否满足条件
				if(isAdd)
					oeExamPapers.add(oep);
			}
			//排序
	    	SortList<OeExamPapers> sortList = new SortList<OeExamPapers>();  
	    	sortList.Sort(oeExamPapers, "getEpName", "asc");
			//设置数据
			oepDto.setOeExamPapers(oeExamPapers);
			// 分页
			PageBean<OeExamPapers> pageBean = new PageBean<OeExamPapers>(oeExamPapers, curPageNum, pageSize);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageTotal(pageBean.getTotalCount());
			pageInfo.setCurPageNum(curPageNum);
			oepDto.setPageInfo(pageInfo);
			//消息ID
			oepDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		
		return oepDto;
	}
	
	// 在线考试试卷列表模型函数
	public OeExamPapersDTO appModFunc(String token, String epCategory, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		OeExamPapersDTO oepDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//试卷分类，1:系统操作，2:食品安全，3:政策法规
			int curEpCategory = -1;
			if(epCategory != null)
				curEpCategory = Integer.parseInt(epCategory);
			if(db2Service != null) {
				oepDto = oeExamPapers(curEpCategory, db1Service, db2Service);
			}
			else {
				logger.info("访问接口参数非法！");
			}	
		}
		else {    //模拟数据
			//模拟数据函数
			oepDto = SimuDataFunc();
		}		

		return oepDto;
	}
}