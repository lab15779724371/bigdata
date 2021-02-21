package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperContDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperSubjectDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionBodyDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionCandAnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeSubAnsBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeSubAns;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeSubAnsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeSubExamQuestion;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeSubExamTopics;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.SubEpCont;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//在线考试提交答案应用模型
public class OeSubAnsAppMod {
	private static final Logger logger = LogManager.getLogger(OeSubAnsAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
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
    String[] trueFalseTopics_questions_eeAns_Array = {"正确", "错误"};
    int[] trueFalseTopics_questions_eeAnsJuge_Array = {1, 0};
    String[] trueFalseTopics_questions_stdAns_Array = {"正确", "正确"};
    String singleChoiceTopic_subject = "二、单选题(每题5分,漏选或错选均不得分)";
    String[] singleChoiceTopic_questions_bodyId_Array = {"1306793d-335e-41d1-8750-abf448a06303", "2417793d-335e-41d1-8750-abf448a06303"};
    String[] singleChoiceTopic_questions_body_Array = {"1.《食品安全法》于 ______ 起实施。", "2.工商行政管理部门在食品安全监督管理中负责对 ______ 领域实施监督管理。"};
    String[] singleChoiceTopic_questions_candAns_Array = {"A. 2009年2月28日;B. 2009年5月1日;C. 2009年6月1;D. 2009年10月1日", "A. 食品生产;B. 食品流通;C. 餐饮服务;D. 以上全是"};
    String[] singleChoiceTopic_questions_candAnsId_Array = {"2;3;4;5", "6;7;8;9"};    
    String[] singleChoiceTopic_questions_eeAns_Array = {"C", "B"};
    int[] singleChoiceTopic_questions_eeAnsJuge_Array = {1, 1};
    String[] singleChoiceTopic_questions_stdAns_Array = {"C", "B"};    
    String multiChoiceTopic_subject = "三、多选题(每题15分,漏选或错选均不得分)";
    String[] multiChoiceTopic_questions_bodyId_Array = {"3528793d-335e-41d1-8750-abf448a06303", "4539793d-335e-41d1-8750-abf448a06303"};
    String[] multiChoiceTopic_questions_body_Array = {" 1.食品安全标准包括以下内容 ______ 。", " 2.食品添加剂应当有 ______ 。"};
    String[] multiChoiceTopic_questions_candAns_Array = {"A. 食品添加剂的品种、使用范围、用量;B. 专供婴幼儿和其他特定人群的主辅食品的营养成分要求;C .食品检验方法与规程;D. 食品、食品相关产品中的致病性微生物、农药残留、兽药残留、重金属、污染物质以及其他危害人体健康物质的限量规定", "A. 标签;B. 说明书;C. 包装;D. 商标"};
    String[] multiChoiceTopic_questions_candAnsId_Array = {"10;11;12;13", "14;15;16;17"};    
    String[] multiChoiceTopic_questions_eeAns_Array = {"ABC", "ABC"};
    int[] multiChoiceTopic_questions_eeAnsJuge_Array = {0, 1};
    String[] multiChoiceTopic_questions_stdAns_Array = {"ABCD", "ABC"};
	//模拟数据函数
	private OeSubAnsDTO SimuDataFunc() {
		OeSubAnsDTO osaDto = new OeSubAnsDTO();
		//时戳
		osaDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//在线考试提交答案模拟数据
		OeSubAns oeSubAns = new OeSubAns();
		//赋值
		oeSubAns.setEpId(epId);
		oeSubAns.setEpName(epName);
		oeSubAns.setEpTitel(epTitel);
		oeSubAns.setRemark(remark);
		//判断题
		OeSubExamTopics trueFalseTopics = new OeSubExamTopics();
		trueFalseTopics.setSubject(trueFalseTopics_subject);
		List<OeSubExamQuestion> questions1 = new ArrayList<>();
		for (int i = 0; i < trueFalseTopics_questions_bodyId_Array.length; i++) {
			OeSubExamQuestion oseq = new OeSubExamQuestion();
			oseq.setBodyId(trueFalseTopics_questions_bodyId_Array[i]);
			oseq.setBody(trueFalseTopics_questions_body_Array[i]);
			oseq.setCandAns(trueFalseTopics_questions_candAns_Array[i]);
			oseq.setCandAnsId(trueFalseTopics_questions_candAnsId_Array[i]);
			oseq.setEeAns(trueFalseTopics_questions_eeAns_Array[i]);
			oseq.setEeAnsJuge(trueFalseTopics_questions_eeAnsJuge_Array[i]);
			oseq.setStdAns(trueFalseTopics_questions_stdAns_Array[i]);
			questions1.add(oseq);
		}
		trueFalseTopics.setQuestions(questions1);
		oeSubAns.setTrueFalseTopics(trueFalseTopics);
		//单选题
		OeSubExamTopics singleChoiceTopic = new OeSubExamTopics();
		singleChoiceTopic.setSubject(singleChoiceTopic_subject);
		List<OeSubExamQuestion> questions2 = new ArrayList<>();
		for (int i = 0; i < singleChoiceTopic_questions_bodyId_Array.length; i++) {
			OeSubExamQuestion oseq = new OeSubExamQuestion();
			oseq.setBodyId(singleChoiceTopic_questions_bodyId_Array[i]);
			oseq.setBody(singleChoiceTopic_questions_body_Array[i]);
			oseq.setCandAns(singleChoiceTopic_questions_candAns_Array[i]);
			oseq.setCandAnsId(singleChoiceTopic_questions_candAnsId_Array[i]);
			oseq.setEeAns(singleChoiceTopic_questions_eeAns_Array[i]);
			oseq.setEeAnsJuge(singleChoiceTopic_questions_eeAnsJuge_Array[i]);
			oseq.setStdAns(singleChoiceTopic_questions_stdAns_Array[i]);
			questions2.add(oseq);
		}
		singleChoiceTopic.setQuestions(questions2);
		oeSubAns.setSingleChoiceTopic(singleChoiceTopic);
		//多选题
		OeSubExamTopics multiChoiceTopic = new OeSubExamTopics();
		multiChoiceTopic.setSubject(multiChoiceTopic_subject);
		List<OeSubExamQuestion> questions3 = new ArrayList<>();
		for (int i = 0; i < multiChoiceTopic_questions_bodyId_Array.length; i++) {
			OeSubExamQuestion oseq = new OeSubExamQuestion();
			oseq.setBodyId(multiChoiceTopic_questions_bodyId_Array[i]);
			oseq.setBody(multiChoiceTopic_questions_body_Array[i]);
			oseq.setCandAns(multiChoiceTopic_questions_candAns_Array[i]);
			oseq.setCandAnsId(multiChoiceTopic_questions_candAnsId_Array[i]);
			oseq.setEeAns(multiChoiceTopic_questions_eeAns_Array[i]);
			oseq.setEeAnsJuge(multiChoiceTopic_questions_eeAnsJuge_Array[i]);
			oseq.setStdAns(multiChoiceTopic_questions_stdAns_Array[i]);
			questions3.add(oseq);
		}
		multiChoiceTopic.setQuestions(questions3);
		oeSubAns.setMultiChoiceTopic(multiChoiceTopic);
		//设置数据
		osaDto.setOeSubAns(oeSubAns);
		//消息ID
		osaDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return osaDto;
	}
	
	// 在线考试提交答案模型函数
	public OeSubAnsDTO appModFunc(String token, String strBodyCont, Db1Service db1Service, Db2Service db2Service) {
		OeSubAnsDTO osaDto = null;
		if(isRealData) {       //真实数据
			OeSubAnsBody osab = null;
  			try {
  				if(strBodyCont != null)
  					osab = objectMapper.readValue(strBodyCont, OeSubAnsBody.class);
			} catch (JsonParseException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			if(db2Service != null && osab != null && osab.getEpId() != null) {					
				osaDto = new OeSubAnsDTO();
				//时戳
				osaDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//在线考试试卷详情数据
				OeSubAns oeSubAns = new OeSubAns();
				OeSubExamTopics trueFalseTopics = new OeSubExamTopics();
				List<OeSubExamQuestion> questions1 = new ArrayList<>();
				OeSubExamTopics singleChoiceTopic = new OeSubExamTopics();
				List<OeSubExamQuestion> questions2 = new ArrayList<>();
				OeSubExamTopics multiChoiceTopic = new OeSubExamTopics();
				List<OeSubExamQuestion> questions3 = new ArrayList<>();
				//试题ID映射提交答案
				Map<String, String> questionIdToSubAnsMap = new HashMap<>();
				List<SubEpCont> subCont = osab.getSubCont();
				if(subCont != null) {
					for(int i = 0; i < subCont.size(); i++) {
						questionIdToSubAnsMap.put(subCont.get(i).getBodyId(), subCont.get(i).getSubAnsId());
					}
				}
				//试卷试题ID与其索引映射
				List<TEduBdExamPaperContDo> tebepcDoList = null;
				Map<String, Integer> questionIdToEpcIdxMap = new HashMap<>();
				//获取所有试卷信息以试卷ID
			    TEduBdExamPaperDo tebepDo = db2Service.getTEduBdExamPaperDoById(osab.getEpId());
			    if(tebepDo != null) {
			    	oeSubAns.setEpId(tebepDo.getId());                   //试卷ID，试卷唯一标识
			    	oeSubAns.setEpName(tebepDo.getName());               //试卷名称
			    	oeSubAns.setEpTitel(tebepDo.getTitle());             //试卷主题
			    	oeSubAns.setRemark(tebepDo.getRemark());             //试卷备注
			    	int[] questionSns = {0, 0, 0};
			    	//获取所有试卷内容以试卷ID
				    tebepcDoList = db2Service.getTEduBdExamPaperContDosByEpId(osab.getEpId());
				    if(tebepcDoList != null) {
				    	for(int i = 0; i < tebepcDoList.size(); i++) {
				    		TEduBdExamPaperContDo tebepcDo = tebepcDoList.get(i);
				    		questionIdToEpcIdxMap.put(tebepcDo.getQuestionId(), i);
				    		//获取试题以试题ID
				    	    TEduBdQuestionBodyDo tebqbDo = db2Service.getTEduBdQuestionBodyDoById(tebepcDo.getQuestionId());
				    	    //获取候选答案以试题ID
				    	    List<TEduBdQuestionCandAnsDo> tebqcaDoList = db2Service.getTEduBdQuestionCandAnsDoByQuestionId(tebepcDo.getQuestionId());
				    		OeSubExamQuestion oseq = new OeSubExamQuestion();
				    		oseq.setBodyId(tebepcDo.getQuestionId());      //试题ID
				    		if(tebepcDo.getQuestionType() == 0) {     //判断题
				    			questionSns[0]++;
				    			if(tebqbDo != null) {                         //试题题干
					    			oseq.setBody(questionSns[0] + "、" + tebqbDo.getBody());
					    		}
				    			if(tebqcaDoList != null) {      //候选答案
					    			String candAns = "", candAnsId = "", subAns = "", subAnsId = "", stdAns = "";
					    			//提取提交答案
					    			if(questionIdToSubAnsMap.containsKey(oseq.getBodyId())) {
					    				subAnsId = questionIdToSubAnsMap.get(oseq.getBodyId());
					    			}
					    			String[] subAnsIds = subAnsId.split(";");
					    			//提交答案评判
					    			boolean initFlag1 = true, initFlag2 = true;
					    			int eeAnsJuge = 0, selWrongAns = 0;
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
					    				for(int k = 0; k < subAnsIds.length; k++) {
					    					//提交答案ID与候选答案ID比较
					    					if(subAnsIds[k].equals(tebqcaDo.getId())) {
					    						if(initFlag1) {
					    							subAns += tebqcaDo.getCandAnsDescr();
					    							initFlag1 = false;
					    						}
					    						else {
					    							subAns += (";" + tebqcaDo.getCandAnsDescr());
					    						}
					    						//提交答案正确
					    						if(tebqcaDo.getStdAnsFlag() == 1) {
					    							eeAnsJuge++;
					    						}
					    						else {
					    							selWrongAns++;
					    						}
					    					}					    					
					    				}
					    				//标准答案
					    				if(tebqcaDo.getStdAnsFlag() == 1) {
			    							if(initFlag2) {
			    								stdAns += tebqcaDo.getCandAnsDescr();
			    								initFlag2 = false;
			    							}
			    							else {
			    								stdAns += (";" + tebqcaDo.getCandAnsDescr());
			    							}
			    						}
					    			}
					    			//设置候选答案及其ID
					    			oseq.setCandAns(candAns);
					    			oseq.setCandAnsId(candAnsId);
					    			//设置提交答案
					    			oseq.setEeAns(subAns);
					    			//设置答案判断
					    			if(eeAnsJuge > 0 && selWrongAns == 0)
					    				eeAnsJuge = 1;
					    			else
					    				eeAnsJuge = 0;
					    			oseq.setEeAnsJuge(eeAnsJuge);
					    			//设置标准答案
					    			oseq.setStdAns(stdAns);
					    		}
				    			questions1.add(oseq);
				    		}
				    		else if(tebepcDo.getQuestionType() == 1) {     //单选题
				    			questionSns[1]++;
				    			if(tebqbDo != null) {                         //试题题干
					    			oseq.setBody(questionSns[1] + "、" + tebqbDo.getBody());
					    		}
				    			if(tebqcaDoList != null) {      //候选答案
					    			String candAns = "", candAnsId = "", subAns = "", subAnsId = "", stdAns = "";
					    			//提取提交答案
					    			if(questionIdToSubAnsMap.containsKey(oseq.getBodyId())) {
					    				subAnsId = questionIdToSubAnsMap.get(oseq.getBodyId());
					    			}
					    			String[] subAnsIds = subAnsId.split(";");
					    			//提交答案评判
					    			boolean initFlag1 = true, initFlag2 = true;
					    			int eeAnsJuge = 0, selWrongAns = 0;
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
					    				for(int k = 0; k < subAnsIds.length; k++) {
					    					//提交答案ID与候选答案ID比较
					    					if(subAnsIds[k].equals(tebqcaDo.getId())) {
					    						if(initFlag1) {
					    							subAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
					    							initFlag1 = false;
					    						}
					    						else {
					    							subAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
					    						}
					    						//提交答案正确
					    						if(tebqcaDo.getStdAnsFlag() == 1) {
					    							eeAnsJuge++;
					    						}
					    						else {
					    							selWrongAns++;
					    						}
					    					}					    					
					    				}
					    				//标准答案
					    				if(tebqcaDo.getStdAnsFlag() == 1) {
			    							if(initFlag2) {
			    								stdAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
			    								initFlag2 = false;
			    							}
			    							else {
			    								stdAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
			    							}
			    						}
					    			}
					    			//设置候选答案及其ID
					    			oseq.setCandAns(candAns);
					    			oseq.setCandAnsId(candAnsId);
					    			//设置提交答案
					    			oseq.setEeAns(subAns);
					    			//设置答案判断
					    			if(eeAnsJuge > 0 && selWrongAns == 0)
					    				eeAnsJuge = 1;
					    			else
					    				eeAnsJuge = 0;
					    			oseq.setEeAnsJuge(eeAnsJuge);
					    			//设置标准答案
					    			oseq.setStdAns(stdAns);
					    		}
				    			questions2.add(oseq);
				    		}
				    		else if(tebepcDo.getQuestionType() == 2) {     //多选题
				    			questionSns[2]++;
				    			if(tebqbDo != null) {                         //试题题干
					    			oseq.setBody(questionSns[2] + "、" + tebqbDo.getBody());
					    		}
				    			if(tebqcaDoList != null) {      //候选答案
					    			String candAns = "", candAnsId = "", subAns = "", subAnsId = "", stdAns = "";
					    			//提取提交答案
					    			if(questionIdToSubAnsMap.containsKey(oseq.getBodyId())) {
					    				subAnsId = questionIdToSubAnsMap.get(oseq.getBodyId());
					    			}
					    			String[] subAnsIds = subAnsId.split(";");
					    			//提交答案评判
					    			boolean initFlag1 = true, initFlag2 = true;
					    			int eeAnsJuge = 0, stdAnsCount = 0, selWrongAns = 0;
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
					    				for(int k = 0; k < subAnsIds.length; k++) {
					    					//提交答案ID与候选答案ID比较
					    					if(subAnsIds[k].equals(tebqcaDo.getId())) {
					    						if(initFlag1) {
					    							subAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
					    							initFlag1 = false;
					    						}
					    						else {
					    							subAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
					    						}
					    						//提交答案正确
					    						if(tebqcaDo.getStdAnsFlag() == 1) {
					    							eeAnsJuge++;
					    						}
					    						else {
					    							selWrongAns++;
					    						}
					    					}					    					
					    				}
					    				//标准答案
					    				if(tebqcaDo.getStdAnsFlag() == 1) {
					    					stdAnsCount++;
			    							if(initFlag2) {
			    								stdAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
			    								initFlag2 = false;
			    							}
			    							else {
			    								stdAns += AppModConfig.choiceCandAnsSnIdToNameMap.get(j);
			    							}
			    						}
					    			}
					    			//设置候选答案及其ID
					    			oseq.setCandAns(candAns);
					    			oseq.setCandAnsId(candAnsId);
					    			//设置提交答案
					    			oseq.setEeAns(subAns);
					    			//设置答案判断
					    			if(eeAnsJuge == stdAnsCount && selWrongAns == 0)
					    				eeAnsJuge = 1;
					    			else
					    				eeAnsJuge = 0;
					    			oseq.setEeAnsJuge(eeAnsJuge);
					    			//设置标准答案
					    			oseq.setStdAns(stdAns);
					    		}
				    			questions3.add(oseq);
				    		}
				    		else if(tebepcDo.getQuestionType() == 3) {     //填空题
				    			
				    		}
				    		else if(tebepcDo.getQuestionType() == 4) {     //问答题
				    			
				    		}
				    	}
				    	//判断题
				    	TEduBdExamPaperSubjectDo tebepsDo1 = db2Service.getTEduBdExamPaperSubjectDoByEpIdQuestionType(osab.getEpId(), 0);
				    	if(tebepsDo1 != null) {
				    		trueFalseTopics.setSubject("一、" + tebepsDo1.getSubjectDescr());
				    	}
				    	trueFalseTopics.setQuestions(questions1);
				    	oeSubAns.setTrueFalseTopics(trueFalseTopics);
				    	//单选题
				    	TEduBdExamPaperSubjectDo tebepsDo2 = db2Service.getTEduBdExamPaperSubjectDoByEpIdQuestionType(osab.getEpId(), 1);
				    	if(tebepsDo2 != null) {
				    		singleChoiceTopic.setSubject("二、" + tebepsDo2.getSubjectDescr());
				    	}
				    	singleChoiceTopic.setQuestions(questions2);
				    	oeSubAns.setSingleChoiceTopic(singleChoiceTopic);
				    	//多选题
				    	TEduBdExamPaperSubjectDo tebepsDo3 = db2Service.getTEduBdExamPaperSubjectDoByEpIdQuestionType(osab.getEpId(), 2);
				    	if(tebepsDo3 != null) {
				    		multiChoiceTopic.setSubject("三、" + tebepsDo3.getSubjectDescr());
				    	}
				    	multiChoiceTopic.setQuestions(questions3);
				    	oeSubAns.setMultiChoiceTopic(multiChoiceTopic);
				    }				    
			    }
			    //获取评卷信息
			    float epScore = 0;
			    //判断题
			    for(int i = 0; i < trueFalseTopics.getQuestions().size(); i++) {
			    	OeSubExamQuestion oseq = trueFalseTopics.getQuestions().get(i);
			    	if(oseq.getEeAnsJuge() == 1) {
			    		if(questionIdToEpcIdxMap.containsKey(oseq.getBodyId())) {
			    			int j = questionIdToEpcIdxMap.get(oseq.getBodyId());
			    			epScore += tebepcDoList.get(j).getScore();
			    		}
			    	}
			    }
			    //单选题
			    for(int i = 0; i < singleChoiceTopic.getQuestions().size(); i++) {
			    	OeSubExamQuestion oseq = singleChoiceTopic.getQuestions().get(i);
			    	if(oseq.getEeAnsJuge() == 1) {
			    		if(questionIdToEpcIdxMap.containsKey(oseq.getBodyId())) {
			    			int j = questionIdToEpcIdxMap.get(oseq.getBodyId());
			    			epScore += tebepcDoList.get(j).getScore();
			    		}
			    	}
			    }
			    //多选题
			    for(int i = 0; i < multiChoiceTopic.getQuestions().size(); i++) {
			    	OeSubExamQuestion oseq = multiChoiceTopic.getQuestions().get(i);
			    	if(oseq.getEeAnsJuge() == 1) {
			    		if(questionIdToEpcIdxMap.containsKey(oseq.getBodyId())) {
			    			int j = questionIdToEpcIdxMap.get(oseq.getBodyId());
			    			epScore += tebepcDoList.get(j).getScore();
			    		}
			    	}
			    }
			    oeSubAns.setScore(epScore);
			    if(epScore >= 60) {
			    	oeSubAns.setExamStatus(1);
			    }
			    else {
			    	oeSubAns.setExamStatus(0);
			    }
			    //设置数据
				osaDto.setOeSubAns(oeSubAns);
				//消息ID
				osaDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}	
			else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			osaDto = SimuDataFunc();
		}		

		return osaDto;
	}
}