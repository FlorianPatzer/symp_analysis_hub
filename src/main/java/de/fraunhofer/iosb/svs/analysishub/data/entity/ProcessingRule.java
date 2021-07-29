package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.RULE_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A processing rule in the policy-based-analysis ontology. Processing rules have a rule property.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "ProcessingRule")
public abstract class ProcessingRule extends Implementation {
    @Property(name = RULE_LABEL)
    private String rule;

    /**
     * Constructor for a processing rule.
     *
     * @param rule the rule that is to be executed
     */
    public ProcessingRule(String namespace, String localName, String description, Phase phase, Topic topic, List<Implementation> dependsOnImplementation, List<OntologyDependency> dependsOnOntologyDependency, String rule) {
        super(namespace, localName, description, phase, topic, dependsOnImplementation, dependsOnOntologyDependency);
        this.rule = rule;
    }

    /**
     * Empty constructor.
     */
    public ProcessingRule() {
        super();
    }
}
