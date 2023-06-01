package de.sustineo.acc.leaderboard.configuration;

import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Profile("local")
@Configuration
@MapperScan(basePackages = {"de.sustineo.acc.leaderboard.entities.mapper"})
public class EmbeddedPersistenceConfiguration {
    private static DataSource dataSource;
    private SqlSessionFactory sqlSessionFactory;

    @Bean("embeddedDatabaseIdProvider")
    public VendorDatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put("H2", "h2");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    @Bean("embeddedDataSource")
    public DataSource dataSource() {
        if (Objects.isNull(dataSource)) {
            dataSource = new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .generateUniqueName(true)
                    .build();
        }
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("embeddedDataSource") DataSource dataSource,
                                               @Qualifier("embeddedDatabaseIdProvider") VendorDatabaseIdProvider databaseIdProvider) throws Exception {
        if (Objects.isNull(sqlSessionFactory)) {
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setDatabaseIdProvider(databaseIdProvider);
            sqlSessionFactory = factoryBean.getObject();
        }
        return sqlSessionFactory;
    }
}
