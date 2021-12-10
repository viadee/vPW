package de.viadee.vpw.analyzer.data.entity;

import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Model used for all entities with an uuid as primary key
 */
@MappedSuperclass
public class AbstractUUIDModel extends AbstractModel<UUID> {
    public AbstractUUIDModel(UUID id) {
        super(id);
    }

    public AbstractUUIDModel() {
        this(UUID.randomUUID());
    }
}
