package com.learn.spring.learnspring.config;

import com.clickhouse.client.config.ClickHouseClientOption;
import com.clickhouse.jdbc.ClickHouseDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "clickhouseEntityManager",
        transactionManagerRef = "clickhouseTransactionManager",
        basePackages = {
                "com.learn.spring.learnspring.entities.clickhouse",
                "com.learn.spring.learnspring.repository.clickhouse"
        }
)
@RequiredArgsConstructor
public class ClickhouseDataSourceConfig {
    @Value("${spring.datasource.clickhouse1.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.clickhouse1.url}")
    private String url;
    @Value("${spring.datasource.clickhouse1.database}")
    private String database;
    @Value("${spring.datasource.clickhouse1.username}")
    private String username;
    @Value("${spring.datasource.clickhouse1.password}")
    private String password;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    /**
     * https://github.com/ClickHouse/clickhouse-jdbc/blob/d6bcbe412d4c7310bba7dc4b0334140b04614004/clickhouse-jdbc/src/test/java/com/clickhouse/jdbc/JdbcIntegrationTest.java#L106
     * @return
     * @throws SQLException
     */
    public DataSource getClickhouseDataSource() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(ClickHouseClientOption.DATABASE.getKey(), database);
        properties.setProperty(ClickHouseClientOption.CLIENT_NAME.getKey(), username);
        properties.setProperty("user", username);
        properties.setProperty("password", password);

        return new ClickHouseDataSource(url, properties);
    }

    @Bean(name = "clickhouseDataSource")
    public DataSource clickhouseDataSource() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(getClickhouseDataSource());
        hikariConfig.setPoolName("clickhouseDataSourcePool");
        hikariConfig.setDriverClassName(driverClassName);

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "clickhouseEntityManager")
    public LocalContainerEntityManagerFactoryBean clickhouseEntityManager(EntityManagerFactoryBuilder builder) throws SQLException {
        Map<String, Object> vendorProperties = getVendorProperties();
        for (String key : vendorProperties.keySet()) {
            log.info("{}: {}", key, vendorProperties.get(key));
        }
        return builder
                .dataSource(clickhouseDataSource())
                .properties(getVendorProperties())
                .packages("com.learn.spring.learnspring.entities.clickhouse")
                .persistenceUnit("clickhouseEntityManager")
                .build();
    }

    private Map<String, Object> getVendorProperties() {
        Map<String,String> properties=jpaProperties.getProperties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return hibernateProperties.determineHibernateProperties(
                properties, new HibernateSettings());
    }

    @Bean(name = "clickhouseTransactionManager")
    public PlatformTransactionManager clickhouseTransactionManager(LocalContainerEntityManagerFactoryBean clickhouseEntityManager) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(clickhouseEntityManager.getObject());

        return txManager;
    }
}
