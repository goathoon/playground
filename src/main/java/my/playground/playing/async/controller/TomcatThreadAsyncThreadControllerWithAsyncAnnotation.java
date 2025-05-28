package my.playground.playing.async.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * tomcat 쓰레드 수를 한개로 하고, 비동기로 백그라운드로 처리했을 때, 어떻게 되는지 확인해보자
 */
@Slf4j
@RestController
public class TomcatThreadAsyncThreadControllerWithFuture {

    private final ExecutorService es = Executors.newFixedThreadPool(10);

    @GetMapping("/block")
    public String block(int idx) throws InterruptedException, ExecutionException {
        log.info("TOMCAT REQUEST THREAD : {}", Thread.currentThread().getName());
        Future<String> future = es.submit(() -> slowThread(idx));
        return future.get();
    }

    private String slowThread(int i) {
        try {
            log.info("BACKGROUND THREAD : {}", Thread.currentThread().getName());
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "INTERRUPTED";
        }
        return "SLOW THREAD" + i;
    }
}
