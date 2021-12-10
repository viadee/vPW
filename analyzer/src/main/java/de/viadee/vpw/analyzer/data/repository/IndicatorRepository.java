package de.viadee.vpw.analyzer.data.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.viadee.vpw.analyzer.data.entity.Indicator;
import de.viadee.vpw.analyzer.data.entity.IndicatorProjection;

/**
 * Expose entity as repository and provide it as paged rest apis.
 * List results are projected
 * CORS with exposed ETag header is enabled
 */
@CrossOrigin(exposedHeaders = { "ETag" })
@RepositoryRestResource(excerptProjection = IndicatorProjection.class)
public interface IndicatorRepository extends PagingAndSortingRepository<Indicator, UUID> {

    @SuppressWarnings("unused")
    @RestResource(path = "findByProcessDefinitionId", rel = "findByProcessDefinitionId")
    Page<Indicator> findByProcessDefinitionIdOrProcessDefinitionIdIsNull(
            @Param("processDefinitionId") String processDefinitionId, Pageable p);
}
