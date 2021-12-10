package de.viadee.vpw.analyzer.dto.entity;

import java.util.Date;

public class HistogramBucket {

    private Date startTime;

    private long size;

    public HistogramBucket(Date startTime, long size) {
        this.startTime = startTime;
        this.size = size;
    }

    public Date getStartTime() {
        return startTime;
    }

    public long getSize() {
        return size;
    }
}
