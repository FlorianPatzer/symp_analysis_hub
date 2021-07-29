package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;
import de.fraunhofer.iosb.svs.analysishub.controller.OntologyDependencyController;
import de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependency;
import de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependencyFile;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.OntologyDependencyFileProjection;
import de.fraunhofer.iosb.svs.analysishub.data.filesystem.OntologyDependencyFileService;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_NAMESPACE;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A service for handling the ontology dependency nodes.
 */
@Service
public class OntologyDependencyService extends ResourceService<OntologyDependency> {
    private static final Logger log = LoggerFactory.getLogger(OntologyDependencyService.class);
    private static final String ONTOLOGY_DEPENDENCY = "ontology dependency";

    private final OntologyDependencyRepository ontologyDependencyRepository;
    private final OntologyDependencyFileService ontologyDependencyFileService;

    @Autowired
    public OntologyDependencyService(OntologyDependencyRepository ontologyDependencyRepository, OntologyDependencyFileService ontologyDependencyFileService) {
        this.ontologyDependencyRepository = ontologyDependencyRepository;
        this.ontologyDependencyFileService = ontologyDependencyFileService;
    }

    public List<OntologyDependency> getOntologyDependencies() {
        return ontologyDependencyRepository.findAll();
    }

    public FileSystemResource getOntologyDependencyFileByName(String ontoDepName) {
        return getOntologyDependencyFileByUri(URIUtil.assemble(KNOWLEDGE_BASE_NAMESPACE, ontoDepName));
    }

    /**
     * Gets the file contents of an ontology from the file system given an ontology dependency uri.
     */
    public FileSystemResource getOntologyDependencyFileByUri(String ontoDepUri) {
        OntologyDependencyFileProjection ontologyDependencyFileProjection = ontologyDependencyRepository.findByUri(ontoDepUri, OntologyDependencyFileProjection.class).orElseThrow(() -> {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(ONTOLOGY_DEPENDENCY, ontoDepUri);
            log.error("error", resourceNotFoundException);
            return resourceNotFoundException;
        });
        return ontologyDependencyFileService.getOntologyDependencyFile(ontologyDependencyFileProjection.getFile());
    }

    public Page<OntologyDependency> findAll(Pageable pageable) {
        return ontologyDependencyRepository.findAll(pageable);
    }


    @Override
    protected ResourceRepository<OntologyDependency> getRepository() {
        return ontologyDependencyRepository;
    }

    /**
     * Saves a ontology dependency. The ontology dependency file is saved to the file system.
     * The path is connected to the ontology dependency. Also the link to the API to reach the ontology file is added.
     */
    public OntologyDependency update(OntologyDependency ontologyDependency, InputStream inputStream) {

        String location = ontologyDependencyFileService.saveOntologyDependencyFile(inputStream, ontologyDependency.getPrefix() + ".owl");
        OntologyDependencyFile ontologyDependencyFile = new OntologyDependencyFile(location);
        ontologyDependency.setFile(ontologyDependencyFile);
        ontologyDependency.setDownloadLink(linkTo(methodOn(OntologyDependencyController.class).getOntologyDependencyFileById(ontologyDependency.getLocalName())).toString());
        return this.update(ontologyDependency);
    }
}
