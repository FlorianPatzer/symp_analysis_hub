package de.fraunhofer.iosb.svs.analysishub.data.service;

import de.fraunhofer.iosb.svs.analysishub.URIUtil;
import de.fraunhofer.iosb.svs.analysishub.data.entity.Phase;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.POLICY_BASED_ANALYSIS_NAMESPACE;

/**
 * A service handling the phase nodes.
 * <p>
 * Phases are the already 6 set phases plus one phase for "any phase". Therefore no phase generation is needed.
 * The given phases are represented as the {@link StaticPhase} enum.
 */
@Service
public class PhaseService {
    private final PhaseRepository phaseRepository;

    @Autowired
    public PhaseService(PhaseRepository phaseRepository) {
        this.phaseRepository = phaseRepository;
    }

    public List<Phase> getPhases() {
        return phaseRepository.findAll();
    }

    /**
     * Given a static phase return the real phase node.
     */
    public Phase getPhaseByStaticPhase(StaticPhase staticPhase) {
        return phaseRepository.findByUri(URIUtil.assemble(POLICY_BASED_ANALYSIS_NAMESPACE, staticPhase.getLocalName()))
                .orElseThrow(() -> new ResourceNotFoundException("Phase not found. This should not be possible"));
    }

    /**
     * Given a real phase node get the static phase.
     */
    public StaticPhase getStaticPhaseByPhase(Phase phase) {
        if (phase != null) {
            return Arrays.stream(StaticPhase.values()).filter(staticPhase -> phase.getLocalName().equals(staticPhase.getLocalName()))
                    .findAny()
                    .orElseThrow(() -> new ResourceNotFoundException("Phase not found. This should not be possible"));
        }
        return null;
    }

    public Phase getPhaseByUri(String uri) {
        return phaseRepository.findByUri(uri).orElseThrow(() -> new ResourceNotFoundException("Phase not found"));
    }

    /**
     * An enum containing the local names and uris of all 7 phases.
     */
    public enum StaticPhase {
        KNOWLEDGE_COLLECTION("KnowledgeCollection"),
        KNOWLEDGE_FUSION("KnowledgeFusion"),
        MODEL_CLEANING("ModelCleaning"),
        STATIC_KNOWLEDGE_EXTENSION("StaticKnowledgeExtension"),
        DYNAMIC_KNOWLEDGE_EXTENSION("DynamicKnowledgeExtension"),
        ANALYSIS("Analysis"),
        ANY_PHASE("AnyPhase");

        private final String uri;
        private final String localName;

        StaticPhase(String localName) {
            this.localName = localName;
            this.uri = POLICY_BASED_ANALYSIS_NAMESPACE + localName;
        }

        public String getUri() {
            return uri;
        }

        public String getLocalName() {
            return localName;
        }

    }

}
