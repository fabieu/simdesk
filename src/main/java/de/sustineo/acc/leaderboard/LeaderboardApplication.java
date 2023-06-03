package de.sustineo.acc.leaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.ZoneOffset;
import java.util.TimeZone;

@SpringBootApplication
@EnableWebMvc
public class LeaderboardApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));

        SpringApplication.run(LeaderboardApplication.class);
    }
}
