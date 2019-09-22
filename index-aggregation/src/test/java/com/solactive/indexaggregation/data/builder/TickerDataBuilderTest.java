package com.solactive.indexaggregation.data.builder;

import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerData;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TickerDataBuilderTest {

    TickerDataBuilder underTest = new TickerDataBuilder();

    @Test
    public void shouldBuildTickerDataForNewInstrument() {
        TickerData tickerData = new TickerData();
        Tick tick = new Tick("Instrument", 100D, System.currentTimeMillis());
        underTest.build(tickerData, tick);
        Assert.assertEquals(tick.getPrice(), tickerData.getAvg());
        Assert.assertEquals(tick.getPrice(), tickerData.getMin());
        Assert.assertEquals(tick.getPrice(), tickerData.getMax());
        Assert.assertEquals(1, tickerData.getCount());
    }

    @Test
    public void shouldBuildTickerDataForNewInstrumentMultipleTicks() {
        TickerData tickerData = new TickerData();
        Tick tick = new Tick("Instrument", 100D, System.currentTimeMillis());
        underTest.build(tickerData, tick);
        Tick tick2 = new Tick("Instrument", 200D, System.currentTimeMillis());
        underTest.build(tickerData, tick2);
        Tick tick3 = new Tick("Instrument", 300D, System.currentTimeMillis());
        underTest.build(tickerData, tick3);
        Assert.assertEquals(getAveragePrice(tick.getPrice(), tick2.getPrice(), tick3.getPrice()), tickerData.getAvg());
        Assert.assertEquals(getMin(tick.getPrice(), tick2.getPrice(), tick3.getPrice()), tickerData.getAvg(), tickerData.getMin());
        Assert.assertEquals(getMax(tick.getPrice(), tick2.getPrice(), tick3.getPrice()), tickerData.getAvg(), tickerData.getMax());
        Assert.assertEquals(3, tickerData.getCount());
    }

    private Double getMin(Double... prices) {
        return Stream.of(prices).mapToDouble(value -> value).min().getAsDouble();
    }

    private Double getAveragePrice(Double... prices) {
        return Stream.of(prices).mapToDouble(value -> value).sum()/prices.length;
    }

    private Double getMax(Double... prices) {
        return Stream.of(prices).mapToDouble(value -> value).max().getAsDouble();
    }
}
