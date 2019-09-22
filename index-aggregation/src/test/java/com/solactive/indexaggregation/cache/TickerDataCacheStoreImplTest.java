package com.solactive.indexaggregation.cache;

import com.solactive.indexaggregation.data.builder.TickerDataBuilder;
import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerData;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TickerDataCacheStoreImplTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    TickerDataBuilder tickerDataBuilder;

    TickerDataCacheStoreImpl underTest;
    Tick tick;
    TickerData tickerData;

    @Before
    public void setup() {
        context.setImposteriser(ClassImposteriser.INSTANCE);
        tickerDataBuilder = context.mock(TickerDataBuilder.class);
        tick = context.mock(Tick.class);
        tickerData = context.mock(TickerData.class);
        underTest = new TickerDataCacheStoreImpl(tickerDataBuilder, 60000);
    }

    @Test
    public void testCacheWithNewInstrument() {
        context.checking(new Expectations() {
            {
                allowing(tick).getInstrument();
                will(returnValue("INSTRUMENT1"));
                oneOf(tickerDataBuilder).build(new TickerData(), tick);
                oneOf(tickerDataBuilder).updateStats(underTest.getTickerStats(), underTest.getCache());
            }
        });
        underTest.put(tick);
        context.assertIsSatisfied();
    }


}
