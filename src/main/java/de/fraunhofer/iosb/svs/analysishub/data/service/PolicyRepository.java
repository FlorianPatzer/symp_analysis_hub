package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Policy;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyDTOProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyRepository extends ResourceRepository<Policy> {
    Page<PolicyDTOProjection> findAllDTOBy(Pageable pageable);
}