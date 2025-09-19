package my.playground.playing.memoryburstkafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("kafka")
public class ConsumerListener {

    private final CommonService commonService;

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, byte[]> record) {
        commonService.process(record);
    }
}
