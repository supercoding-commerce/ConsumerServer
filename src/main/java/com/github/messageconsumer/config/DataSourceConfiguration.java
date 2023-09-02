package com.github.messageconsumer.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfiguration {

    public static final String MASTER_DATASOURCE = "masterDataSource";
    public static final String SLAVE_DATASOURCE = "slaveDataSource";
    public static final String SLAVE_DATASOURCE2 = "slaveDataSource2";

    @Bean(MASTER_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.master.hikari") // (1)
    public DataSource masterDataSource() {
        log.info("------------masterDB_connected------------");
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(SLAVE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
    public DataSource slaveDataSource() {
        log.info("------------slaveDB_connected------------");
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(SLAVE_DATASOURCE2)
    @ConfigurationProperties(prefix = "spring.datasource.slave2.hikari")
    public DataSource slaveDataSource2() {
        log.info("------------slaveDB_connected------------");
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @DependsOn({MASTER_DATASOURCE, SLAVE_DATASOURCE, SLAVE_DATASOURCE2})
    public DataSource routingDataSource(
            @Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
            @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource,
            @Qualifier(SLAVE_DATASOURCE2) DataSource slaveDataSource2
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> datasource = new HashMap<>();

        datasource.put("master", masterDataSource);
        datasource.put("slave", slaveDataSource);
        datasource.put("slave2", slaveDataSource2);
        routingDataSource.setTargetDataSources(datasource);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Primary
    @Bean
    @DependsOn("routingDataSource")
    public LazyConnectionDataSourceProxy dataSource(DataSource routingDataSource){
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

}