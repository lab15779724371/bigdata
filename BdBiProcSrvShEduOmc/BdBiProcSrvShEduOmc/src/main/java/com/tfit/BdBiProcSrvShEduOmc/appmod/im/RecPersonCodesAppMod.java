package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RecPersonCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//回收人编码列表应用模型
public class RecPersonCodesAppMod {
	private static final Logger logger = LogManager.getLogger(RecPersonCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"张山", "李四"};
	String[] code_Array = {"4f6ee487-8913-40c8-bcce-7a538be4ec40", "4f7fe487-8913-40c8-bcce-7a538be4ec61"};
		
	//模拟数据函数
	private RecPersonCodesDTO SimuDataFunc() {
		RecPersonCodesDTO rpcDto = new RecPersonCodesDTO();
		//时戳
		rpcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//回收人编码列表模拟数据
		List<NameCode> recPersonCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode rpc = new NameCode();
			rpc.setName(name_Array[i]);
			rpc.setCode(code_Array[i]);
			recPersonCodes.add(rpc);
		}
		// 分页
		PageBean<NameCode> pageBean = new PageBean<NameCode>(recPersonCodes, curPageNum, this.pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		rpcDto.setPageInfo(pageInfo);
		//设置数据
		rpcDto.setRecPersonCodes(recPersonCodes);
		//消息ID
		rpcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rpcDto;
	}
	
	// 回收人编码列表模型函数
	public RecPersonCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, SaasService saasService) {
		RecPersonCodesDTO rpcDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			List<String> recPersonList = saasService.getAllRecPersonName();
			if(recPersonList != null) {
				rpcDto = new RecPersonCodesDTO();
				//时戳
				rpcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//回收人列表模拟数据
				List<NameCode> rmcList = new ArrayList<>();
				//赋值
				for (int i = 0; i < recPersonList.size(); i++) {
					NameCode rpc = new NameCode();
					rpc.setName(recPersonList.get(i));
					rpc.setCode(recPersonList.get(i));			
					rmcList.add(rpc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(rmcList, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				rpcDto.setPageInfo(pageInfo);
				//设置数据
				rpcDto.setRecPersonCodes(pageBean.getCurPageData());
				//消息ID
				rpcDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取回收人数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			rpcDto = SimuDataFunc();
		}		

		return rpcDto;
	}
}
