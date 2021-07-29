package de.fraunhofer.iosb.svs.analysishub.data.filesystem;

import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Repository that holds ontology dependencies on the file system.
 */
@Repository
public class FileSystemRepository {
    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);
    public final String RESOURCES_DIR = FileSystemRepository.class.getResource("/")
            .getPath();
    private final Path ontologyDependencyDir;

    public FileSystemRepository(@Value("${ontology-dependency.directory}") String ontologyDependencyDir) {
        this.ontologyDependencyDir = Paths.get(ontologyDependencyDir).toAbsolutePath().normalize();
    }

    /**
     * Saves the contents of ontology to a file.
     *
     * @param content      the byte content of the file
     * @param ontologyName the name of the ontology
     * @return the absolute path to the ontology
     * @throws IOException
     */
    String save(byte[] content, String ontologyName) throws IOException {
        log.debug("Save to {}", ontologyDependencyDir.toAbsolutePath());
        Path newFile = this.ontologyDependencyDir.resolve(new Date().getTime() + "-" + ontologyName);
        Files.createDirectories(newFile.getParent());
        log.debug("Saving '{}' to '{}'", newFile.getFileName(), newFile.toAbsolutePath().toString());
        Files.write(newFile, content);

        return newFile.toAbsolutePath()
                .toString();
    }

    FileSystemResource findInFileSystem(String location) {
        return new FileSystemResource(Paths.get(location));
    }
}
