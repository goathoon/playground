package my.playground.playing.threadstateonjvm;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.openfeign.FeignClient(name = "slow", url = "http://localhost:8081")
interface FeignClient {
    @GetMapping("/slow")
    String slow(@RequestParam("ms") long ms);
}
