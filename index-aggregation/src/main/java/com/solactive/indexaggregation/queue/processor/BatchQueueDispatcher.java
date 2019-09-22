package com.solactive.indexaggregation.queue.processor;

import com.solactive.indexaggregation.cache.TickerDataCacheStore;
import com.solactive.indexaggregation.concurrent.SequencedRunnable;
import com.solactive.indexaggregation.executor.SequencedThreadPoolExecutor;
import com.solactive.indexaggregation.processor.TickProcessor;
import com.solactive.indexaggregation.queue.TickerQueue;
import com.solactive.indexaggregation.web.data.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * BatchQueueDispatcher will start an ObserverThread which would keep polling the tickerQueue.
 * Any request coming t TickerQueue would be submitted to the SequencedThreadPoolExecutor,
 * which would cater to the parallel-cum sequenctial prccessing(as per key) of the request.
 */
@Component
public class BatchQueueDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchQueueDispatcher.class);
    private final TickerQueue<Tick> tickerQueue;
    private final SequencedThreadPoolExecutor executor;
    private final TickerDataCacheStore cache;


    public BatchQueueDispatcher(TickerQueue tickerQueue, TickerDataCacheStore cache, SequencedThreadPoolExecutor executor) {
        this.executor = executor;
        this.tickerQueue = tickerQueue;
        this.cache = cache;
        init();
    }

    private void init() {
        new QueueObserver(executor).start();
    }

    private class QueueObserver extends Thread {
        private final SequencedThreadPoolExecutor executor;

        public QueueObserver(SequencedThreadPoolExecutor executor) {
            this.executor = executor;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Tick tick = tickerQueue.getQueue().take();
                    LOGGER.info("Received a tick with instrument {}, and price: {}. Publishing to executor ", tick.getInstrument(), tick.getPrice());
                    BatchExecutionTask batchTask = new BatchExecutionTask(tick);
                    executor.execute(batchTask);
                } catch (Exception e) {
                    LOGGER.error("Problem in processing ticker", e);
                }
            }
        }
    }

    private class BatchExecutionTask implements SequencedRunnable {
        private final Tick tick;

        public BatchExecutionTask(Tick tick) {
            this.tick = tick;
        }

        @Override
        public Object getKey() {
            return tick.getInstrument();
        }

        @Override
        public boolean merge(SequencedRunnable task) {
            return false;
        }

        @Override
        public void run() {
            TickProcessor processor = new TickProcessor(tick, cache);
            processor.process();
        }
    }
}
