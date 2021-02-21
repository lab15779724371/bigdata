package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbHealthEduDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.RbHealthEduDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//发布通报健康宣教详情应用 模型
public class RbHealthEduDetAppMod {
	private static final Logger logger = LogManager.getLogger(RbHealthEduDetAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化
	String bulletinId = "256510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String title = "学校营养午餐怎样健康又美味";
	String pubOrg = "上海市教委";
	String pubDate = "2018-09-05";
	int readNum = 58;
	String content = "营养餐应执行怎样的标准？“营养餐的标准首先是安全。除了一些显性的不安全因素外，农药残留、瘦肉精、化学调味品等隐性的因素也要考虑。其次是口味，大多数学生觉得好吃，大部分人认可就可以。比如一些孩子可能只爱吃肉，但是考虑营养搭配我们不能这么做。再次就是营养。搭配要合理。2017年1月，我国出台了《中国居民膳食指南》和《中国学龄儿童膳食指南》。学生的膳食搭配可以这么形容：一拳头肉，两拳头碳水化合物，三拳头蔬菜，四拳头水果。还有更形象的膳食宝塔图等，营养午餐都要涉及。最后是多种食材。我们要求，中小学生一天要摄入12种食材，一周至少要摄入25种食材，在西方发达国家，营养午餐要求一周摄入30多种食材。食材种类越多，营养越均衡。”中国营养餐产业技术创新战略联盟秘书长孟庆芬告诉记者。尽管营养餐有完整的标准依据，但是乔锦忠认为，“目前的营养餐并没有达到应有的水平”。张萌萌告诉记者，每周的营养餐都会有一天是“西餐”，比如“意大利面”和“炸鸡”等，她只爱吃那天的餐食，因为“只有那天是有正常味道的”。长期研究学生营养餐食，乔锦忠认为，最主要的欠缺来自人员配备。“我们缺少专业的营养师为学生做好搭配，一些配餐公司尽管有所谓营养师，实际上因为成本原因，有的地方营养师只是一个称谓，在配餐及营养核算上并没有经验。就学校来说，也欠缺营养师针对学生成长发育做好搭配。这是目前亟待解决的问题，如果单个学校无法做到，一个区域有一个营养师也可以，希望这个目标尽快得到落实。”中国教育科学研究院研究员储朝晖认为，营养餐的问题背后反映的是社会管理的精细化程度。“学校供应餐食，对学生和家长来说，是减轻负担的好事，同时也对学生的身体健康、综合素质的提升有一定的帮助。目前暴露出来的问题都是管理的细化问题，以及细化后工作量增大的问题。比如农村营养餐暴露出来的就餐人数不一的问题，以及城市营养餐的口味不佳、滋生浪费等问题。”他认为。孟庆芬表示，校园餐管理亟待立法。“目前各级政府有关部门没有专职人员负责学生营养餐管理工作，学校没有专业教师开展营养健康教育，学校工勤人员薪酬没有来源保障，厨师培训缺乏硬性要求，社会餐饮企业进入学校食堂门槛不规范等等，这些都是中小学校园餐管理中的突出问题。";
	String lastBulletinId = "366510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String lastTitle = "7-9岁学生营养摄入建议参考";
	String nextBulletinId = "496510ab-a428-46b4-b3a7-6388191e1ffc20180321";
	String nextTitle = "国外中小学生校餐大观";
	
	//模拟数据函数
	private RbHealthEduDetDTO SimuDataFunc() {
		RbHealthEduDetDTO rhedDto = new RbHealthEduDetDTO();
		//时戳
		rhedDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//发布通报健康宣教详情模拟数据
		RbHealthEduDet rbBulletinDet = new RbHealthEduDet();
		//赋值
		rbBulletinDet.setBulletinId(bulletinId);
		rbBulletinDet.setTitle(title);
		rbBulletinDet.setPubOrg(pubOrg);
		rbBulletinDet.setPubDate(pubDate);
		rbBulletinDet.setReadNum(readNum);
		rbBulletinDet.setContent(content);
		rbBulletinDet.setLastBulletinId(lastBulletinId);
		rbBulletinDet.setLastTitle(lastTitle);
		rbBulletinDet.setNextBulletinId(nextBulletinId);
		rbBulletinDet.setNextTitle(nextTitle);
		//设置数据
		rhedDto.setRbHealthEduDet(rbBulletinDet);
		//消息ID
		rhedDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rhedDto;
	}
	
	// 发布通报健康宣教详情模型函数
	public RbHealthEduDetDTO appModFunc(String bulletinId, String distName, String prefCity, String province, Db1Service db1Service) {
		RbHealthEduDetDTO rhedDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			rhedDto = SimuDataFunc();
		}		

		return rhedDto;
	}
}
