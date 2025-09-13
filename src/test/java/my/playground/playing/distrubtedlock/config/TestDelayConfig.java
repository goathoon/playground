package my.playground.playing.distrubtedlock.config;

import my.playground.playing.distrubtedlock.util.DelayUtil;
import my.playground.playing.distrubtedlock.util.TestDelayUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestDelayConfig {
    @Bean
    public DelayUtil delayUtil() {
        return new TestDelayUtil();
    }
}
