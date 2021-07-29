package de.fraunhofer.iosb.svs.analysishub.data.entity;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_NAMESPACE;

/**
 * A resource given by the neosemantics library.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("Resource")
public abstract class Resource {
    private static final Logger log = LoggerFactory.getLogger(Resource.class);

    @Id
    @GeneratedValue
    private Long id;

    @Property("uri")
    private String uri;

    public Resource(String uri) {
        this.uri = uri;
    }

    public Resource(String namespace, String localName) {
        setUri(namespace, localName);
    }

    public String getNamespace() {
        int localNameIdx = URIUtil.getLocalNameIndex(getUri());
        return getUri().substring(0, localNameIdx);
    }

    public String getLocalName() {
        String uri = getUri();
        if (uri == null) {
            return null;
        }
        int localNameIdx = URIUtil.getLocalNameIndex(uri);
        return getUri().substring(localNameIdx);
    }

    /**
     * Sets the uri by combining the namespace with the local name
     */
    public void setUri(String namespace, String localName) {
        this.setUri(namespace + localName);
    }

    /**
     * Sets the uri with the given knowledge base namespace
     */
    public void setUriByLocalName(String localName) {
        this.setUri(KNOWLEDGE_BASE_NAMESPACE + localName);
    }
}
