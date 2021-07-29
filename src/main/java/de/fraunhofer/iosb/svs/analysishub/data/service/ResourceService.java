package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Resource;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceAlreadyExistsException;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * An abstract service for all resource nodes.
 *
 * @param <T>
 */
public abstract class ResourceService<T extends Resource> {
    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    public ResourceService() {
    }

    protected abstract ResourceRepository<T> getRepository();

    public Optional<T> get(Long id) {
        return this.getRepository().findById(id);
    }

    public T getOrThrow(Long id) {
        return this.getRepository().findById(id).orElseThrow(resourceNotFound("Resource", id.toString()));
    }

    public T update(T entity) {
        log.debug("Saving resource with uri '{}'", entity.getUri());
        try {
            return this.getRepository().save(entity);
        } catch (DataIntegrityViolationException dive) {
            if (dive.getMessage() != null) {
                if (dive.getMessage().contains("Neo.ClientError.Schema.ConstraintValidationFailed")) {
                    log.debug("Resource {} already exists", entity.getUri());
                    throw new ResourceAlreadyExistsException("Resource with uri " + entity.getUri() + " already exists", dive);
                } else {
                    throw dive;
                }
            } else {
                throw new RuntimeException("Unexpected error message null;");
            }
        }
    }

    public void delete(Long id) {
        this.getRepository().deleteById(id);
    }

    public Page<T> list(Pageable pageable) {
        return this.getRepository().findAll(pageable);
    }

    public Page<ResourceUriProjection> listProjections(Pageable pageable) {
        return this.getRepository().findAllProjectedBy(pageable);
    }

    public int count() {
        return (int) this.getRepository().count();
    }

    protected Supplier<ResourceNotFoundException> resourceNotFound(String resource, String key) {
        return () -> {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(resource, key);
            log.error("error", resourceNotFoundException);
            return resourceNotFoundException;
        };
    }
}
