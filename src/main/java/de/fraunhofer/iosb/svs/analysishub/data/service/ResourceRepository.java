package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Resource;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ResourceRepository<T extends Resource> extends Neo4jRepository<T, Long> {

    Optional<T> findByUri(String uri);

    <T1> Optional<T1> findByUri(String uri, Class<T1> clazz);

    boolean existsByUri(String uri);

    Page<ResourceUriProjection> findAllProjectedBy(Pageable pageable);
}
