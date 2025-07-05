package my.playground.playing.distrubtedlock.util;

import org.springframework.stereotype.Component;

@Component
public class DelayUtil {

    public void randomDelay(int ms) {
        final long delay = ms + (long) (Math.random() * 51); // 50ms ~ 200ms 랜덤 지연
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 기본값 오버로드 (실서비스용)
    public void randomDelay() {
        randomDelay(50); // 50~100ms
    }
}
