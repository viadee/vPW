package de.viadee.vpw.pipeline.model;

import java.util.Date;

public interface ElasticsearchEntity {

    String getType();

    default Object getParentJoin() {
        return getType();
    }

    @SuppressWarnings("unused")
    Date getTimestamp(); // uniform date field in Elastic for all event types
}
