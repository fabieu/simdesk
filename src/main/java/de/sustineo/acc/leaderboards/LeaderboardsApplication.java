package de.sustineo.acc.leaderboards;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class LeaderboardsApplication {
	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LeaderboardsApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}
