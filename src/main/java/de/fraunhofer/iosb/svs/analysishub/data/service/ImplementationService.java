package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service for handling implementation nodes.
 */
@Service
public class ImplementationService extends ResourceService<Implementation> {
    private static final String IMPLEMENTATION = "implementation";

    private final ImplementationRepository repository;

    @Autowired
    public ImplementationService(ImplementationRepository repository) {
        this.repository = repository;
    }

    protected ImplementationRepository getRepository() {
        return repository;
    }
}
