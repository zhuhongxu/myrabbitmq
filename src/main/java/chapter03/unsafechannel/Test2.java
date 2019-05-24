package chapter03.unsafechannel;

import java.util.concurrent.atomic.AtomicLong;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        final AtomicLong count = new AtomicLong();
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 200000; i++){
                    count.addAndGet(1);
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                for (int i = 200000; i < 400000; i++){
                    count.addAndGet(1);
                }
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        System.out.println(count);
    }
}
