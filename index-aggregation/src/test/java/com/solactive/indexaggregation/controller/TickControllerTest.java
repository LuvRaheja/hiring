package com.solactive.indexaggregation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solactive.indexaggregation.queue.TickerQueue;
import com.solactive.indexaggregation.web.data.Tick;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TickControllerTest {

    private static final String TICKER_PATH = "/ticks/";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    TickerQueue tickerQueue;

    @Test
    public void shouldRejectWhenInvalidRequest() throws Exception {
        Tick tick = new Tick(null, null, null);
        this.mockMvc.perform(post(TICKER_PATH).content(asJsonString(tick))
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnCreatedForExpectedInput() throws Exception {
        Tick tick = new Tick("test1",143.43D, new Date().getTime());
        this.mockMvc.perform(post(TICKER_PATH).content(asJsonString(tick))
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturn204ForOldRequests() throws Exception {
        Tick tick = new Tick("test1",143.43D, System.currentTimeMillis()-70000);
        this.mockMvc.perform(post(TICKER_PATH).content(asJsonString(tick))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("NO_CONTENT")));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}