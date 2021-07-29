package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * An analytic query in the policy-based-analysis ontology. Has no special properties.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "AnalyticQuery")
public class AnalyticQuery extends Query {
    public AnalyticQuery(String namespace, String localName, String query, List<Implementation> implementations, List<OntologyDependency> ontologyDependencies) {
        super(namespace, localName, query, implementations, ontologyDependencies);
    }
}
