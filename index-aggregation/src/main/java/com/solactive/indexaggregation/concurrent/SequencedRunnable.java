package com.solactive.indexaggregation.concurrent;

public interface SequencedRunnable extends Runnable{

    Object getKey();
    boolean merge(SequencedRunnable task);
}
