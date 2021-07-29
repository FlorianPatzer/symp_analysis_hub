package de.fraunhofer.iosb.svs.analysishub.data.filesystem;

import de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependencyFile;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface OntologyDependencyFileRepository extends Neo4jRepository<OntologyDependencyFile, Long> {
}
