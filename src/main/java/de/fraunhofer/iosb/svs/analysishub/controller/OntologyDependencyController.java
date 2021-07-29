package de.fraunhofer.iosb.svs.analysishub.controller;

import de.fraunhofer.iosb.svs.analysishub.data.service.OntologyDependencyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestApiV1Controller
public class OntologyDependencyController {
    private static final Logger log = LoggerFactory.getLogger(PolicyImplementationController.class);

    private final OntologyDependencyService ontologyDependencyService;

    @Autowired
    public OntologyDependencyController(OntologyDependencyService ontologyDependencyService) {
        this.ontologyDependencyService = ontologyDependencyService;
    }

    /**
     * Gets the ontology dependency file by its unique name.
     *
     * @param ontoDepName the unique name
     * @return
     */
    @GetMapping(path = "/ontology-dependency/{ontoDepName}", produces = "application/octet-stream")
    public ResponseEntity<FileSystemResource> getOntologyDependencyFileById(@PathVariable("ontoDepName") String ontoDepName) {
        log.info("Get ontology dependency file");
        FileSystemResource fileSystemResource = ontologyDependencyService.getOntologyDependencyFileByName(ontoDepName);
        try {
            return ResponseEntity.ok().contentLength(fileSystemResource.contentLength()).body(fileSystemResource);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
