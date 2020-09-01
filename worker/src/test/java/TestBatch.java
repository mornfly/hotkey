import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-08-27
 */
public class TestBatch {
    public static BlockingQueue<String> QUEUE = new LinkedBlockingQueue<>(2000000);
    private static Logger logger = LoggerFactory.getLogger("tttt");
    private static int i;

    public static void main(String[] args) throws InterruptedException {

//        for (int j = 0; j < 10; j++) {
//            QUEUE.offer("" + j);
//        }
//        List<String> list = new ArrayList<>();
//        Queues.drain(QUEUE, list, 2, 1, TimeUnit.SECONDS);
//        System.out.println(QUEUE);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    QUEUE.offer("" + i++);
//                }
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(QUEUE);
//                }
//            }
//        }).start();
//
//        while (true) {
//            List<String> list = new ArrayList<>();
//            try {
//                System.out.println("size " + QUEUE.size());
//                Queues.drain(QUEUE, list, 2, 1, TimeUnit.SECONDS);
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            logger.info(list.toString());
//        }

    }
}
