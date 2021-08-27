package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.IMAGE_NAME_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.IMAGE_EXTERNAL_SOURCE;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.IMAGE_TAG;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A processing module in the policy-based-analysis ontology. Processing module have a image name of a docker image.
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "ProcessingModule")
public class ProcessingModule extends Implementation {
    @Property(name = IMAGE_EXTERNAL_SOURCE)
    private String imageExternalSource;
    
    @Property(name = IMAGE_NAME_LABEL)
    private String imageName;
    
    @Property(name = IMAGE_TAG)
    private String imageTag;
    
   
    /**
     * Constructor for a processing module.
     *
     * @param imageName the name of the docker image for the worker of this processing module
     */
    public ProcessingModule(String namespace, String localName, String description, Phase phase, Topic topic, List<Implementation> dependsOnImplementation, List<OntologyDependency> dependsOnOntologyDependency, String imageExternalSource, String imageName, String imageTag) {
        super(namespace, localName, description, phase, topic, dependsOnImplementation, dependsOnOntologyDependency);
        this.imageExternalSource = imageExternalSource;
        this.imageName = imageName;
        this.imageTag = imageTag;
    }

    /**
     * Empty constructor.
     */
    public ProcessingModule() {
        super();
    }
}
