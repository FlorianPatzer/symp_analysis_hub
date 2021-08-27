package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Topic;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_NAMESPACE;
import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.POLICY_BASED_ANALYSIS_NAMESPACE;

/**
 * A service for handling topic nodes.
 * The SwrlTopic and JenaTopic do not need to be generated as they are part of the policy-based-analysis ontology.
 */
@Service
public class TopicService extends ResourceService<Topic> {
    public static final String TOPIC = "topic";
    public static final String SWRL_TOPIC_URI = POLICY_BASED_ANALYSIS_NAMESPACE + "SwrlTopic";
    public static final String JENA_TOPIC_URI = POLICY_BASED_ANALYSIS_NAMESPACE + "JenaTopic";
    private static final Logger log = LoggerFactory.getLogger(TopicService.class);
    private final TopicRepository repository;

    @Autowired
    public TopicService(TopicRepository repository) {
        this.repository = repository;
    }

    protected TopicRepository getRepository() {
        return repository;
    }

    public Topic getTopicByUri(String uri) {
        return repository.findByUri(uri).orElseThrow(resourceNotFound(TOPIC, uri));
    }

    /**
     * Fetches a topic from the repository.
     * If none was found a new one is created.
     */
    public Topic getTopicByNameOrElseCreate(String name) {
        // TODO do better (ex. add extra view to add topic and then fetch)
        return repository.findByName(name).orElseGet(() -> {
            // replacing whitespace characters with an underscore
            // TODO as the topic is also in the implementation of a processing module the topic should not be modified here.
            // TODO as whitespaces are not allowed, it should throw an error in the view
            Topic topic = new Topic(KNOWLEDGE_BASE_NAMESPACE,
                    name.replaceAll("\\s", "_"),
                    "Auto generated description",
                    name);
            try {
                return update(topic);
            } catch (ResourceAlreadyExistsException raae) {
                log.error("Topic with local name '{}' already exists, but no topic with property name '{}'",
                        topic.getLocalName(),
                        topic.getName());
                throw raae;
            }
        });
    }
}
