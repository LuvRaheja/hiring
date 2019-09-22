package com.solactive.indexaggregation.data.builder;

import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TickerDataBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TickerDataBuilder.class);

    public void build(TickerData tickerData, Tick tick) {
        List<Tick> ticks = tickerData.getTicks();
        LOGGER.info("Adding tick with instrument {} to existing ticks with count {}", tick.getInstrument(), ticks.size());
        ticks.add(tick);
        buildSummary(tickerData, ticks);
        LOGGER.info("Built summary for instrument {} : {}", tick.getInstrument(), tickerData);
    }

    private void buildSummary(TickerData tickerData, List<Tick> ticks) {
        DoubleSummaryStatistics stats = ticks.parallelStream().mapToDouble(Tick::getPrice).summaryStatistics();
        tickerData.setCount(ticks.size());
        tickerData.setAvg(stats.getAverage());
        tickerData.setMax(stats.getMax());
        tickerData.setMin(stats.getMin());
    }

    public void updateStats(TickerData tickerSummary, Map<String, TickerData> tickMap) {
        List<Tick> ticks = tickMap.values().parallelStream().flatMap(t -> t.getTicks().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ticks)) {
            buildSummary(tickerSummary, ticks);
        }
    }
}
