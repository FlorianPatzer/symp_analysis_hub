package de.fraunhofer.iosb.svs.analysishub;

import org.neo4j.driver.exceptions.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.POLICY_BASED_ANALYSIS_NAMESPACE;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.POLICY_BASED_ANALYSIS_PREFIX;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class AnalysisHub {// if war --> extends SpringBootServletInitializer {
    private static final Logger log = LoggerFactory.getLogger(AnalysisHub.class);
    private final Neo4jClient neo4jClient;
    private final ResourceLoader resourceLoader;

    @Autowired
    public AnalysisHub(Neo4jClient neo4jClient, ResourceLoader resourceLoader) {
        this.neo4jClient = neo4jClient;
        this.resourceLoader = resourceLoader;
    }

    public static void main(String[] args) {
        SpringApplication.run(AnalysisHub.class, args);
    }
    
    /**
     * @return 
     * 0 - database is connected
     * 1 - database is still booting
     * 2 - database is not responding
     */
    private int testDatabaseConnection(String database) throws InterruptedException {
        int status = 0;

        try {
            log.debug("Connecting to database {}", database);
            neo4jClient.query("Match () Return 1 Limit 1").in(database).run();
        } catch (Exception e) {
            Throwable exceptionCause = e.getCause();
            if (exceptionCause instanceof ServiceUnavailableException) {
                status = 1;
            } else if (exceptionCause instanceof InvocationTargetException) {
                status = 2;
            } else {
                throw e;
            }
        }

        return status;
    }
    
    
    private void handleDatabaseConnection(String database, int retryCount, int timeoutMillis) throws Exception {
        int status = testDatabaseConnection(database);

        switch (status) {
        case 0:
            log.debug("Database connected");
            break;
        case 1:
            Thread.sleep(timeoutMillis);
            log.debug("Database wasn't done with boot procedure. Retrying.");
            handleDatabaseConnection(database, retryCount, timeoutMillis);
        case 2:
            for (int i = 0; i < retryCount; i++) {
                Thread.sleep(timeoutMillis);
                log.debug("Database host is not responding. Retry {} of {}", i + 1, retryCount);
                if (testDatabaseConnection(database) == 0) {
                    log.debug("Connection to database established");
                    return;
                }
            }
            log.debug("Couldn't establish connection with the database host");
            throw new Exception("Database connection failed");
        }
    }

    @EventListener(ApplicationStartedEvent.class)
    public void runAfterStart() throws Exception {
        log.debug("Setup database");


        String database = "neo4j";
        int retryCount = 10;
        int timeoutMillis = 5000;
        handleDatabaseConnection(database, retryCount, timeoutMillis);

        // Need a unique constraint on uri (neosemantics requires that)
        Optional<String> response = neo4jClient
                .query("CREATE CONSTRAINT n10s_unique_uri  IF NOT EXISTS ON (r:Resource) ASSERT r.uri IS UNIQUE;")
                .in(database).fetchAs(String.class).one();
        response.ifPresent(res -> log.debug("Response: {}", res));

        // check if a graph config exists. Otherwise create one
        checkAndAddGraphConfig(database);

        // check if prefix is defined for namespace policy based analysis
        checkAndAddPrefix(database);

        // load ontology if not exists
        loadPolicyBasedAnalysisOntology(database);

    }

    /**
     * Check if a graph config is present as required by neosemantics. If not init one.
     *
     * @param database
     */
    private void checkAndAddGraphConfig(String database) {
        Optional<Integer> graphConfigOptional = neo4jClient.query("MATCH (gc:`_GraphConfig`) RETURN count(*)")
                .in(database).fetchAs(Integer.class).one();
        if (graphConfigOptional.isPresent()) {
            Integer graphConfig = graphConfigOptional.get();
            if (graphConfig == 0) {
                // no graph config exists
                log.debug("Create Graph config");
                neo4jClient.query("CALL n10s.graphconfig.init()").in(database).run();
            } else {
                log.debug("Graph config exists");
            }
        } else {
            log.error("Check for graph config returned null");
        }
    }

    /**
     * Check if prefix for the policy-based-analysis ontology is given and if not add it.
     *
     * @param database
     */
    private void checkAndAddPrefix(String database) {
        Collection<Map<String, Object>> prefixes = neo4jClient.query("CALL n10s.nsprefixes.list()").in(database).fetch()
                .all();
        if (prefixes.stream().noneMatch(map -> map.get("prefix").equals(POLICY_BASED_ANALYSIS_PREFIX)
                && map.get("namespace").equals(POLICY_BASED_ANALYSIS_NAMESPACE))) {
            log.debug("Adding prefix and namespace for: {}: {}", POLICY_BASED_ANALYSIS_PREFIX,
                    POLICY_BASED_ANALYSIS_NAMESPACE);
            neo4jClient.query("CALL n10s.nsprefixes.add(\"" + POLICY_BASED_ANALYSIS_PREFIX + "\", \""
                    + POLICY_BASED_ANALYSIS_NAMESPACE + "\");").in(database).run();
        } else {
            log.debug("Prefix and namespace found: {}: {}", POLICY_BASED_ANALYSIS_PREFIX,
                    POLICY_BASED_ANALYSIS_NAMESPACE);
        }
    }

    /**
     * If the policy-based-analysis ontology is not yet in the neo4j database it will added.
     *
     * @param database
     */
    private void loadPolicyBasedAnalysisOntology(String database) {
        Optional<Integer> owlClass = neo4jClient.query("MATCH (n:owl__Class) return count(*)").in(database)
                .fetchAs(Integer.class).one();
        if (owlClass.isPresent()) {
            if (owlClass.get() < 1) {
                log.debug("Set up ontology");
                String policyBasedAnalysis = ResourceUtil
                        .asString(resourceLoader.getResource("classpath:ontologies/policy-based-analysis.owl"));
                neo4jClient.query("CALL n10s.rdf.import.inline('" + policyBasedAnalysis + "', \"RDF/XML\");")
                        .in(database).run();
            } else {
                log.debug("Ontology already set up");
            }
        } else {
            log.error("Check for graph config returned null");
        }
    }
}
