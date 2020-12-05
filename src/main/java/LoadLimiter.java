import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadLimiter {

    private final int estimationWindowSize;
    private final Queue<Double> execTimeQueue;
    private double estimatedExecTime;
    private final ReadWriteLock lock;

    public LoadLimiter(int estimationWindowSize) {
        this.estimationWindowSize = estimationWindowSize;
        this.execTimeQueue = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public double estimateWaiting(int queueSize, int consumerSize) {
        lock.readLock().lock();
        if (execTimeQueue.size() == 0) {
            lock.readLock().unlock();
            return 0;
        }
        double meanExecTime = estimatedExecTime / execTimeQueue.size();
        lock.readLock().unlock();
        return (meanExecTime * queueSize / consumerSize) + meanExecTime;
    }

    public void updateEstimation(double execTime) {
        lock.writeLock().lock();
        if (execTimeQueue.size() == estimationWindowSize) {
            estimatedExecTime -= execTimeQueue.remove();
        }
        estimatedExecTime += execTime;
        execTimeQueue.add(execTime);
        lock.writeLock().unlock();
    }
}
