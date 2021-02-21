package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperContDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperSubjectDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionBodyDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionCandAnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamPaperDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamPaperDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamQuestion;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamTopics;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//在线考试试卷详情应用模型
public class OeExamPaperDetAppMod {
	private static final Logger logger = LogManager.getLogger(OeExamPaperDetAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	
	//数组数据初始化	
	String epId = "319d4ab7-5f60-4e99-9f32-495f024a0286";
	String epName = "试卷I";
	String epTitel = "食品安全培训测试";
	String remark = "（总分100分，考试时间30分钟）";
	String trueFalseTopics_subject = "一、判断题(每题5分,漏选或错选均不得分)";
	String[] trueFalseTopics_questions_bodyId_Array = {"9184793d-335e-41d1-8750-abf448a06303", "0295793d-335e-41d1-8750-abf448a06303"};
    String[] trueFalseTopics_questions_body_Array = {"1.承担食品添加剂生产许可发证检验工作的检验机构，应当具备法定资质并由国家质检总局统一发布名录。", "2.食品和食品添加剂与其标签、说明书所载的内容不符的，不得上市销售。"};
    String[] trueFalseTopics_questions_candAns_Array = {"正确;错误", "正确;错误"};
    String[] trueFalseTopics_questions_candAnsId_Array = {"1;0", "1;0"};
    String singleChoiceTopic_subject = "二、单选题(每题5分,漏选或错选均不得分)";
    String[] singleChoiceTopic_questions_bodyId_Array = {"1306793d-335e-41d1-8750-abf448a06303", "2417793d-335e-41d1-8750-abf448a06303"};
    String[] singleChoiceTopic_questions_body_Array = {"1.《食品安全法》于 ______ 起实施。", "2.工商行政管理部门在食品安全监督管理中负责对 ______ 领域实施监督管理。"};
    String[] singleChoiceTopic_questions_candAns_Array = {"A. 2009年2月28日;B. 2009年5月1日;C. 2009年6月1;D. 2009年10月1日", "A. 食品生产;B. 食品流通;C. 餐饮服务;D. 以上全是"};
    String[] singleChoiceTopic_questions_candAnsId_Array = {"2;3;4;5", "6;7;8;9"};
    String multiChoiceTopic_subject = "三、多选题(每题15分,漏选或错选均不得分)";
    String[] multiChoiceTopic_questions_bodyId_Array = {"3528793d-335e-41d1-8750-abf448a06303", "4539793d-335e-41d1-8750-abf448a06303"};
    String[] multiChoiceTopic_questions_body_Array = {" 1.食品安全标准包括以下内容 ______ 。", " 2.食品添加剂应当有 ______ 。"};
    String[] multiChoiceTopic_questions_candAns_Array = {"A. 食品添加剂的品种、使用范围、用量;B. 专供婴幼儿和其他特定人群的主辅食品的营养成分要求;C .食品检验方法与规程;D. 食品、食品相关产品中的致病性微生物、农药残留、兽药残留、重金属、污染物质以及其他危害人体健康物质的限量规定", "A. 标签;B. 说明书;C. 包装;D. 商标"};
    String[] multiChoiceTopic_questions_candAnsId_Array = {"10;11;12;13", "14;15;16;17"};
	//模拟数据函数
	private OeExamPaperDetDTO SimuDataFunc() {
		OeExamPaperDetDTO oepdDto = new OeExamPaperDetDTO();
		//时戳
		oepdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//在线考试试卷详情模拟数据
		OeExamPaperDet oeExamPaperDet = new OeExamPaperDet();
		//赋值
		oeExamPaperDet.setEpId(epId);
		oeExamPaperDet.setEpName(epName);
		oeExamPaperDet.setEpTitel(epTitel);
		oeExamPaperDet.setRemark(remark);
		//判断题
		OeExamTopics trueFalseTopics = new OeExamTopics();
		trueFalseTopics.setSubject(trueFalseTopics_subject);
		List<OeExamQuestion> questions1 = new ArrayList<>();
		for (int i = 0; i < trueFalseTopics_questions_bodyId_Array.length; i++) {
			OeExamQuestion oeq = new OeExamQuestion();
			oeq.setBodyId(trueFalseTopics_questions_bodyId_Array[i]);
			oeq.setBody(trueFalseTopics_questions_body_Array[i]);
			oeq.setCandAns(trueFalseTopics_questions_candAns_Array[i]);
			oeq.setCandAnsId(trueFalseTopics_questions_candAnsId_Array[i]);
			questions1.add(oeq);
		}
		trueFalseTopics.setQuestions(questions1);
		oeExamPaperDet.setTrueFalseTopics(trueFalseTopics);
		//单选题
		OeExamTopics singleChoiceTopic = new OeExamTopics();
		singleChoiceTopic.setSubject(singleChoiceTopic_subject);
		List<OeExamQuestion> questions2 = new ArrayList<>();
		for (int i = 0; i < singleChoiceTopic_questions_bodyId_Array.length; i++) {
			OeExamQuestion oeq = new OeExamQuestion();
			oeq.setBodyId(singleChoiceTopic_questions_bodyId_Array[i]);
			oeq.setBody(singleChoiceTopic_questions_body_Array[i]);
			oeq.setCandAns(singleChoiceTopic_questions_candAns_Array[i]);
			oeq.setCandAnsId(singleChoiceTopic_questions_candAnsId_Array[i]);
			questions2.add(oeq);
		}
		singleChoiceTopic.setQuestions(questions2);
		oeExamPaperDet.setSingleChoiceTopic(singleChoiceTopic);
		//多选题
		OeExamTopics multiChoiceTopic = new OeExamTopics();
		multiChoiceTopic.setSubject(multiChoiceTopic_subject);
		List<OeExamQuestion> questions3 = new ArrayList<>();
		for (int i = 0; i < multiChoiceTopic_questions_bodyId_Array.length; i++) {
			OeExamQuestion oeq = new OeExamQuestion();
			oeq.setBodyId(multiChoiceTopic_questions_bodyId_Array[i]);
			oeq.setBody(multiChoiceTopic_questions_body_Array[i]);
			oeq.setCandAns(multiChoiceTopic_questions_candAns_Array[i]);
			oeq.setCandAnsId(multiChoiceTopic_questions_candAnsId_Array[i]);
			questions3.add(oeq);
		}
		multiChoiceTopic.setQuestions(questions3);
		oeExamPaperDet.setMultiChoiceTopic(multiChoiceTopic);
		//设置数据
		oepdDto.setOeExamPaperDet(oeExamPaperDet);
		//消息ID
		oepdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return oepdDto;
	}
	
	// 在线考试试卷详情模型函数
	public OeExamPaperDetDTO appModFunc(String token, String epId, String distName, String prefCity, String province, Db1Service db1Service, Db2Service db2Service) {
		OeExamPaperDetDTO oepdDto = null;
		if(isRealData) {       //真实数据
			if(epId != null && db2Service != null) {
				oepdDto = new OeExamPaperDetDTO();
				//时戳
				oepdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//在线考试试卷详情数据
				OeExamPaperDet oeExamPaperDet = new OeExamPaperDet();
				OeExamTopics trueFalseTopics = new OeExamTopics();
				List<OeExamQuestion> questions1 = new ArrayList<>();
				OeExamTopics singleChoiceTopic = new OeExamTopics();
				List<OeExamQuestion> questions2 = new ArrayList<>();
				OeExamTopics multiChoiceTopic = new OeExamTopics();
				List<OeExamQuestion> questions3 = new ArrayList<>();
				//获取所有试卷信息以试卷ID
			    TEduBdExamPaperDo tebepDo = db2Service.getTEduBdExamPaperDoById(epId);
			    if(tebepDo != null) {
			    	oeExamPaperDet.setEpId(tebepDo.getId());                   //试卷ID，试卷唯一标识
			    	oeExamPaperDet.setEpName(tebepDo.getName());               //试卷名称
			    	oeExamPaperDet.setEpTitel(tebepDo.getTitle());             //试卷主题
			    	oeExamPaperDet.setRemark(tebepDo.getRemark());             //试卷备注
			    	int[] questionSns = {0, 0, 0};
			    	//获取所有试卷内容以试卷ID
				    List<TEduBdExamPaperContDo> tebepcDoList = db2Service.getTEduBdExamPaperContDosByEpId(epId);
				    if(tebepcDoList != null) {
				    	for(int i = 0; i < tebepcDoList.size(); i++) {
				    		TEduBdExamPaperContDo tebepcDo = tebepcDoList.get(i);
				    		//获取试题以试题ID
				    	    TEduBdQuestionBodyDo tebqbDo = db2Service.getTEduBdQuestionBodyDoById(tebepcDo.getQuestionId());
				    	    //获取候选答案以试题ID
				    	    List<TEduBdQuestionCandAnsDo> tebqcaDoList = db2Service.getTEduBdQuestionCandAnsDoByQuestionId(tebepcDo.getQuestionId());
				    		OeExamQuestion oeq = new OeExamQuestion();
				    		oeq.setBodyId(tebepcDo.getQuestionId());      //试题ID
				    		if(tebepcDo.getQuestionType() == 0) {     //判断题
				    			questionSns[0]++;
				    			if(tebqbDo != null) {                         //试题题干
					    			oeq.setBody(questionSns[0] + "、" + tebqbDo.getBody());
					    		}
				    			if(tebqcaDoList != null) {      //候选答案
					    			String candAns = "", candAnsId = "";
					    			for(int j = 0; j < tebqcaDoList.size(); j++) {
					    				TEduBdQuestionCandAnsDo tebqcaDo = tebqcaDoList.get(j);
					    				if(j < tebqcaDoList.size() -1) {
					    					candAns += (tebqcaDo.getCandAnsDescr() + ";");
					    					candAnsId += (tebqcaDo.getId() + ";");
					    				}
					    				else {
					    					candAns += tebqcaDo.getCandAnsDescr();
					    					candAnsId += tebqcaDo.getId();
					    				}
					    			}
					    			oeq.setCandAns(candAns);
					    			oeq.setCandAnsId(candAnsId);
					    		}
				    			questions1.add(oeq);
				    		}
				    		else if(tebepcDo.getQuestionType() == 1) {     //单选题
				    			questionSns[1]++;
				    			if(tebqbDo != null) {                         //试题题干
					    			oeq.setBody(questionSns[1] + "、" + tebqbDo.getBody());
					    		}
				    			if(tebqcaDoList != null) {      //候选答案
					    			String candAns = "", candAnsId = "";
					    			for(int j = 0; j < tebqcaDoList.size() && j < AppModConfig.choiceCandAnsSnIdToNameMap.size(); j++) {
					    				TEduBdQuestionCandAnsDo tebqcaDo = tebqcaDoList.get(j);
					    				if(j < tebqcaDoList.size() -1) {
					    					candAns += (AppModConfig.choiceCandAnsSnIdToNameMap.get(j) + "、" + tebqcaDo.getCandAnsDescr() + ";");
					    					candAnsId += (tebqcaDo.getId() + ";");
					    				}
					    				else {
					    					candAns += (AppModConfig.choiceCandAnsSnIdToNameMap.get(j) + "、" + tebqcaDo.getCandAnsDescr());
					    					candAnsId += tebqcaDo.getId();
					    				}
					    			}
					    			oeq.setCandAns(candAns);
					    			oeq.setCandAnsId(candAnsId);
					    		}
				    			questions2.add(oeq);
				    		}
				    		else if(tebepcDo.getQuestionType() == 2) {     //多选题
				    			questionSns[2]++;
				    			if(tebqbDo != null) {                         //试题题干
					    			oeq.setBody(questionSns[2] + "、" + tebqbDo.getBody());
					    		}
				    			if(tebqcaDoList != null) {      //候选答案
					    			String candAns = "", candAnsId = "";
					    			for(int j = 0; j < tebqcaDoList.size() && j < AppModConfig.choiceCandAnsSnIdToNameMap.size(); j++) {
					    				TEduBdQuestionCandAnsDo tebqcaDo = tebqcaDoList.get(j);
					    				if(j < tebqcaDoList.size() -1) {
					    					candAns += (AppModConfig.choiceCandAnsSnIdToNameMap.get(j) + "、" + tebqcaDo.getCandAnsDescr() + ";");
					    					candAnsId += (tebqcaDo.getId() + ";");
					    				}
					    				else {
					    					candAns += (AppModConfig.choiceCandAnsSnIdToNameMap.get(j) + "、" + tebqcaDo.getCandAnsDescr());
					    					candAnsId += tebqcaDo.getId();
					    				}
					    			}
					    			oeq.setCandAns(candAns);
					    			oeq.setCandAnsId(candAnsId);
					    		}
				    			questions3.add(oeq);
				    		}
				    		else if(tebepcDo.getQuestionType() == 3) {     //填空题
				    			
				    		}
				    		else if(tebepcDo.getQuestionType() == 4) {     //问答题
				    			
				    		}
				    	}
				    	//判断题
				    	TEduBdExamPaperSubjectDo tebepsDo1 = db2Service.getTEduBdExamPaperSubjectDoByEpIdQuestionType(epId, 0);
				    	if(tebepsDo1 != null) {
				    		trueFalseTopics.setSubject("一、" + tebepsDo1.getSubjectDescr());
				    	}
				    	trueFalseTopics.setQuestions(questions1);
				    	oeExamPaperDet.setTrueFalseTopics(trueFalseTopics);
				    	//单选题
				    	TEduBdExamPaperSubjectDo tebepsDo2 = db2Service.getTEduBdExamPaperSubjectDoByEpIdQuestionType(epId, 1);
				    	if(tebepsDo2 != null) {
				    		singleChoiceTopic.setSubject("二、" + tebepsDo2.getSubjectDescr());
				    	}
				    	singleChoiceTopic.setQuestions(questions2);
				    	oeExamPaperDet.setSingleChoiceTopic(singleChoiceTopic);
				    	//多选题
				    	TEduBdExamPaperSubjectDo tebepsDo3 = db2Service.getTEduBdExamPaperSubjectDoByEpIdQuestionType(epId, 2);
				    	if(tebepsDo3 != null) {
				    		multiChoiceTopic.setSubject("三、" + tebepsDo3.getSubjectDescr());
				    	}
				    	multiChoiceTopic.setQuestions(questions3);
				    	oeExamPaperDet.setMultiChoiceTopic(multiChoiceTopic);
				    }
				    //设置数据
					oepdDto.setOeExamPaperDet(oeExamPaperDet);
					//消息ID
					oepdDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
			    }
			}	
			else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			oepdDto = SimuDataFunc();
		}		

		return oepdDto;
	}
}
