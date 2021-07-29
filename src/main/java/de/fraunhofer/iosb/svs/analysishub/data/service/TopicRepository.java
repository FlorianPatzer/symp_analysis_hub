package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Topic;

import java.util.Optional;

public interface TopicRepository extends ResourceRepository<Topic> {
    Optional<Topic> findByName(String name);
}
