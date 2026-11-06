package kr.co.ratiko.weiter.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TimeZone;
import org.junit.jupiter.api.Test;

class TimeZoneConfigTest {

    @Test
    void loadingTheConfigurationMakesSeoulTheDefaultTimeZone() {
        new TimeZoneConfig();

        assertThat(TimeZone.getDefault().getID()).isEqualTo("Asia/Seoul");
    }
}
