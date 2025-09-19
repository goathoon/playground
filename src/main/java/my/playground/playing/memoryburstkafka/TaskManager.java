package my.playground.playing.memoryburstkafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Slf4j
@Component
@Profile("kafka")
public class TaskManager {
    private final Semaphore semaphore = new Semaphore(100); // 운영 기준 맞춤

    public void acquire(String taskId) {
        semaphore.acquireUninterruptibly();
        log.info("[{}] taskId acquired semaphore : ", taskId);
        log.info("available permits = {}", semaphore.availablePermits());
    }

    public void release(String taskId) {
        semaphore.release();
        log.info("[{}] released semaphore : ", taskId);
    }

    public void assign(TaskEvent taskEvent) {
        Thread.ofVirtual().name(taskEvent.getTaskId())
                .start(() -> {
                    try {
                        Thread.sleep(50); // 다른 통신
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    taskEvent.getCallback().run();
                });

    }

}
