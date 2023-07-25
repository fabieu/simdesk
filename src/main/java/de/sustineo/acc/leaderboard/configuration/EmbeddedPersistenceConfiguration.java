package de.sustineo.acc.leaderboard.configuration;

import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Properties;

@Profile(ProfileManager.PROFILE_H2)
@Configuration
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
        if (dataSource == null) {
            dataSource = new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .generateUniqueName(true)
                    .addScript("db/local-h2/V0_0_1__config.sql")
                    .build();
        }

        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("embeddedDataSource") DataSource dataSource,
                                               @Qualifier("embeddedDatabaseIdProvider") VendorDatabaseIdProvider databaseIdProvider) throws Exception {
        if (sqlSessionFactory == null) {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setDatabaseIdProvider(databaseIdProvider);
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
        }

        return sqlSessionFactory;
    }
}
