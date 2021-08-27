package de.fraunhofer.iosb.svs.analysishub.data;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_URI;

/**
 * A service that fetches rdf data for a policy implementation from neo4j database.
 */
@Service
public class RDFClient {
    private static final Logger log = LoggerFactory.getLogger(RDFClient.class);

    private final ObjectMapper objectMapper;

    private final WebClient webClient;

    @Autowired
    public RDFClient(ObjectMapper objectMapper, @Value("${neo4j.rdf.endpoint}") String rdfEndpoint,
                     @Value("${spring.neo4j.authentication.username}") String neo4jUsername,
                     @Value("${spring.neo4j.authentication.password}") String neo4jPassword) {
        this.objectMapper = objectMapper;
        log.debug("RDF Cypher endpoint: {}", rdfEndpoint);
        this.webClient = WebClient.builder()
                .baseUrl(rdfEndpoint)
                .defaultHeaders(headers -> headers.setBasicAuth(neo4jUsername, neo4jPassword))
                .build();
    }

    /**
     * Pull ontology model for a policy from neosemantics rdf endpoint using custom cypher query.
     * <p>
     * Enriches the model with an ontology tag and and import statement for the policy analysis ontology.
     *
     * @param policyImplementationUri
     * @return
     */
    public ByteArrayResource getPolicyOntology(String policyImplementationUri) {
        log.debug("Getting policy rdf data from neosemantics for policy implementation '{}'", policyImplementationUri);
        DataBuffer dataBuffer = webClient.post()
                .accept(MediaType.parseMediaType("application/rdf+xml"))
                .bodyValue(new CypherBody(policyImplementationUri))
                .retrieve()
                .bodyToMono(DataBuffer.class)
                .block();
        try {
            if (dataBuffer != null) {
                return enrichOntModel(policyImplementationUri, dataBuffer);
            } else {
                throw new RuntimeException("No result from rdf endpoint");
            }
        } catch (IOException e) {
            log.error("Error reading result from post to rdf endpoint", e);
            throw new RuntimeException("No result from rdf endpoint", e);
        }
    }

    private ByteArrayResource enrichOntModel(String policyImplementationUri, DataBuffer dataBuffer) throws IOException {
        // TODO the connection to the policy based ontology should be done in the database...
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        ontModel.read(dataBuffer.asInputStream(), "RDF/XML");
        Ontology ontology = ontModel.createOntology(KNOWLEDGE_BASE_URI + "/" + URIUtil.getLocalName(policyImplementationUri) + "_Ontology");
        ontology.addImport(ontModel.createResource("https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis/0.1.0"));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //ontModel.write(byteArrayOutputStream, "RDF/XML", TEST_BASE);
        RDFWriter.create()
                .source(ontModel)
                //.base(TEST_BASE)
                .format(RDFFormat.RDFXML)
                .output(byteArrayOutputStream);
        // TODO setting base uri does noting...
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    @Getter
    public class CypherBody {
        // cypher query that matches a policy implementation by an uri.
        // Also matches the policy that is analyzed.
        // Also matches the query that is contained. Beginning from the query all implementations and the relationships are matched
        // For all implementations are then the topic matched.
        // For all implementations are also the phases matched.
        private final String cypher = "MATCH (x:pba__PolicyImplementation {uri: $policyImplementationUri}) " +
                "OPTIONAL MATCH (policy)-[r1:pba__analyzedBy]->(x) " +
                "OPTIONAL MATCH p = (x)-[:pba__contains]->(:pba__Query)-[:pba__dependsOn*0..]->(impls) With *, relationships(p) as r3 " +
                "OPTIONAL MATCH (impls)-[r4:pba__hasTopic]->(topic) " +
                "OPTIONAL MATCH (impls)-[r6:pba__isIn]->(phase) RETURN *";
        private final String format = "RDF/XML";
        private final boolean showOnlyMapped = true;
        private final Map<String, String> cypherParams;

        public CypherBody(String policyImplementationUri) {
            cypherParams = new HashMap<>();
            cypherParams.put("policyImplementationUri", policyImplementationUri);
        }
    }
}
