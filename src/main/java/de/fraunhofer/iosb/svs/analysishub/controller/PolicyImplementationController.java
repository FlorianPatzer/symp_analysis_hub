package de.fraunhofer.iosb.svs.analysishub.controller;

import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyImplementationService;
import de.fraunhofer.iosb.svs.analysishub.dto.PolicyImplementationDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestApiV1Controller
public class PolicyImplementationController {
    private static final Logger log = LoggerFactory.getLogger(PolicyImplementationController.class);
    private final PolicyImplementationService policyImplementationService;
    
    @GetMapping(path = "/")
    public String defaultPath() {
        return "AH reached";
    }

    @Autowired
    public PolicyImplementationController(PolicyImplementationService policyImplementationService) {
        this.policyImplementationService = policyImplementationService;
    }

    /**
     * Gets the meta information about a policy implementation by its unique name.
     *
     * @param policyImplementationName the unique name
     * @return
     */
    @GetMapping(path = "/policyimplementation/{policyImplementationName}", produces = "application/json")
    public ResponseEntity<PolicyImplementationDTO> getPolicyImplementationById(@PathVariable("policyImplementationName") String policyImplementationName) {
        log.debug("API request for policy implementation '{}'", policyImplementationName);
        return ResponseEntity.ok(policyImplementationService.getPolicyImplementationDTOByName(policyImplementationName));
    }

    /**
     * Gets the model of a policy implementation by its unique name.
     *
     * @param policyImplementationName the unique name
     * @return
     */
    @GetMapping(path = "/policyimplementation/{policyImplementationName}/model", produces = "application/octet-stream")
    public ResponseEntity<ByteArrayResource> getPolicyModelById(@PathVariable("policyImplementationName") String policyImplementationName) {
        log.debug("API request for the model of the policy implementation: '{}'", policyImplementationName);
        ByteArrayResource resource = policyImplementationService.getPolicyModelByName(policyImplementationName);
        return ResponseEntity.ok().contentLength(resource.contentLength()).body(resource);
    }
}
