package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;
import de.fraunhofer.iosb.svs.analysishub.controller.PolicyImplementationController;
import de.fraunhofer.iosb.svs.analysishub.data.RDFClient;
import de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyImplementation;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyImplementationDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.dto.PolicyImplementationDTO;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_NAMESPACE;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A service to handle policy implementation nodes.
 */
@Service
public class PolicyImplementationService extends ResourceService<PolicyImplementation> {
    private static final Logger log = LoggerFactory.getLogger(PolicyImplementationService.class);
    private static final String POLICY_IMPLEMENTATION = "Policy Implementation";
    private final PolicyImplementationRepository repository;
    private final RDFClient rdfClient;


    @Autowired
    public PolicyImplementationService(PolicyImplementationRepository repository, RDFClient rdfClient) {
        this.repository = repository;
        this.rdfClient = rdfClient;
    }

    @Override
    protected ResourceRepository<PolicyImplementation> getRepository() {
        return repository;
    }

    public PolicyImplementation getPolicyImplementationByUri(String policyImplementationUri) {
        return this.getRepository().findByUri(policyImplementationUri).orElseThrow(resourceNotFound(POLICY_IMPLEMENTATION, policyImplementationUri));
    }

    /**
     * Gets the rdf model as ByteArrayResource given the name of the policy implementation.
     */
    public ByteArrayResource getPolicyModelByName(String policyImplementationName) {
        return getPolicyModel(URIUtil.assemble(KNOWLEDGE_BASE_NAMESPACE, policyImplementationName));
    }

    /**
     * Gets the rdf model as ByteArrayResource given the uri of the policy implementation.
     */
    public ByteArrayResource getPolicyModel(String policyImplementationUri) {
        if (!repository.existsByUri(policyImplementationUri)) {
            throw new ResourceNotFoundException(POLICY_IMPLEMENTATION, policyImplementationUri);
        }
        return rdfClient.getPolicyOntology(policyImplementationUri);
    }

    public PolicyImplementationDTO getPolicyImplementationDTOByName(String policyImplementationName) {
        return getPolicyImplementationDTOByUri(URIUtil.assemble(KNOWLEDGE_BASE_NAMESPACE, policyImplementationName));
    }

    public PolicyImplementationDTO getPolicyImplementationDTOByUri(String policyImplementationUri) {
        PolicyImplementationDTOProjection policyImplementationDTOProjection = getPolicyImplementationDTOProjectionByUri(policyImplementationUri).orElseThrow(resourceNotFound(POLICY_IMPLEMENTATION, policyImplementationUri));
        String localName = URIUtil.getLocalName(policyImplementationDTOProjection.getUri());
        String modelLink = getModelLink(localName);
        return new PolicyImplementationDTO(policyImplementationDTOProjection.getUri(), localName, modelLink, policyImplementationDTOProjection.getDescription(), policyImplementationDTOProjection.getLastChanged());
    }

    public Optional<PolicyImplementationDTOProjection> getPolicyImplementationDTOProjectionByUri(String policyImplementationUri) {
        return repository.findByUri(policyImplementationUri, PolicyImplementationDTOProjection.class);
    }

    public Page<PolicyImplementationDTOProjection> listDTOProjectionsByPolicy(Pageable pageable, PolicyDTOProjection policyDTOProjection) {
        return repository.findAllDTOByPolicyUri(pageable, policyDTOProjection.getUri());
    }

    /**
     * Gets a link to the API where the policy model can be accessed.
     */
    public String getModelLink(String name) {
        return linkTo(methodOn(PolicyImplementationController.class).getPolicyModelById(name)).toUri().toString();
    }

    /**
     * Gets a link to the API where the policy implementation meta information can be accessed.
     */
    public String getPolicyImplementationLink(String name) {
        return linkTo(methodOn(PolicyImplementationController.class).getPolicyImplementationById(name)).toUri().toString();
    }

    public int countByPolicy(PolicyDTOProjection policyDTOProjection) {
        return (int) repository.countByPolicyUri(policyDTOProjection.getUri());
    }
}
