package my.playground.playing.virtualthread;

import java.util.List;
import java.util.stream.IntStream;

public class ThreadPinningSynchronizedTestDifferent {


    private static final Object lock = new Object();

    public static void main(String[] args) {

        giveSomeTimeForJFRInit();

        List<Thread> threads = IntStream.range(0, 10)
                .mapToObj(index -> Thread.ofVirtual().unstarted(() -> {
                    String threadInfo = "BEFORE " + index + " - " + Thread.currentThread() + "\n";

                    synchronized (lock) {
                        // 시간은 오래 걸리지만 blocking은 없는 사례
                        // 시간은 오래 걸리지만 blocking은 없는 사례
                         getFibonacci(40);
//                        try {
//                            Thread.sleep(50);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
                    }

                    threadInfo += "AFTER  " + index + " - " + Thread.currentThread() + "\n";
                    System.out.println(threadInfo);
                }))
                .toList();

        threads.forEach(Thread::start);

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // n번째 피보나치 수
    private static long getFibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return getFibonacci(n - 1) + getFibonacci(n - 2);
    }

    private static void giveSomeTimeForJFRInit() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

/**
 * BEFORE 8 - VirtualThread[#29]/runnable@ForkJoinPool-1-worker-9
 * AFTER  8 - VirtualThread[#29]/runnable@ForkJoinPool-1-worker-9
 *
 * BEFORE 3 - VirtualThread[#24]/runnable@ForkJoinPool-1-worker-4
 * AFTER  3 - VirtualThread[#24]/runnable@ForkJoinPool-1-worker-4
 *
 * BEFORE 7 - VirtualThread[#28]/runnable@ForkJoinPool-1-worker-8
 * AFTER  7 - VirtualThread[#28]/runnable@ForkJoinPool-1-worker-8
 *
 * BEFORE 4 - VirtualThread[#25]/runnable@ForkJoinPool-1-worker-5
 * AFTER  4 - VirtualThread[#25]/runnable@ForkJoinPool-1-worker-5
 *
 * BEFORE 0 - VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
 * AFTER  0 - VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
 *
 * BEFORE 1 - VirtualThread[#22]/runnable@ForkJoinPool-1-worker-2
 * AFTER  1 - VirtualThread[#22]/runnable@ForkJoinPool-1-worker-2
 *
 * BEFORE 9 - VirtualThread[#30]/runnable@ForkJoinPool-1-worker-10
 * AFTER  9 - VirtualThread[#30]/runnable@ForkJoinPool-1-worker-10
 *
 * BEFORE 5 - VirtualThread[#26]/runnable@ForkJoinPool-1-worker-6
 * AFTER  5 - VirtualThread[#26]/runnable@ForkJoinPool-1-worker-6
 *
 * BEFORE 2 - VirtualThread[#23]/runnable@ForkJoinPool-1-worker-3
 * AFTER  2 - VirtualThread[#23]/runnable@ForkJoinPool-1-worker-3
 *
 * BEFORE 6 - VirtualThread[#27]/runnable@ForkJoinPool-1-worker-7
 * AFTER  6 - VirtualThread[#27]/runnable@ForkJoinPool-1-worker-7
 */