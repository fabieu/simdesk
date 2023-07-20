package de.sustineo.acc.leaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@EnableWebMvc
@EnableTransactionManagement
public class LeaderboardApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        Locale.setDefault(Locale.ENGLISH);

        SpringApplication.run(LeaderboardApplication.class);
    }
}
