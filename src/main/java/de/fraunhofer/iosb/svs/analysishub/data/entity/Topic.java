package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.DESCRIPTION_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.NAME_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A topic in the policy-based-analysis ontology.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "Topic")
public class Topic extends NamedIndividual {

    @Property(name = DESCRIPTION_LABEL)
    private String description;

    @Property(name = NAME_LABEL)
    private String name;

    /**
     * @param namespace   the namespace the topic should be in
     * @param localName   the local name within the namespace for the topic
     * @param description a description what the topic is for
     * @param name        the name of the topic
     */
    public Topic(String namespace, String localName, String description, String name) {
        super(namespace, localName);
        this.description = description;
        this.name = name;
    }
}
