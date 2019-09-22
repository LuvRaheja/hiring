package com.solactive.indexaggregation.service;

import com.solactive.indexaggregation.cache.TickerDataCacheStore;
import com.solactive.indexaggregation.web.data.TickerData;
import com.solactive.indexaggregation.web.data.TickerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TickerDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TickerDataService.class);
    private final TickerDataCacheStore cacheStore;

    public TickerDataService(TickerDataCacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    public TickerResponse getStatistics() {
        TickerData tickerData = cacheStore.getTickerStats();
        if (tickerData != null) {
            LOGGER.info("Returning stats for All instruments: {} ", tickerData);
            return new TickerResponse(tickerData.getAvg(), tickerData.getMax(), tickerData.getMin(), tickerData.getCount());
        } else {
            return null;
        }
    }

    public TickerResponse getStatisticsForInstrument(String instrumentId) {
        TickerData tickerData = cacheStore.getStatisticsOfInstrument(instrumentId);
        if (tickerData != null) {
            LOGGER.info("Returning stats for instrument {} : {} ", instrumentId, tickerData);
            return new TickerResponse(tickerData.getAvg(), tickerData.getMax(), tickerData.getMin(), tickerData.getCount());
        } else {
            LOGGER.info("No ticker found for instrument {}", instrumentId);
            return new TickerResponse(null, null, null, 0L);
        }
    }
}
