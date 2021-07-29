package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.DESCRIPTION_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.ObjectProperties.ANALYZED_BY_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.ObjectProperties.CONTAINS_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A policy implementation in the policy-based-analysis ontology. Policy implementations contain a {@link Query} and connect to a {@link Policy}
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "PolicyImplementation")
public class PolicyImplementation extends NamedIndividual {

    @Property(name = DESCRIPTION_LABEL)
    private String description;

    @Relationship(type = CONTAINS_LABEL, direction = Relationship.Direction.OUTGOING)
    private Query query;

    // Need to exclude from equals and hash code as this is a cyclic reference
    // (the policy has a list of policy implementations containing this one)
    @EqualsAndHashCode.Exclude
    @Relationship(type = ANALYZED_BY_LABEL, direction = Relationship.Direction.INCOMING)
    private Policy policy;

    public PolicyImplementation(String uri, String description, Query query) {
        super(uri);
        this.description = description;
        this.query = query;
    }

    /**
     * Constructor for a policy implementation.
     *
     * @param namespace   the namespace the policy implementation should be in
     * @param localName   the local name within the namespace for the policy implementation
     * @param description a description what the policy implementation does
     * @param query       the query this policy implementation contains
     */
    public PolicyImplementation(String namespace, String localName, String description, Query query) {
        super(namespace, localName);
        this.description = description;
        this.query = query;
    }

    /**
     * Empty constructor
     */
    public PolicyImplementation() {
    }

}
