package de.viadee.vpw.analyzer.service.histogram.elastic.dto;

public class Bounds {

    private long min;

    private long max;

    public Bounds(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }
}
