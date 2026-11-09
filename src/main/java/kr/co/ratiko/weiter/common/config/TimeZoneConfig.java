package kr.co.ratiko.weiter.common.config;

import java.util.TimeZone;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

    private static final String KOREA_TIMEZONE_ID = "Asia/Seoul";

    static {
        TimeZone.setDefault(
                TimeZone.getTimeZone(KOREA_TIMEZONE_ID)
        );
    }
}
