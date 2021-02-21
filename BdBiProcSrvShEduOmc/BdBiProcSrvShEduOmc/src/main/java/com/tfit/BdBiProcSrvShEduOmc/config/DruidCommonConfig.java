package com.tfit.BdBiProcSrvShEduOmc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Descritpion：Druid属性通用配置
 * @author: tianfang_infotech
 * @date: 2019/1/15 15:04
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.common")
public class DruidCommonConfig {

    /**
     * 一个连接在池中最小生存的时间(ms)
     */
    private Long minEvictableIdleTimeMillis;

    /**
     * 一个连接在池中最大生存的时间(ms)
     */
    private Long maxEvictableIdleTimeMillis;

	public Long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public Long getMaxEvictableIdleTimeMillis() {
		return maxEvictableIdleTimeMillis;
	}

	public void setMaxEvictableIdleTimeMillis(Long maxEvictableIdleTimeMillis) {
		this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
	}
}
