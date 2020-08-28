/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-08-27
 */
//public class TestBatch {
//    public static BlockingQueue<String> QUEUE = new LinkedBlockingQueue<>(2000000);
//    private static Logger logger = LoggerFactory.getLogger("tttt");
//
//    public static void main(String[] args) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    QUEUE.offer("1");
//                }
//            }
//        }).start();
//
//        while (true) {
//            List<String> list = new ArrayList<>();
//            try {
//                Queues.drain(QUEUE, list, 1000, 1, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            logger.info(list.toString());
//        }
//
//    }
//}
