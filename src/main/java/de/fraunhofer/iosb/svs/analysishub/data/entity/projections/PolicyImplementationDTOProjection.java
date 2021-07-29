package de.fraunhofer.iosb.svs.analysishub.data.entity.projections;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Projection for {@link de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyImplementation} containing uri and description.
 */
@Value
public class PolicyImplementationDTOProjection {
    String uri;
    String description;
    LocalDateTime lastChanged;

    public String getLocalName() {
        return URIUtil.getLocalName(uri);
    }
}
