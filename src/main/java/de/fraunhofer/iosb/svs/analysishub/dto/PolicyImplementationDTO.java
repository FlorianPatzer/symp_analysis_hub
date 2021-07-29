package de.fraunhofer.iosb.svs.analysishub.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * A DataTransferObject for a {@link de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyImplementation}.
 * Contains uri, name, the generated link to the API where the model for the policy implementation can be found and a description.
 */
@Value
public class PolicyImplementationDTO {
    String uri;
    String localName;
    String modelLink;
    String description;
    LocalDateTime lastChanged;
}
