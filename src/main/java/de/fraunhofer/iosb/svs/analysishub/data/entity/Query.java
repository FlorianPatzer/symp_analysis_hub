package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.QUERY_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.ObjectProperties.DEPENDS_ON_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * A query in the policy-based-analysis ontology.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "Query")
public abstract class Query extends NamedIndividual {
    @Property(name = QUERY_LABEL)
    private String query;

    /**
     * A list of {@link Implementation} this query has a dependsOn relationship with.
     */
    @Relationship(type = DEPENDS_ON_LABEL, direction = Relationship.Direction.OUTGOING)
    private List<Implementation> implementations;

    /**
     * A list of {@link OntologyDependency} this query has a dependsOn relationship with.
     */
    @Relationship(type = DEPENDS_ON_LABEL, direction = Relationship.Direction.OUTGOING)
    private List<OntologyDependency> ontologyDependencies;

    /**
     * Constructor for a query.
     *
     * @param namespace            the namespace the query should be in
     * @param localName            the local name within the namespace for the query
     * @param query                the actual query
     * @param implementations      a list of implementations this query depends on
     * @param ontologyDependencies a list of ontology dependencies this query depends on
     */
    public Query(String namespace, String localName, String query, List<Implementation> implementations, List<OntologyDependency> ontologyDependencies) {
        super(namespace, localName);
        this.query = query;
        this.implementations = implementations;
        this.ontologyDependencies = ontologyDependencies;
    }

    /**
     * Empty constructor initializing empty lists.
     */
    public Query() {
        super();
        this.implementations = new ArrayList<>();
        this.ontologyDependencies = new ArrayList<>();
    }
}
