package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.JenaRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.fraunhofer.iosb.svs.analysishub.data.service.TopicService.JENA_TOPIC_URI;

/**
 * A service for handling jena rule nodes.
 */
@Service
public class JenaRuleService extends ResourceService<JenaRule> {
    private final JenaRuleRepository repository;
    private final TopicService topicService;

    @Autowired
    public JenaRuleService(JenaRuleRepository repository, TopicService topicService) {
        this.repository = repository;
        this.topicService = topicService;
    }

    protected JenaRuleRepository getRepository() {
        return repository;
    }

    /**
     * Saves a jena rule. Inserts the jena topic.
     */
    @Override
    public JenaRule update(JenaRule jenaRule) {
        jenaRule.setTopic(topicService.getTopicByUri(JENA_TOPIC_URI));
        return super.update(jenaRule);
    }
}
