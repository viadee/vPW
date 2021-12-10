package de.viadee.vpw.analyzer.data.repository;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import de.viadee.vpw.analyzer.data.entity.Dashboard;
import de.viadee.vpw.analyzer.data.entity.SimpleProjection;

/**
 * Expose entity as repository and provide it as paged rest apis.
 * List results are projected
 * CORS with exposed ETag header is enabled
 *
 * @see SimpleProjection
 */
@CrossOrigin(exposedHeaders = { "ETag" })
@RepositoryRestResource(excerptProjection = SimpleProjection.class)
public interface DashboardRepository extends PagingAndSortingRepository<Dashboard, UUID> {

}
