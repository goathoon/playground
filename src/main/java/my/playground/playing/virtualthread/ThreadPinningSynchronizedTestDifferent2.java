package my.playground.playing.virtualthread;

import java.util.List;
import java.util.stream.IntStream;

public class ThreadPinningSynchronizedTestDifferent2 {

    private static final Object lock = new Object();

    public static void main(String[] args) {
        System.out.println("availableProcessors = " + Runtime.getRuntime().availableProcessors());

        giveSomeTimeForJFRInit();

        List<Thread> threads = IntStream.range(0, 10)
                .mapToObj(index -> Thread.ofVirtual().unstarted(() -> {
                    System.out.println("BEFORE " + index + " - " + Thread.currentThread());

                    synchronized (lock) {
                        // 시간은 오래 걸리지만 blocking은 없는 사례
                        getFibonacci(40);
                    }

                    System.out.println("AFTER  " + index + " - " + Thread.currentThread());
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
 * BEFORE 1 - VirtualThread[#27]/runnable@ForkJoinPool-1-worker-2
 * BEFORE 4 - VirtualThread[#30]/runnable@ForkJoinPool-1-worker-5
 * BEFORE 5 - VirtualThread[#31]/runnable@ForkJoinPool-1-worker-6
 * BEFORE 8 - VirtualThread[#34]/runnable@ForkJoinPool-1-worker-8
 * BEFORE 7 - VirtualThread[#33]/runnable@ForkJoinPool-1-worker-7
 * BEFORE 3 - VirtualThread[#29]/runnable@ForkJoinPool-1-worker-4
 * BEFORE 0 - VirtualThread[#26]/runnable@ForkJoinPool-1-worker-1
 * BEFORE 6 - VirtualThread[#32]/runnable@ForkJoinPool-1-worker-9
 * BEFORE 9 - VirtualThread[#35]/runnable@ForkJoinPool-1-worker-10
 * BEFORE 2 - VirtualThread[#28]/runnable@ForkJoinPool-1-worker-3
 * AFTER  1 - VirtualThread[#27]/runnable@ForkJoinPool-1-worker-2
 * AFTER  4 - VirtualThread[#30]/runnable@ForkJoinPool-1-worker-5
 * AFTER  5 - VirtualThread[#31]/runnable@ForkJoinPool-1-worker-2
 * AFTER  8 - VirtualThread[#34]/runnable@ForkJoinPool-1-worker-5
 * AFTER  7 - VirtualThread[#33]/runnable@ForkJoinPool-1-worker-2
 * AFTER  3 - VirtualThread[#29]/runnable@ForkJoinPool-1-worker-5
 * AFTER  0 - VirtualThread[#26]/runnable@ForkJoinPool-1-worker-2
 * AFTER  6 - VirtualThread[#32]/runnable@ForkJoinPool-1-worker-5
 * AFTER  9 - VirtualThread[#35]/runnable@ForkJoinPool-1-worker-2
 * AFTER  2 - VirtualThread[#28]/runnable@ForkJoinPool-1-worker-5
 */