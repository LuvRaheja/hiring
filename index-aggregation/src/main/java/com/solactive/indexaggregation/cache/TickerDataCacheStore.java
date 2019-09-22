package com.solactive.indexaggregation.cache;

import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerData;

import java.util.Map;

public interface TickerDataCacheStore {
    void put(Tick tick);

    TickerData getStatisticsOfInstrument(String instrument);

    Map<String, TickerData> getCache();

    TickerData getTickerStats();
}
