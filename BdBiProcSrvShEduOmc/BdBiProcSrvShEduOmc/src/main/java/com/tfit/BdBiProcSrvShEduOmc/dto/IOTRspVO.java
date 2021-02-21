package com.tfit.BdBiProcSrvShEduOmc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTException;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;

public class IOTRspVO {
	@JsonProperty("msgId")
	private Long msgId;
	
	@JsonProperty("result")
	private IOTRspVOParam result;
	
	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public IOTRspVOParam getResult() {
		if (this.result == null){
			result = new IOTRspVOParam();
		}
		return result;
	}

	public void setResult(IOTRspVOParam result) {
		this.result = result;
	}

	public void setResultByType(IOTRspType rspType) {
		this.getResult().setCode(rspType.getCode());
		this.getResult().setMsg(rspType.getMsg());
	}

	public static IOTRspVO createNew(long msgID, IOTException e){
		IOTRspVO iotRspVO = new IOTRspVO();
		iotRspVO.setMsgId(msgID);

		IOTRspVOParam param = new IOTRspVOParam(e.getMessage(),e.getErrorCode());
		iotRspVO.setResult(param);

		return iotRspVO;
	}

	public static IOTRspVO createNew(long msgID, IOTRspType rspType){
		IOTRspVO iotRspVO = new IOTRspVO();
		iotRspVO.setMsgId(msgID);

		IOTRspVOParam param = new IOTRspVOParam(rspType.getMsg(),rspType.getCode());
		iotRspVO.setResult(param);

		return iotRspVO;
	}
}
