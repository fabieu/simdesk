package de.sustineo.acc.leaderboard.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"de.sustineo.acc.leaderboard.entities.mapper"})
public class EmbeddedPersistenceConfiguration {
    private static DataSource dataSource;
    private SqlSessionFactory sqlSessionFactory;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        if (sqlSessionFactory == null) {
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(dataSource);
            sqlSessionFactory = factoryBean.getObject();
        }
        return sqlSessionFactory;
    }
}
