import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LimitedQueue<T> {

    private final int consumersSize;
    private final LoadLimiter loadLimiter;
    private final BlockingQueue<T> queue;

    public LimitedQueue(int consumersSize, int estimationWindowSize) {
        this.consumersSize = consumersSize;
        this.loadLimiter = new LoadLimiter(estimationWindowSize);
        this.queue = new LinkedBlockingQueue<>();
    }

    public Status produce(T item, int maxTimeout) {
        if (loadLimiter.estimateWaiting(queue.size(), consumersSize) > maxTimeout) {
            return Status.REJECTED;
        }
        queue.add(item);
        return Status.ACCEPTED;
    }

    public T consume() throws InterruptedException {
        return queue.take();
    }

    public void updateEstimation(double processTime) {
        loadLimiter.updateEstimation(processTime);
    }
}
