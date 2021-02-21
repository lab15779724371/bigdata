package com.tfit.BdBiProcSrvShEduOmc.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdDishListAppMod;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Descritpion：Saas库配置
 * @author: tianfang_infotech
 * @date: 2019/1/9 15:16
 */
@Configuration
@MapperScan(basePackages = DruidSaasConfig.PACKAGE_SCAN, sqlSessionFactoryRef = "sqlSessionFactorySaaS")
public class DruidSaasConfig {

    public static final String PACKAGE_SCAN = "com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas";
    public static final String MAPPER_LOCATION = "classpath:mapper/saas/*.xml";
    private static final Logger logger = LogManager.getLogger(BdDishListAppMod.class.getName());
    @Autowired
    private DruidCommonConfig druidCommonConfig;

    @Primary
    @Bean("dataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSourceSaaS() {

        DruidDataSource dataSource = new DruidDataSource();

        if (dataSource.getMinEvictableIdleTimeMillis() > dataSource.getMaxEvictableIdleTimeMillis()) {
            /**
             * setMinEvictableIdleTimeMillis 需先设置
             */
            dataSource.setMinEvictableIdleTimeMillis(druidCommonConfig.getMinEvictableIdleTimeMillis());
            dataSource.setMaxEvictableIdleTimeMillis(druidCommonConfig.getMaxEvictableIdleTimeMillis());
        }
        
        logger.info("DruidSaasConfig dataSourceSaaS"+dataSource.getUrl());

        return dataSource;
    }

    /**
     * 数据源事务管理器
     *
     * @return
     */
    @Primary
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManagerSaaS() {
        return new DataSourceTransactionManager(dataSourceSaaS());
    }

    /**
     * 创建Session
     *
     * @return
     * @throws Exception
     */
    @Primary
    @Bean
    public SqlSessionFactory sqlSessionFactorySaaS() throws Exception {

        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSourceSaaS());

        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));

        logger.info("DruidSaasConfig:sqlSessionFactorySaaS"+factoryBean);
        return factoryBean.getObject();
    }
}
