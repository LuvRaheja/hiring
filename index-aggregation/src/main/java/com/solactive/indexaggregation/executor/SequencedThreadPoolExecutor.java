package com.solactive.indexaggregation.executor;

import com.solactive.indexaggregation.concurrent.SequencedRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SequencedThreadPoolExecutor is an extension of ThreadPoolExecutor with the ability to process tasks in sequence on threads.
 * The tasks with a same key would be processed sequentially.
 * Usage: The tasks submitted to need to implement the SequencedRunnable interface.
 * Eg: When a Tick with same instrument id(key of Tick class) is sent immediately, before the request can be processed,
 * the second tick would wait before the first tick has been processed successfully.
 *
 * This would avoid any synchronization problems that might occur otherwise.
 */
@Component
public class SequencedThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequencedThreadPoolExecutor.class);
    private final ConcurrentMap<Object, SequenceWorker> workQueueMap;

    public SequencedThreadPoolExecutor(@Value("${executor.core.pool.size:15}") int corePoolSize, @Value("${executor.max.pool.size:15}") int maximumPoolSize, @Value("${executor.keepalive.time:60}") long keepAliveTime) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        workQueueMap = new ConcurrentHashMap<>();
    }

    @Override
    public void execute(Runnable command) {
        if (command instanceof SequencedRunnable) {
            Object key = ((SequencedRunnable) command).getKey();
            SequenceWorker workQueue = null;

            while (workQueue == null && key != null) {
                SequenceWorker newQueue = new SequenceWorker(key, this, workQueueMap);
                workQueue = workQueueMap.putIfAbsent(key, newQueue);
                if (workQueue == null) {
                    workQueue = newQueue;
                }
                if (!workQueue.offer(command)) {
                    workQueue = null;
                }
            }
        } else {
            super.execute(command);
        }
    }

    public void evictSequenceWorker(Object obj) {
        workQueueMap.remove(obj);
    }

    private static final class SequenceWorker implements Runnable {

        private final List<Runnable> workItems = new LinkedList<>();
        private boolean completed = false;
        private boolean started = false;
        private final Object key;
        private final Executor executor;
        private final ConcurrentMap<Object, SequenceWorker> containerMap;
        private final List<Runnable> tempWorkItems = new LinkedList<>();
        private ReentrantLock lock = new ReentrantLock();

        public SequenceWorker(Object key, Executor executor, ConcurrentMap<Object, SequenceWorker> containerMap) {
            this.key = key;
            this.executor = executor;
            this.containerMap = containerMap;

        }

        @Override
        public void run() {
            while (!workItems.isEmpty()) {
                for (Runnable r : workItems) {
                    r.run();
                }
                workItems.clear();
                checkAndDrainTempQueue();
            }
        }

        private void checkAndDrainTempQueue() {
            try {
                lock.lock();
                if (!tempWorkItems.isEmpty()) {
                    workItems.addAll(tempWorkItems);
                    tempWorkItems.clear();
                    mergeTasks();
                } else {
                    completed = true;
                    containerMap.remove(key, this);
                }
            } finally {
                lock.unlock();
            }
        }

        private void mergeTasks() {
            List<Runnable> removableTasks = new ArrayList<>();
            if (workItems.size() > 2) {
                SequencedRunnable seqTask = null;
                for (Runnable runnable : workItems) {
                    if (seqTask == null) {
                        seqTask = (SequencedRunnable) runnable;
                    } else {
                        if (!seqTask.merge((SequencedRunnable) runnable)) {
                            seqTask = (SequencedRunnable) runnable;
                        } else {
                            removableTasks.add(runnable);
                        }
                    }
                }
                if (!removableTasks.isEmpty()) {
                    workItems.removeAll(removableTasks);
                }
            }
        }

        public boolean offer(Runnable task) {

            boolean offerStatus = false;
            try {
                lock.tryLock(10, TimeUnit.SECONDS);
                if (!completed) {
                    if (!started) {
                        addToWorkQueue(task);
                    } else {
                        tempWorkItems.add(task);
                    }
                    offerStatus = true;
                }
            } catch (Exception e) {
                LOGGER.error("Could not add task for key {}", ((SequencedRunnable) task).getKey());
            } finally {
                lock.unlock();
            }
            return offerStatus;
        }

        private boolean addToWorkQueue(Runnable task) {
            workItems.add(task);
            if (!started) {
                executor.execute(this);
                started = true;
            }
            return true;
        }
    }

}
