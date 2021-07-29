package de.fraunhofer.iosb.svs.analysishub.data.entity.projections;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;

import lombok.Value;

/**
 * Projection for arbitrary subclasses of {@link de.fraunhofer.iosb.svs.analysishub.data.entity.Resource} containing just the uri.
 */
@Value
public class ResourceUriProjection {
    Long id;
    String uri;

    public String getLocalName() {
        return URIUtil.getLocalName(uri);
    }
}
