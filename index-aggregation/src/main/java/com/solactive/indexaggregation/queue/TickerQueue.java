package com.solactive.indexaggregation.queue;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A shared queue between different components.
 * @param <T>
 */
@Component
public class TickerQueue<T> {

    private final BlockingQueue<T> queue;

    public TickerQueue() {
        this.queue = new LinkedBlockingDeque<>();
    }


    public BlockingQueue<T> getQueue() {
        return queue;
    }
}
