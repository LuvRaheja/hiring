package com.solactive.indexaggregation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solactive.indexaggregation.web.data.Tick;
import com.solactive.indexaggregation.web.data.TickerResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    private static final String ALL_STATS_PATH = "/statistics";
    private static final String TICKER_PATH = "/ticks/";

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup(){

    }

    @Test
    public void testAllPossibleScenarios() throws Exception {
        //1. Test for Empty Response when nothing is sent

        TickerResponse response = getResponse(null, null, null, 0L);
        testForNoTicker(response);

        //2. Test for Response when one tick is sent
        Tick tick1 = testForSingleTickStats();

        //2. Test for Response when one instrument's multiple ticks are sent.
        Tick tick2 = testForsingleInstrumentMultipleTicks(tick1);

        //3. Test for Response when multiple instruments and multiple ticks are sent.
        testForMultipleInstrumentMultipleTicks(tick1, tick2);
    }

    private void testForMultipleInstrumentMultipleTicks(Tick tick1, Tick tick2) throws Exception {
        TickerResponse response;
        Tick tick3 = getInputTick("test2", 300.50D);
        response = new TickerResponse((tick1.getPrice()+tick2.getPrice() + tick3.getPrice())/3,
                Math.max((Math.max(tick1.getPrice(), tick2.getPrice())), tick3.getPrice()),Math.min(Math.min(tick1.getPrice(), tick2.getPrice()),tick3.getPrice()),3L);
        sendTickAndGetStats(tick3, response);
    }

    private Tick testForsingleInstrumentMultipleTicks(Tick tick1) throws Exception {
        TickerResponse response;
        Tick tick2 = getInputTick("test1", 200.50D);
        response = new TickerResponse((tick1.getPrice()+tick2.getPrice())/2,Math.max(tick1.getPrice(), tick2.getPrice()),Math.min(tick1.getPrice(), tick2.getPrice()),2L);
        sendTickAndGetStats(tick2, response);
        return tick2;
    }

    private void getStats(TickerResponse response) throws Exception {
        mockMvc.perform(get(ALL_STATS_PATH)).andExpect(status().isOk()).andExpect(content().json(asJsonString(response)));
    }

    private Tick testForSingleTickStats() throws Exception {
        Tick tick = getInputTick("test1", 100.50D);
        TickerResponse response = getResponse(tick.getPrice(),tick.getPrice(),tick.getPrice(),1L);
        sendTickAndGetStats(tick, response);
        return tick;
    }

    private void sendTickAndGetStats(Tick tick, TickerResponse response) throws Exception {
        sendTick(tick);
        Thread.sleep(10);
        getStats(response);
    }

    private void sendTick(Tick tick) throws Exception {
        mockMvc.perform(post(TICKER_PATH).content(asJsonString(tick))
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    private Tick getInputTick(String instrument, double price) {
        return new Tick(instrument, price, System.currentTimeMillis());
    }

    private void testForNoTicker(TickerResponse response) throws Exception {
        mockMvc.perform(get(ALL_STATS_PATH)).andDo(print()).andExpect(status().isOk()).andExpect(content().json(asJsonString(response)));
    }

    private TickerResponse getResponse(Double avg, Double max, Double min, long count) {
        return new TickerResponse(avg, max, min, count);
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}