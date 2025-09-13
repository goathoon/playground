package my.playground.playing.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LoadTest {
    private static final String BASE_URL = "http://localhost:8080";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ExecutorService es = Executors.newFixedThreadPool(100); // 여기 개수 변경하면서 실험해보면 어떻게 될까

    public void fetchApi() {
        for (int i = 1; i <= 100; i++) {
            final int idx = i;
            // 비동기처리
            es.execute(() -> {
                StopWatch stopWatch = new StopWatch();

                stopWatch.start();
                String response = restTemplate.getForObject(BASE_URL + "/block?idx=" + idx, String.class);
                stopWatch.stop();

                log.info("response=" + response + ", stopWatch=" + stopWatch.getTotalTimeSeconds());
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LoadTest loadTest = new LoadTest();
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        loadTest.fetchApi();
        stopWatch.stop();

        es.shutdown();
//        es.awaitTermination(1000, TimeUnit.SECONDS);
        log.info("Total stop watch " + stopWatch.getTotalTimeSeconds());
    }
}
