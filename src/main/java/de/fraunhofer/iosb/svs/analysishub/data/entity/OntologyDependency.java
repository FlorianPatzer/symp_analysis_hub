package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.DataProperties.*;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.ObjectProperties.DEPENDS_ON_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependencyFile.HAS_FILE_LABEL;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.NODE_PREFIX;

/**
 * An ontology dependency in the policy-based-analysis ontology.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Node(primaryLabel = NODE_PREFIX + "OntologyDependency")
public class OntologyDependency extends NamedIndividual {
    @Property(name = PREFIX_LABEL)
    private String prefix;

    @Property(name = URI_LABEL)
    private String ontologyUri;

    @Property(name = DOWNLOAD_LINK_LABEL)
    private String downloadLink;

    @Relationship(type = HAS_FILE_LABEL, direction = Relationship.Direction.OUTGOING)
    private OntologyDependencyFile file;

    /**
     * A set of {@link OntologyDependency} this ontology dependency has a dependsOn relationship with
     */
    @Relationship(type = DEPENDS_ON_LABEL, direction = Relationship.Direction.OUTGOING)
    private Set<OntologyDependency> dependsOnOntologies;

    /**
     * Constructor for an ontology dependency.
     *
     * @param namespace           the namespace the ontology dependency should be in
     * @param localName           the local name within the namespace for the ontology dependency
     * @param prefix              the prefix used for the ontology
     * @param ontologyUri         the uri of the ontology
     * @param downloadLink        a link where this ontology dependency can be reached from the API
     * @param file                the class containing the actual ontology file
     * @param dependsOnOntologies a set of ontology dependencies this one depends on
     */
    public OntologyDependency(String namespace, String localName, String prefix, String ontologyUri, String downloadLink, OntologyDependencyFile file, Set<OntologyDependency> dependsOnOntologies) {
        super(namespace, localName);
        this.prefix = prefix;
        this.ontologyUri = ontologyUri;
        this.downloadLink = downloadLink;
        this.file = file;
        this.dependsOnOntologies = dependsOnOntologies;
    }

    /**
     * Empty constructor initializing an empty set.
     */
    public OntologyDependency() {
        super();
        this.dependsOnOntologies = new HashSet<>();
    }
}
