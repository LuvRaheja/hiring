package com.solactive.indexaggregation.web.data;

import javax.validation.constraints.NotNull;


public class Tick {
    @NotNull(message = "Please provide the instrument")
    private String instrument;

    @NotNull(message = "Please provide the price")
    private Double price;

    @NotNull(message = "Please provide the timestamp")
    private Long timestamp;

    public Tick(String instrument, Double price, Long timestamp) {
        this.instrument = instrument;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    @Override
    public String toString() {
        return "Tick{" +
                "instrument='" + instrument + '\'' +
                ", price=" + price +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tick tick = (Tick) o;

        return instrument.equals(tick.instrument);
    }

    @Override
    public int hashCode() {
        return instrument.hashCode();
    }
}
