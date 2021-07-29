package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;
import de.fraunhofer.iosb.svs.analysishub.data.RDFClient;
import de.fraunhofer.iosb.svs.analysishub.data.entity.Policy;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyDTOProjection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_NAMESPACE;

/**
 * A service for handling policy nodes.
 */
@Service
public class PolicyService extends ResourceService<Policy> {
    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private static final String POLICY = "policy";

    private final PolicyRepository repository;
    private final RDFClient rdfClient;

    @Autowired
    public PolicyService(PolicyRepository repository, RDFClient rdfClient) {
        this.repository = repository;
        this.rdfClient = rdfClient;
    }

    protected PolicyRepository getRepository() {
        return repository;
    }

    public Policy getPolicyByUri(String policyUri) {
        return this.getRepository().findByUri(policyUri).orElseThrow(resourceNotFound(POLICY, policyUri));
    }

    public Policy getPolicyByName(String policyName) {
        return getPolicyByUri(URIUtil.assemble(KNOWLEDGE_BASE_NAMESPACE, policyName));
    }

    public Page<PolicyDTOProjection> listDTOProjections(Pageable pageable) {
        return repository.findAllDTOBy(pageable);
    }


}
