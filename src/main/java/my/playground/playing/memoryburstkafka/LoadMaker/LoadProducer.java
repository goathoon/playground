package my.playground.playing.memoryburstkafka.LoadMaker;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class LoadProducer {
    public static void main(String[] args) throws Exception {
        String topic = "test-topic";

        int targetCmps = 10000;   // 초당 메시지 수 (조절 가능)
        int payloadKb = 512;     // 메시지 크기 (KB)
        int durationSec = 100;   // 테스트 시간 (초)

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.ByteArraySerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "5");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        try (KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props)) {
            byte[] payload = new byte[payloadKb * 512];
            ThreadLocalRandom.current().nextBytes(payload);

            long intervalNanos = 1_000_000_000L / targetCmps;
            long end = System.nanoTime() + durationSec * 1_000_000_000L;
            long next = System.nanoTime();

            while (System.nanoTime() < end) {
                String key = "id-" + ThreadLocalRandom.current().nextInt(100_000);

                ProducerRecord<String, byte[]> record =
                        new ProducerRecord<>(topic, key, payload);
                record.headers().add("tid", key.getBytes(StandardCharsets.UTF_8));

                producer.send(record);
                log.info("pub key = [{}]", key);

                next += intervalNanos;
                long sleep = next - System.nanoTime();
                if (sleep > 0) LockSupport.parkNanos(sleep);
            }
        }
    }
}