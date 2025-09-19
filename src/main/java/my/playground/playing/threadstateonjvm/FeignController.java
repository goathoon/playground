package my.playground.playing.threadstateonjvm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FeignController {
    private final FeignClient client;
    @GetMapping("/test")
    public String test(@RequestParam(defaultValue="10000") long ms) {
        log.info("start");
        return client.slow(ms);
    }
}
