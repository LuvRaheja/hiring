package com.solactive.indexaggregation.processor;

import com.solactive.indexaggregation.cache.TickerDataCacheStore;
import com.solactive.indexaggregation.web.data.Tick;

public class TickProcessor {
    private final Tick tick;
    private final TickerDataCacheStore cache;

    public TickProcessor(Tick tick, TickerDataCacheStore cache) {
        this.tick = tick;
        this.cache = cache;
    }

    public void process() {
        cache.put(tick);
    }
}
