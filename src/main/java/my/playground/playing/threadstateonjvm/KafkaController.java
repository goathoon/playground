package my.playground.playing.threadstateonjvm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/pub-sync")
    public String pub(@RequestParam String v) throws Exception {
        log.info("kafka send");
        kafkaTemplate.send("demo", v);
        return "ok";
    }
}
