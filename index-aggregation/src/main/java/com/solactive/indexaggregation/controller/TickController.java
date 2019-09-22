package com.solactive.indexaggregation.controller;

import com.solactive.indexaggregation.queue.TickerQueue;
import com.solactive.indexaggregation.web.data.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TickController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickController.class);

    private final TickerQueue tickerQueue;
    private final long tickStatsTime;

    public TickController(TickerQueue tickerQueue, @Value("${tick.statistics.time.in.millis}") long tickStatsTime) {
        this.tickerQueue = tickerQueue;
        this.tickStatsTime = tickStatsTime;
    }

    @PostMapping(value = "/ticks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus ticks(@Valid @RequestBody Tick tick) throws InterruptedException {
        LOGGER.info("Received tick: {}", tick);
        HttpStatus status = compileStatus(tick.getTimestamp());
        tickerQueue.getQueue().put(tick);
        return status;
    }

    private HttpStatus compileStatus(Long timestamp) {
        return timestamp < System.currentTimeMillis() - tickStatsTime ? HttpStatus.NO_CONTENT : HttpStatus.OK;
    }

}
