package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyImplementation;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyImplementationDTOProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyImplementationRepository extends ResourceRepository<PolicyImplementation> {
    Page<PolicyImplementationDTOProjection> findAllDTOByPolicyUri(Pageable pageable, String policyUri);

    long countByPolicyUri(String policyUri);
}
