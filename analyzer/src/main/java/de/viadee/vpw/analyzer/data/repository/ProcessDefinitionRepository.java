package de.viadee.vpw.analyzer.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.viadee.vpw.analyzer.data.entity.ProcessDefinition;
import de.viadee.vpw.analyzer.data.entity.ProcessDefinitionProjection;

/**
 * Expose entity as repository and provide it as paged rest apis.
 * List results are projected
 * CORS with exposed ETag header is enabled
 */
@CrossOrigin(exposedHeaders = { "ETag" })
@RepositoryRestResource(excerptProjection = ProcessDefinitionProjection.class)
public interface ProcessDefinitionRepository extends PagingAndSortingRepository<ProcessDefinition, String> {

    /**
     * Do not expose delete method as rest api
     *
     * @param processDefinition process definition to delete
     */
    @Override
    @RestResource(exported = false)
    void delete(ProcessDefinition processDefinition);

    /**
     * Do not expose method as rest api
     */
    @Override
    @RestResource(exported = false)
    void deleteAll();
}
