package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A swrl rule in the policy-based-analysis ontology.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "SwrlRule")
public class SwrlRule extends ProcessingRule {
    public SwrlRule(String namespace, String localName, String description, Phase phase, Topic topic, List<Implementation> dependsOnImplementation, List<OntologyDependency> dependsOnOntologyDependency, String rule) {
        super(namespace, localName, description, phase, topic, dependsOnImplementation, dependsOnOntologyDependency, rule);
    }

    public SwrlRule() {
        super();
    }
}
