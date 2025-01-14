package de.sustineo.simdesk.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfiguration {
    private static final String DEFAULT_SCHEMA_NAME = "simdesk";

    @Bean
    @ConfigurationProperties("simdesk.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "simdesk.datasource.vendor", havingValue = DatabaseVendor.SQLITE)
    public HikariDataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:data/simdesk.db");
        hikariConfig.setConnectionInitSql("PRAGMA journal_mode = wal; PRAGMA synchronous = normal; PRAGMA temp_store = memory; PRAGMA foreign_keys = ON;");
        hikariConfig.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @ConditionalOnProperty(name = "simdesk.datasource.vendor", havingValue = DatabaseVendor.POSTGRESQL)
    public HikariDataSource dataSourcePostgreSQL(DataSourceProperties dataSourceProperties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dataSourceProperties.determineDriverClassName());
        hikariConfig.setJdbcUrl(dataSourceProperties.determineUrl());
        hikariConfig.setUsername(dataSourceProperties.determineUsername());
        hikariConfig.setPassword(dataSourceProperties.determinePassword());
        hikariConfig.setSchema(DEFAULT_SCHEMA_NAME);

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        Properties vendorProperties = new Properties();
        vendorProperties.setProperty("PostgreSQL", DatabaseVendor.POSTGRESQL);
        vendorProperties.setProperty("SQLite", DatabaseVendor.SQLITE);

        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        databaseIdProvider.setProperties(vendorProperties);

        return databaseIdProvider;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext appContext, VendorDatabaseIdProvider vendorDatabaseIdProvider) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setDatabaseIdProvider(vendorDatabaseIdProvider);

        return bean.getObject();
    }

    /**
     * Customize Flyway migration strategy for different database vendors.
     * Currently, only PostgreSQL and SQLite are supported.
     *
     * @param dataSourceVendor ID of type{@link DatabaseVendor}
     * @return FlywayMigrationStrategy
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(@Value("${simdesk.datasource.vendor}") String dataSourceVendor) {
        return (currentFlyway) -> {
            FluentConfiguration flywayConfiguration = Flyway.configure()
                    .configuration(currentFlyway.getConfiguration());

            if (DatabaseVendor.POSTGRESQL.equals(dataSourceVendor)) {
                flywayConfiguration.locations("classpath:db/migration/" + DatabaseVendor.POSTGRESQL);
                flywayConfiguration.defaultSchema(DEFAULT_SCHEMA_NAME);
                flywayConfiguration.createSchemas(true);
            } else if (DatabaseVendor.SQLITE.equals(dataSourceVendor)) {
                flywayConfiguration.locations("classpath:db/migration/" + DatabaseVendor.SQLITE);
            }

            Flyway flyway = flywayConfiguration.load();
            flyway.migrate();
        };
    }
}
