package de.fraunhofer.iosb.svs.analysishub.data.filesystem;

import de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependencyFile;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * A service that handles the ontology dependency files.
 */
@Service
public class OntologyDependencyFileService {
    private static final Logger log = LoggerFactory.getLogger(OntologyDependencyFileService.class);
    private static final String ONTOLOGY_DEPENDENCY_FILE = "ontology dependency file";

    private final FileSystemRepository fileSystemRepository;
    private final OntologyDependencyFileRepository ontologyDependencyFileRepository;

    @Autowired
    public OntologyDependencyFileService(FileSystemRepository fileSystemRepository, OntologyDependencyFileRepository ontologyDependencyFileRepository) {
        this.fileSystemRepository = fileSystemRepository;
        this.ontologyDependencyFileRepository = ontologyDependencyFileRepository;
    }

    public FileSystemResource getOntologyDependencyFileById(Long ontoFileId) {
        OntologyDependencyFile ontologyDependencyFile = ontologyDependencyFileRepository.findById(ontoFileId)
                .orElseThrow(() -> new ResourceNotFoundException(ONTOLOGY_DEPENDENCY_FILE, ontoFileId));
        return getOntologyDependencyFile(ontologyDependencyFile);
    }

    /**
     * Gets the contents of an ontology dependency file from the file system.
     *
     * @param ontologyDependencyFile
     * @return
     */
    public FileSystemResource getOntologyDependencyFile(OntologyDependencyFile ontologyDependencyFile) {
        FileSystemResource fileSystemResource = fileSystemRepository.findInFileSystem(ontologyDependencyFile.getLocation());
        if (!fileSystemResource.exists() || !fileSystemResource.isReadable()) {
            log.error("File not found or not readable: {}", ontologyDependencyFile.getLocation());
            throw new RuntimeException("File not found or not readable on filesystem for " + ontologyDependencyFile.getId());
        }
        return fileSystemResource;
    }

    /**
     * Saves an stream of data as an ontology on the file system.
     *
     * @param inputStream
     * @param ontologyName
     * @return
     */
    public String saveOntologyDependencyFile(InputStream inputStream, String ontologyName) {
        try {
            return saveOntologyDependencyFile(inputStream.readAllBytes(), ontologyName);
        } catch (IOException ioe) {
            throw new UncheckedIOException("OntologyDependency File IOException", ioe);
        }
    }

    /**
     * Saves a byte array as an ontology on the file system.
     *
     * @param content
     * @param ontologyName
     * @return
     */
    public String saveOntologyDependencyFile(byte[] content, String ontologyName) {
        try {
            return fileSystemRepository.save(content, ontologyName);
        } catch (IOException ioe) {
            throw new UncheckedIOException("OntologyDependency File IOException", ioe);
        }
    }
}
