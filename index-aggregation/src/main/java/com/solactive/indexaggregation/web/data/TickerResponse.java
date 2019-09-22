package com.solactive.indexaggregation.web.data;

public class TickerResponse {

    private Double avg;
    private Double max;
    private Double min;
    private Long count;

    public TickerResponse(Double avg, Double max, Double min, Long count) {
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public Double getAvg() {
        return avg;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public Long getCount() {
        return count;
    }
}
