package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.ProcessingModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service for handling processing modules.
 */
@Service
public class ProcessingModuleService {

    private final ProcessingModuleRepository processingModuleRepository;

    @Autowired
    public ProcessingModuleService(ProcessingModuleRepository processingModuleRepository) {
        this.processingModuleRepository = processingModuleRepository;
    }

    public List<ProcessingModule> getProcessingModules() {
        return processingModuleRepository.findAll();
    }
}
