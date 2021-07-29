package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * A service for handling queries.
 */
@Service
public class QueryService extends ResourceService<Query> {

    private final QueryRepository queryRepository;

    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    protected ResourceRepository<Query> getRepository() {
        return queryRepository;
    }

    public Page<Query> findAll(Pageable pageable) {
        return this.queryRepository.findAll(pageable);
    }
}
