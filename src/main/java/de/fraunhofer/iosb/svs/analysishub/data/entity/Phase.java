package de.fraunhofer.iosb.svs.analysishub.data.entity;


import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Node;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A phase in the policy-based-analysis ontology. Phases do not need to be generated as they are part of the policy-based-analysis ontology.
 */
@Getter
@Setter
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "Phase")
public class Phase extends NamedIndividual {
    private static final String PHASE_LABEL = NODE_PREFIX + "Phase";
    private static final Logger log = LoggerFactory.getLogger(Phase.class);

    public Phase(String uri) {
        super(uri);
    }
}
