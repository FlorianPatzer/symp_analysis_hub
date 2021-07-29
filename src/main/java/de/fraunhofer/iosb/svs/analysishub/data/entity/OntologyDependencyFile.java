package de.fraunhofer.iosb.svs.analysishub.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * A node for a ontology dependency file containing the location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("OntologyDependencyFile")
public class OntologyDependencyFile {
    public static final String HAS_FILE_LABEL = "hasFile";

    @Id
    @GeneratedValue
    private Long id;

    @Property(name = "location")
    private String location;

    public OntologyDependencyFile(String location) {
        this.location = location;
    }
}
