package com.solactive.indexaggregation.web.data;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class TickerData {
    private List<Tick> ticks;
    private Double avg;
    private Double max;
    private Double min;
    private long count;



    public TickerData() {
        this.ticks = new ArrayList<>();
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Tick> getTicks() {
        return ticks;
    }

    public void setTicks(List<Tick> ticks) {
        this.ticks = new ArrayList<>(ticks);
        setCount(ticks.size());
        DoubleSummaryStatistics stats = ticks.parallelStream().mapToDouble(Tick::getPrice).summaryStatistics();
        setAvg(stats.getAverage());
        setMax(stats.getMax());
        setMin(stats.getMin());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TickerData that = (TickerData) o;

        if (count != that.count) return false;
        if (ticks != null ? !ticks.equals(that.ticks) : that.ticks != null) return false;
        if (avg != null ? !avg.equals(that.avg) : that.avg != null) return false;
        if (max != null ? !max.equals(that.max) : that.max != null) return false;
        return min != null ? min.equals(that.min) : that.min == null;
    }

    @Override
    public int hashCode() {
        int result = ticks != null ? ticks.hashCode() : 0;
        result = 31 * result + (avg != null ? avg.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }

    public void add(Tick tick) {
        this.getTicks().add(tick);
    }

    @Override
    public String toString() {
        return "TickerData{" +
                "avg=" + avg +
                ", max=" + max +
                ", min=" + min +
                ", count=" + count +
                '}';
    }
}
