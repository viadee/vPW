package de.viadee.vpw.analyzer.data.entity;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Model used from all entities
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractModel<K> {

    /**
     * Primary key
     */
    @Id
    private K id;

    /**
     * Version of the entity. Used as E-Tag
     */
    @Version
    private Long etag;

    /**
     * Last change of entity
     */
    @LastModifiedDate
    private Date lastChanged;

    public AbstractModel(K id) {
        this.id = id;
    }

    public K getId() {
        return id;
    }

    public void setId(K id) {
        this.id = id;
    }

    public Long getEtag() {
        return this.etag != null ? this.etag : 1L;
    }

    public void setEtag(Long etag) {
        this.etag = etag;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }
}
