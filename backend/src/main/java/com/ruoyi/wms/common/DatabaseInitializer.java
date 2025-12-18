package com.ruoyi.wms.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库初始化类
 * 用于在应用启动时执行SQL脚本，初始化H2数据库
 */
@Configuration
public class DatabaseInitializer {

    @Autowired
    private DataSource dataSource;

    @Bean
    public ApplicationRunner initDatabase() {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                // 执行初始化脚本
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/init.sql"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}