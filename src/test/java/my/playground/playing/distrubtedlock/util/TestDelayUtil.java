package my.playground.playing.distrubtedlock.util;

public class TestDelayUtil extends DelayUtil {
    private int testDelayMs = 0;

    public void setTestDelayMs(int ms) {
        this.testDelayMs = ms;
    }

    @Override
    public void randomDelay() {
        // 테스트에서 기본 호출되는 메서드는 이걸 타게 함
        randomDelay(testDelayMs);
    }

    @Override
    public void randomDelay(int ms) {
        final long delay = ms;
        // 실제 지연을 일으키지 않거나, testDelayMs에 따라 제어
        try {
            Thread.sleep(delay); // 또는 0으로
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

