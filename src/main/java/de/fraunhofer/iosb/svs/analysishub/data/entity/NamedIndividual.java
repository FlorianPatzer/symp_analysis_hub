package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * All named individuals will have the owl NamedIndividual tag.
 */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node("owl__NamedIndividual")
public abstract class NamedIndividual extends Resource {
    public static final String OWL_NAMED_INDIVIDUAL = "owl__NamedIndividual";

    public NamedIndividual(String uri) {
        super(uri);
    }

    public NamedIndividual(String namespace, String localName) {
        super(namespace, localName);
    }

}
