public class Example {

    public static void main(String[] args) throws InterruptedException {
        int producersCount = 7;
        int consumersCount = 2;
        int estimationWindowSize = 10;

        int taskPause = 1000;

        Thread[] producers = new Thread[producersCount];
        Thread[] consumers = new Thread[consumersCount];

        LimitedQueue<Runnable> queue = new LimitedQueue<>(consumersCount, estimationWindowSize);

        for (int i = 0; i < producersCount; i++) {
            producers[i] = new Thread(() -> {
                while (true) {
                    Status status = queue.produce(() -> sleep(taskPause), 3000);
                    System.out.println(status);
                    sleep(1000);
                }
            });
        }

        for (int i = 0; i < consumersCount; i++) {
            consumers[i] = new Thread(() -> {
                try {
                    while (true) {
                        Runnable runnable = queue.consume();
                        long startTime = System.currentTimeMillis();
                        runnable.run();
                        queue.updateEstimation(System.currentTimeMillis() - startTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        for (int i = 0; i < producersCount; i++) {
            producers[i].start();
        }
        for (int i = 0; i < consumersCount; i++) {
            consumers[i].start();
        }

        producers[0].join();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
