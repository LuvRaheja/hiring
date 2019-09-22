package com.solactive.indexaggregation.cache;

import com.solactive.indexaggregation.data.builder.TickerDataBuilder;
import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TickerDataCacheStoreImpl implements TickerDataCacheStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerDataCacheStoreImpl.class);
    private final Map<String, TickerData> tickerCache;
    private final TickerData tickerStats;
    private final TickerDataBuilder tickerDataBuilder;
    private final long tickStatsTime;

    TickerDataCacheStoreImpl(TickerDataBuilder tickerDataBuilder, @Value("${tick.statistics.time.in.millis}") long tickStatsTime) {
        this.tickerStats = new TickerData();
        this.tickStatsTime = tickStatsTime;
        this.tickerDataBuilder = tickerDataBuilder;
        tickerCache = new ConcurrentHashMap<>();
    }

    @Override
    public void put(Tick tick) {
        TickerData tickerData = getTickerData(tick);
        tickerDataBuilder.build(tickerData, tick);
        tickerCache.put(tick.getInstrument(), tickerData);
        tickerDataBuilder.updateStats(tickerStats, tickerCache);
    }

    private TickerData getTickerData(Tick tick) {
        if (!tickerCache.containsKey(tick.getInstrument())) {
            LOGGER.info("Tick Data with instrument {} does not exist since past {} millis. Adding new entry", tick.getInstrument(), tickStatsTime);
            return new TickerData();
        } else {
            LOGGER.info("Tick Data with instrument {} Updating with price: {} ", tick.getInstrument(), tick.getPrice());
            return tickerCache.get(tick.getInstrument());
        }
    }

    @Override
    public TickerData getStatisticsOfInstrument(String instrument) {
        return tickerCache.get(instrument);
    }

    @Override
    public Map<String, TickerData> getCache() {
        return tickerCache;
    }

    @Override
    public TickerData getTickerStats() {
        return tickerStats;
    }
}
