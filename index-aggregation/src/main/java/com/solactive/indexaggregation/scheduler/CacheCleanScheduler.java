package com.solactive.indexaggregation.scheduler;

import com.solactive.indexaggregation.cache.TickerDataCacheStore;
import com.solactive.indexaggregation.data.builder.TickerDataBuilder;
import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableScheduling
@Component
public class CacheCleanScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheCleanScheduler.class);
    private final TickerDataCacheStore store;
    private final TickerDataBuilder tickerDataBuilder;
    private final long tickerStatisticsTime;

    public CacheCleanScheduler(TickerDataCacheStore store, TickerDataBuilder tickerDataBuilder, 
                               @Value("${tick.statistics.time.in.millis}") long tickerStatisticsTime) {
        this.store = store;
        this.tickerDataBuilder = tickerDataBuilder;
        this.tickerStatisticsTime = tickerStatisticsTime;
    }

    @Scheduled(fixedDelay = 1000)
    public void clearMap() {
        Map<String, TickerData> tickMap = store.getCache();
        TickerData tickerStats = store.getTickerStats();
        tickMap.entrySet().forEach(this::removeAndUpdateMap);
        tickMap.entrySet().removeIf(entry -> CollectionUtils.isEmpty(entry.getValue().getTicks()));
        tickerDataBuilder.updateStats(tickerStats, tickMap);
    }

    private void removeAndUpdateMap(Map.Entry<String, TickerData> entry) {
        List<Tick> l = new ArrayList<>(entry.getValue().getTicks().stream().filter(t -> t.getTimestamp() > System.currentTimeMillis() - tickerStatisticsTime).collect(Collectors.toList()));
        if (entry.getValue().getTicks() != null && l.size() < entry.getValue().getTicks().size()) {
            LOGGER.info("Evicted {} old entries from cache.", entry.getValue().getTicks().size() - l.size());
        }
        entry.getValue().setTicks(l);
    }
}
