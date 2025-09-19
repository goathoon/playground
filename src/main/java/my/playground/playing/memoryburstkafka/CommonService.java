package my.playground.playing.memoryburstkafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("kafka")
public class CommonService {
    private final TaskManager taskManager;

    public void process(ConsumerRecord<String, byte[]> record) {
        String taskId = UUID.randomUUID().toString();

        taskManager.acquire(taskId);

        TaskEvent event = new TaskEvent();
        event.setTaskId(taskId);
        event.setTask(() -> {
            byte[] copy = new byte[1024]; // 메모리 부하 유발
            try {
                Thread.sleep(100); // 더미 지연 (다른 비즈니스 로직)
            } catch (InterruptedException ignored) {}
        });
        event.setCallback(() -> {
            taskManager.release(taskId);
        });

        taskManager.assign(event);
    }
}
