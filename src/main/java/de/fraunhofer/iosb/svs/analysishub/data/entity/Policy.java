package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.DESCRIPTION_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.ObjectProperties.ANALYZED_BY_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A policy in the policy-based-analysis ontology.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "Policy")
public class Policy extends NamedIndividual {
    private static final String POLICY_LABEL = NODE_PREFIX + "Policy";

    /* non ontology properties do not work here, as they are also exported as rdf data
     * Workaround: Create new class with node label policyProperties and add a relationship
     * Because the cypher query wont reach that new node, it will not be in the exported rdf data.
     * For now we dont need these properties.
     * */
    /*@LastModifiedDate
    private LocalDateTime lastChanged;

    @Property("_owner")
    private String owner;*/

    @Property(name = DESCRIPTION_LABEL)
    private String description;

    /**
     * A list of {@link PolicyImplementation} this policy is analyzed by.
     */
    @Relationship(type = ANALYZED_BY_LABEL, direction = Relationship.Direction.OUTGOING)
    private List<PolicyImplementation> policyImplementations;

    public Policy(String uri, String description, List<PolicyImplementation> policyImplementations) {
        super(uri);
        this.description = description;
        this.policyImplementations = policyImplementations;
    }

    /**
     * Constructor for a policy.
     *
     * @param namespace             the namespace the policy should be in
     * @param localName             the local name within the namespace for the policy
     * @param description           a description what the policy requires
     * @param policyImplementations a list of policy implementations this policy is analyzed by
     */
    public Policy(String namespace, String localName, String description, List<PolicyImplementation> policyImplementations) {
        super(namespace, localName);
        this.description = description;
        this.policyImplementations = policyImplementations;
    }

    /**
     * Empty constructor initializing an empty list.
     */
    public Policy() {
        this.policyImplementations = new ArrayList<>();
    }
}
