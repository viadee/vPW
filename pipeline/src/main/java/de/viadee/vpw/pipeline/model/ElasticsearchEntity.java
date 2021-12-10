package de.viadee.vpw.pipeline.model;

import java.util.Date;

public interface ElasticsearchEntity {

    String getType();

    default Object getParentJoin() {
        return getType();
    }

    @SuppressWarnings("unused")
    Date getTimestamp(); // einheitliches Datumsfeld in Elastic für alle Event-Typen
}
