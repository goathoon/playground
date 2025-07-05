package my.playground.playing.distrubtedlock;

import my.playground.playing.distrubtedlock.config.TestDelayConfig;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import my.playground.playing.distrubtedlock.util.TestDelayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Import(TestDelayConfig.class)
public class UserServiceConcurrencyTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ProtectedResourceRepository resourceRepository;

    @Autowired
    private TestDelayUtil delayUtil;

    @BeforeEach
    void setUp() {
        resourceRepository.save(new ProtectedResource("shared-id", 0));
    }

    @Test
    void isIdSet() {
        Optional<ProtectedResource> found = resourceRepository.findById("shared-id");

        assertThat(found)
                .isPresent()
                .get()
                .satisfies(resource -> {
                    assertThat(resource.getId()).isEqualTo("shared-id");
                    assertThat(resource.getCount()).isEqualTo(0);
                });
    }

    @Nested
    @DisplayName("0_5 초의 delay Time")
    class DelayTime_0_5 {

        @BeforeEach
        void setDelayTime() {
            delayUtil.setTestDelayMs(500);
        }

        @Test
        @DisplayName("0.02초 간격으로 삭제/삭제/업로드 요청시, 첫번째 요청의 비즈니스 로직만 수행된다")
        void 순서대로_삭제_삭제_업로드_요청시_최초_요청만_수행된다() throws InterruptedException {
            int concurrency = 3;
            ExecutorService executor = Executors.newFixedThreadPool(concurrency);
            List<String> results = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < concurrency; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        String result = null;
                        if (index == concurrency - 1) {
                            result = userService.upload("shared-id");
                        } else {
                            result = userService.delete("shared-id");
                        }
                        results.add(result);
                    } catch (DuplicateKeyException e) {
                        results.add("Duplication Exception");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                Thread.sleep(20);
            }

            // 스레드풀 전체 종료까지 main스레드 대기
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);

            long successCount = results.stream().filter("Success"::equals).count();

            Optional<ProtectedResource> resource = resourceRepository.findById("shared-id");

            assertAll(
                () -> assertThat(successCount)
                        .as("하나의 스레드에서만 비즈니스 로직이 성공해야 한다")
                        .isEqualTo(1),
                () -> assertThat(resource)
                        .as("shared-id 리소스는 존재하지 않아야 한다")
                        .isNotPresent()

            );

        }

        @Test
        @DisplayName("동시에 삭제/삭제/업로드 요청시, 최초 인입 요청의 비즈니스 로직만 수행된다")
        void 동시에_삭제_삭제_업로드_요청시_최초_요청만_수행된다() throws InterruptedException {
            int concurrency = 1000;
            ExecutorService executor = Executors.newFixedThreadPool(concurrency);
            CountDownLatch allThreadsLatch = new CountDownLatch(1);
            CountDownLatch eachThreadLatch = new CountDownLatch(concurrency);
            List<String> results = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < concurrency; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        allThreadsLatch.await();
                        String result = null;
                        if (index == concurrency - 1) {
                            result = userService.upload("shared-id");
                        } else {
                            result = userService.delete("shared-id");
                        }
                        results.add(result);
                    } catch (DuplicateKeyException e) {
                        results.add("Duplication Exception");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        eachThreadLatch.countDown();
                    }
                });
            }

            allThreadsLatch.countDown();
            eachThreadLatch.await();

            // 스레드풀 전체 종료까지 main스레드 대기
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);

            long successCount = results.stream().filter("Success"::equals).count();

            assertThat(successCount)
                    .as("하나의 스레드에서만 비즈니스 로직이 성공해야 한다")
                    .isEqualTo(1);


        }
    }
}