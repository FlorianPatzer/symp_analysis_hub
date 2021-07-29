package de.fraunhofer.iosb.svs.analysishub.data.entity.projections;

import de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependencyFile;

import lombok.Value;

/**
 * Projection for {@link de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependency} only containing the {@link OntologyDependencyFile}.
 */
@Value
public class OntologyDependencyFileProjection {
    OntologyDependencyFile file;
}
