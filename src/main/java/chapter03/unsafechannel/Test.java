package chapter03.unsafechannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        final List<Integer> list = new ArrayList<Integer>();
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 200000; i++){
                    list.add(i);
                }
            }
        });
//
//        Thread thread2 = new Thread(new Runnable() {
//            public void run() {
//                for (int i = 200000; i < 400000; i++){
//                    list.add(i);
//                }
//            }
//        });

        thread1.start();
//        thread2.start();
        thread1.join();
//        thread2.join();

        System.out.println(list.size());

    }
}
