package my.playground.playing.memoryburstkafka;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class TaskEvent implements Runnable {
    private String taskId;
    private Runnable task;
    private Runnable callback;

    @Override
    public void run() {
        callback.run();
    }
}