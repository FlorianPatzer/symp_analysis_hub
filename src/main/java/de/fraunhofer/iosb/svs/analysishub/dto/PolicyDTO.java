package de.fraunhofer.iosb.svs.analysishub.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * A DataTransferObject for a {@link de.fraunhofer.iosb.svs.analysishub.data.entity.Policy}.
 */
// TODO remove?
@Value
public class PolicyDTO {
    String uri;
    String localName;
    String modelLink;
    String description;
    LocalDateTime lastChanged;
}
