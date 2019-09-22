package com.solactive.indexaggregation.controller;

import com.solactive.indexaggregation.service.TickerDataService;
import com.solactive.indexaggregation.web.data.TickerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final TickerDataService tickerDataService;

    public StatisticsController(TickerDataService tickerDataService) {
        this.tickerDataService = tickerDataService;
    }

    @GetMapping(path = "/statistics")
    public TickerResponse statistics() {
        return tickerDataService.getStatistics();

    }

    @GetMapping(path = "/statistics/{instrument_identifier}")
    public TickerResponse statistics(@PathVariable("instrument_identifier") String instrumentId) {
        return tickerDataService.getStatisticsForInstrument(instrumentId);
    }

}
