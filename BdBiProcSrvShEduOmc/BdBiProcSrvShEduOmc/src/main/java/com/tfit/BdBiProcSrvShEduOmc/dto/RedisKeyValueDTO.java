package com.tfit.BdBiProcSrvShEduOmc.dto;

import java.util.List;

public class RedisKeyValueDTO {
	String time;
	List<RedisKeyValue> redisKeyValue;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<RedisKeyValue> getRedisKeyValue() {
		return redisKeyValue;
	}
	public void setRedisKeyValue(List<RedisKeyValue> redisKeyValue) {
		this.redisKeyValue = redisKeyValue;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
