package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.SwrlRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.fraunhofer.iosb.svs.analysishub.data.service.TopicService.SWRL_TOPIC_URI;

/**
 * A service for handling swrl rule nodes.
 */
@Service
public class SwrlRuleService extends ResourceService<SwrlRule> {
    private final SwrlRuleRepository repository;
    private final TopicService topicService;

    @Autowired
    public SwrlRuleService(SwrlRuleRepository repository, TopicService topicService) {
        this.repository = repository;
        this.topicService = topicService;
    }

    protected SwrlRuleRepository getRepository() {
        return repository;
    }

    /**
     * Saves a swrl rule. Inserts the swrl topic.
     */
    @Override
    public SwrlRule update(SwrlRule swrlRule) {
        swrlRule.setTopic(topicService.getTopicByUri(SWRL_TOPIC_URI));
        return super.update(swrlRule);
    }
}
