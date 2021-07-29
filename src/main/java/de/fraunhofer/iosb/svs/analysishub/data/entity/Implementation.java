package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.DESCRIPTION_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.ObjectProperties.*;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * An implementation in the policy-based-analysis ontology. Implementations need a {@link Topic} and a {@link Phase}. They can also have dependsOn relationships.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "Implementation")
public abstract class Implementation extends NamedIndividual {
    private static final Logger log = LoggerFactory.getLogger(Implementation.class);
    private static final String IMPLEMENTATION_LABEL = NODE_PREFIX + "Implementation";

    @Property(name = DESCRIPTION_LABEL)
    private String description;

    @Relationship(type = IS_IN_LABEL, direction = Relationship.Direction.OUTGOING)
    private Phase phase;

    @Relationship(type = HAS_TOPIC_LABEL, direction = Relationship.Direction.OUTGOING)
    private Topic topic;

    /**
     * A list of {@link Implementation} this implementation has a dependsOn relationship with.
     */
    @Relationship(type = DEPENDS_ON_LABEL, direction = Relationship.Direction.OUTGOING)
    private List<Implementation> dependsOnImplementation;

    /**
     * A list of {@link OntologyDependency} this implementation has a dependsOn relationship with.
     */
    @Relationship(type = DEPENDS_ON_LABEL, direction = Relationship.Direction.OUTGOING)
    private List<OntologyDependency> dependsOnOntologyDependency;

    /**
     * Constructor for an implementation.
     *
     * @param namespace                   the namespace the implementation should be in
     * @param localName                   the local name within the namespace for the implementation
     * @param description                 a description what the implementation does
     * @param phase                       the phase the implementation is placed in
     * @param topic                       the topic on which the implementation executes on
     * @param dependsOnImplementation     a list of implementations this one depends on
     * @param dependsOnOntologyDependency a list of ontology dependencies this implementation depends on
     */
    public Implementation(String namespace, String localName, String description, Phase phase, Topic topic, List<Implementation> dependsOnImplementation, List<OntologyDependency> dependsOnOntologyDependency) {
        super(namespace, localName);
        this.description = description;
        this.phase = phase;
        this.topic = topic;
        this.dependsOnImplementation = dependsOnImplementation;
        this.dependsOnOntologyDependency = dependsOnOntologyDependency;
    }

    /**
     * Empty constructor initializing empty lists.
     */
    public Implementation() {
        super();
        this.dependsOnImplementation = new ArrayList<>();
        this.dependsOnOntologyDependency = new ArrayList<>();
    }
}
