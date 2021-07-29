package de.fraunhofer.iosb.svs.analysishub.data.generator;

import de.fraunhofer.iosb.svs.analysishub.data.entity.*;
import de.fraunhofer.iosb.svs.analysishub.data.filesystem.OntologyDependencyFileService;
import de.fraunhofer.iosb.svs.analysishub.data.service.*;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.*;

import static de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyBasedAnalysis.KNOWLEDGE_BASE_NAMESPACE;
import static de.fraunhofer.iosb.svs.analysishub.data.service.TopicService.JENA_TOPIC_URI;
import static de.fraunhofer.iosb.svs.analysishub.data.service.TopicService.SWRL_TOPIC_URI;
/**
 * This class introduces some example policies and needs to be deactivated or adapted when used outside the Fraunhofer IOSB lab environment.
 *
 */
@SpringComponent
public class DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private OntologyDependencyFileService ontologyDependencyFileService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public CommandLineRunner loadData(PolicyRepository policyRepository,
            ImplementationRepository implementationRepository, QueryRepository queryRepository,
            OntologyDependencyRepository ontDepRepository) {
        return args -> {
            log.info("Getphases");
            phaseService.getPhases().forEach(phase -> log.info(phase.toString()));

            String location1 = ontologyDependencyFileService.saveOntologyDependencyFile(resourceLoader
                    .getResource("classpath:ontologydependencies/lab-62443.owl").getInputStream().readAllBytes(),
                    "lab-62443.owl");
            OntologyDependencyFile ontologyDependencyFile1 = new OntologyDependencyFile(location1);
            String downloadLink1 = "http://symp-ah:8545/api/v1/ontology-dependency/Lab_62443_OntologyDependency";

            OntologyDependency ontDepLab62443 = new OntologyDependency(KNOWLEDGE_BASE_NAMESPACE,
                    "Lab_62443_OntologyDependency", "lab-62443", "http://iosb.fraunhoferde/security/lab-62443",
                    downloadLink1, ontologyDependencyFile1, new HashSet<>());
            
            if (!ontDepRepository.existsByUri(ontDepLab62443.getUri())) {
                ontDepRepository.save(ontDepLab62443);
            }
            else {
                log.debug("Ontology dependency with uri {} already exists. Skip adding", ontDepLab62443.getUri());
            }

            String location2 = ontologyDependencyFileService.saveOntologyDependencyFile(
                    resourceLoader.getResource("classpath:ontologydependencies/ics-attAck_iec62443_mapping.owl")
                            .getInputStream().readAllBytes(),
                    "attAck-62443.owl");
            OntologyDependencyFile ontologyDependencyFile2 = new OntologyDependencyFile(location2);
            String downloadLink2 = "http://symp-ah:8545/api/v1/ontology-dependency/AttAck_62443_OntologyDependency";

            OntologyDependency ontDepAttAck62443 = new OntologyDependency(KNOWLEDGE_BASE_NAMESPACE,
                    "AttAck_62443_OntologyDependency", "ics-attAck_iec62443_mapping",
                    "https://iosb.fraunhofer.de/ICS-Security/ics-attAck_iec62443_mapping", downloadLink2,
                    ontologyDependencyFile2, new HashSet<>());
            
            if (!ontDepRepository.existsByUri(ontDepAttAck62443.getUri())) {
                ontDepRepository.save(ontDepAttAck62443);
            }
            else {
                log.debug("Ontology dependency with uri {} already exists. Skip adding", ontDepAttAck62443.getUri());
            }            

            this.addPolicy1(policyRepository, implementationRepository, queryRepository);

            this.addPolicy2(policyRepository, implementationRepository, queryRepository);

            this.addPolicy3(ontDepRepository, policyRepository, implementationRepository, queryRepository);

            this.addPolicy4(policyRepository, implementationRepository, queryRepository);

            this.addPolicy5(policyRepository, implementationRepository, queryRepository);

            this.addPolicy6(ontDepRepository, policyRepository, implementationRepository, queryRepository);
            
            this.addPolicy7(policyRepository, implementationRepository, queryRepository);

            log.info("Found {} policies", policyRepository.count());
        };
    }

    private void addPolicy1(PolicyRepository policyRepository, ImplementationRepository implementationRepository,
            QueryRepository queryRepository) {
        String uri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#Pfsense_Config_Analysis";
        if (!policyRepository.existsByUri(uri)) {



            SwrlRule mergeRedundant = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE,
                    "Merge_Redundant_Netwoks_and_Build_Network_Hierarchy",
                    "Merge Redundant Netwoks and Build Network Hierarchy",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    "ICS-Security:Network(?n1)^ICS-Security:Network(?n2)^ICS-Security:prefixBits(?n1,?pb1)^ICS-Security:prefixBits(?n2, ?pb2)^swrlb:equal(?pb1, ?pb2)^ICS-Security:ipV4Address(?n1, ?a1)^ICS-Security:ipV4Address(?n2, ?a2)^swrlb:equal(?a1, ?a2)->sameAs(?n1, ?n2)");

            SwrlRule buildNetworkHierarchy = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Build_Network_Hierarchy",
                    "Build Network Hierarchy",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    "ICS-Security:Network(?n1)\n" + "^ ICS-Security:Network(?n2)\n"
                            + "^ ICS-Security:ipV4Address(?n1, ?na1)\n" + "^ ICS-Security:ipV4Address(?n2, ?na2)\n"
                            + "^ ICS-Security:prefixBits(?n1, ?np1)\n" + "^ ICS-Security:prefixBits(?n2, ?np2)\n"
                            + "^ swrlb:lessThan(?np2, ?np1)\n" + "^ swrlb:subtract(?diff, ?np1, ?np2)\n"
                            + "^ swrlb:pow(?divisor, 2, ?diff)\n"
                            + "^ swrlb:divide(?res1, ?na1, ?divisor) ^ swrlb:equal(?na2, ?res1) -> ICS-Security:subnet(?n2, ?n1)");

            SwrlRule addAssetsToNetworks = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Add_Assets_to_Network",
                    "Add Assets to Network",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Arrays.asList(mergeRedundant, buildNetworkHierarchy),
                    new ArrayList<>(),
                    "ICS-Security:Asset(?a) ^ ICS-Security:Network(?n) ^ ICS-Security:IpV4Interface(?i) ^ ICS-Security:isInNetwork(?i, ?n)^ICS-Security:interface(?a,?i) -> ICS-Security:isInNetwork(?a, ?n)");

            SwrlRule relateToParentNetworks = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE,
                    "Create_Relations_to_Parent_Networks", "Create Relations to Parent Networks",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Arrays.asList(mergeRedundant, buildNetworkHierarchy),
                    new ArrayList<>(),
                    "ICS-Security:IpV4Interface(?i) ^ ICS-Security:isInNetwork(?i, ?n)^ICS-Security:parentNetwork(?n,?pn) -> ICS-Security:isInNetwork(?i, ?pn)");

            SwrlRule swrlPreparePfsense = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Prepare_pfSense_Rules",
                    "Prepare pfSense Rules",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    "ICS-Security:PfConfiguration(?config)^ICS-Security:containsRule(?config,?firstRule)^ICS-Security:pfNextRule(?firstRule, ?secondRule)->ICS-Security:containsRule(?config, ?secondRule)");

            Topic pfsenseTopic = new Topic(KNOWLEDGE_BASE_NAMESPACE, "pfsense_effective_config",
                    "topic for effektive configuration pfsense worker", "pfsense_effective_config");
            ProcessingModule pfsense = new ProcessingModule(KNOWLEDGE_BASE_NAMESPACE,
                    "Create_PfSense_Effective_Configuration", "Create PfSense Effective Configuration",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    pfsenseTopic, Arrays.asList(addAssetsToNetworks, relateToParentNetworks, swrlPreparePfsense),
                    Collections.emptyList(), "fw_effective_configuration" ,"fec");

            SwrlRule furtherConnect = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Further_Connect_Flows_to_Network",
                    "Further Connect Flows to Network",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Collections.singletonList(pfsense), new ArrayList<>(),
                    ":AllowedIpV4Flow(?aif)^:srcNetwork(?aif, ?srcNet)^:parentNetwork(?subNet, ?srcNet)->:srcNetwork(?aif, ?subNet)");

            SwrlRule labelOverlappingSrc = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Overlapping_Flows_Src",
                    "Label Overlapping Flows Source",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.DYNAMIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Collections.singletonList(furtherConnect),
                    new ArrayList<>(),
                    ":AllowedTcpFlow(?atf)^:DisallowedTcpFlow(?dtf)^:srcPortRange(?atf, ?aSrcPR)^:srcPortRange(?dtf, ?dSrcPR)^:maxPort(?aSrcPR, ?aSrcPMax)^:minPort(?dSrcPR, ?dSrcPMin)^:minPort(?aSrcPR, ?aSrcPMin)^:maxPort(?dSrcPR, ?dSrcPMax)^swrlb:lessThanOrEqual(?aSrcPMax, ?dSrcPMin)^swrlb:lessThanOrEqual(?aSrcPMin, ?dSrcPMax)->:overlapsWith(?dSrcPR, ?aSrcPR)");

            SwrlRule labelOverlappingDst = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Overlapping_Flows_Dst",
                    "Label Overlapping Flows Destination",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.DYNAMIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Collections.singletonList(furtherConnect),
                    new ArrayList<>(),
                    ":AllowedTcpFlow(?atf)^:DisallowedTcpFlow(?dtf)^:dstPortRange(?atf, ?aDstPR)^:dstPortRange(?dtf, ?dDstPR)^:maxPort(?aDstPR, ?aDstPMax)^:minPort(?dDstPR, ?dDstPMin)^:minPort(?aDstPR, ?aDstPMin)^:maxPort(?dDstPR, ?dDstPMax)^swrlb:lessThanOrEqual(?aDstPMax, ?dDstPMin)^swrlb:lessThanOrEqual(?aDstPMin, ?dDstPMax)->:overlapsWith(?dDstPR, ?aDstPR)");

            SwrlRule labelConflicts = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Conflicts", "Label Conflicts",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.ANALYSIS),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Arrays.asList(labelOverlappingSrc, labelOverlappingDst),
                    new ArrayList<>(),
                    ":AllowedTcpFlow(?atf)^:DisallowedTcpFlow(?dtf)^\n"
                            + ":AllowedIpV4Flow(?aif)^:DisallowedIpV4Flow(?dif)^\n"
                            + ":usesFlow(?atf, ?aif)^:usesFlow(?dtf, ?dif)^\n"
                            + ":srcNetwork(?aif, ?srcNet)^:srcNetwork(?dif, ?srcNet)^\n"
                            + ":dstNetwork(?aif, ?dstNet)^:dstNetwork(?dif, ?dstNet)^\n"
                            + ":portRange(?atf, ?aPR)^:portRange(?dtf, ?dPR)^\n"
                            + ":overlapsWith(?aPR, ?dPR)->:inConflictWith(?dtf, ?atf)");

            AnalyticQuery queryConfig = new AnalyticQuery(KNOWLEDGE_BASE_NAMESPACE, "Config_Analysis_Query",
                    "PREFIX : <http://iosb.fraunhofer.de/ICS-Security#>\n"
                            + "SELECT ?firewall1 ?firewall2 ?f1 ?f2 WHERE {\n"
                            + "    ?firewall1 :firewallConfig ?config1.\n" + "    ?config1 :flow ?f1.\n"
                            + "    ?firewall2 :firewallConfig ?config2.\n" + "    ?config2 :flow ?f2.\n"
                            + "    ?tcpFlow1 :usesFlow ?f1.\n" + "    ?tcpFlow2 :usesFlow ?f2.\n"
                            + "    ?tcpFlow1 :inConflictWith ?tcpFlow2.\n" + "}",
                    Collections.singletonList(labelConflicts), new ArrayList<>());
            PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE,
                    "Config_Analysis_Policy_Implementation", "Is a policy implementation for Config_Analysis_Example",
                    queryConfig);

            Policy configAnalysis = new Policy(uri, "PfSense Config Analysis",
                    Collections.singletonList(policyImplementation));

            policyRepository.save(configAnalysis);
            log.info("Create policy {}", configAnalysis.getUri());
        }

    }
   
    private void addPolicy2(PolicyRepository policyRepository, ImplementationRepository implementationRepository, QueryRepository queryRepository) {
    	String plicyUri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#NIST_800-82_5-7-1";
    	if (!policyRepository.existsByUri(plicyUri)) {

            SwrlRule ClassifyFirewalls = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Classify_Firewalls",
                    "Classify Firewalls",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    ":Asset(?a)^:firewallConfig(?a, ?c)^:Configuration(?c)\n" + "->:Firewall(?a)");

            SwrlRule labelWhitelistFirewalls = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Whitelist_Firewalls",
                    "Label PfSense Firewalls as Whitelist Firewalls",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.ANALYSIS),
                    topicService.getTopicByUri(SWRL_TOPIC_URI),
                    Collections.singletonList(ClassifyFirewalls),
                    new ArrayList<>(),
                    ":Firewall(?f)^:PfConfiguration(?c)^\n" +
                    ":firewallConfig(?f, ?c)^\n" +
                    "->:WhitelistFirewall(?f)"
            );
            
    		AnalyticQuery queryBlacklistFirewalls = new AnalyticQuery(KNOWLEDGE_BASE_NAMESPACE, "Blacklist_Firewalls_Query", 
    				"PREFIX : <http://iosb.fraunhofer.de/ICS-Security#>\n" + 
            	            "SELECT ?asset  WHERE {\n" + 
            	            "    ?asset a :Firewall.\n" + 
            	            "    MINUS{ ?asset a :WhitelistFirewall.}\n" + 
            	            "}", Collections.singletonList(labelWhitelistFirewalls), new ArrayList<>());
    		
    		PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE, "PfSense_Whitelist_Labelling", "Very simple labelling of firewalls, marking each PfSense firewall as whitelist firewall", queryBlacklistFirewalls);

    		
		    Policy nist55 = new Policy();
		    nist55.setUri(plicyUri);
		    nist55.setDescription("NIST 800-82 best practice 5.7 FW rule set 1");
		    nist55.setPolicyImplementations(Collections.singletonList(policyImplementation));
		
		
		    policyRepository.save(nist55);
		    log.info("Create policy {}", nist55.getUri());
		}
    }

    
    private void addPolicy3(OntologyDependencyRepository ontDepRepository, PolicyRepository policyRepository, ImplementationRepository implementationRepository, QueryRepository queryRepository) throws IOException {
    	String plicyUri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#NIST_800-82_5-5-1";
    	if (!policyRepository.existsByUri(plicyUri)) {

    		ArrayList<Implementation> impls = new ArrayList<Implementation>();
    		impls.add(this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"Merge_Redundant_Netwoks_and_Build_Network_Hierarchy"));
    		impls.add(this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"Build_Network_Hierarchy"));
    		impls.add(this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"Add_Assets_to_Network"));
    		impls.add(this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"Create_Relations_to_Parent_Networks"));
    		impls.add(this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"Classify_Firewalls"));
    		
    		
    		OntologyDependency ontDepLab62443 = this.getOntologyDependency(ontDepRepository, KNOWLEDGE_BASE_NAMESPACE
    				+ "Lab_62443_OntologyDependency");
    		
    		JenaRule labelDualHomed = new JenaRule(KNOWLEDGE_BASE_NAMESPACE,
                    "label_Dual_Homed_Devices",
                    "Label Dual Homed Devices in Field and Enterprise Zone.",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.ANALYSIS),
                    topicService.getTopicByUri(JENA_TOPIC_URI), impls, Arrays.asList(ontDepLab62443),
                    "[classifyDualHomed: (?a rdf:type :Asset),noValue(?a rdf:type :Firewall),"
                    + "(?a :isInNetwork <http://iosb.fraunhofer.de/ICS-Security/pfsense1#172_16_110_0_24>),(?a :isInNetwork <http://iosb.fraunhofer.de/ICS-Security/pfsense1#172_16_121_0_24>)"
                    + "->(?a rdf:type <http://iosb.fraunhofer.de/security/iec62443#DualHomedDevice>)]"
            );
            
    		AnalyticQuery queryDualHomed = new AnalyticQuery(KNOWLEDGE_BASE_NAMESPACE, "Dual_Homed_Devices_Query",
            	            "PREFIX  iec62443: <http://iosb.fraunhofer.de/security/iec62443#>\n" + 
            	            "SELECT ?asset  WHERE {\n" + 
            	            "    ?asset a iec62443:DualHomedDevice.\n" +  
            	            "}", Collections.singletonList(labelDualHomed), new ArrayList<>());
    		
    		PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE, "DualHomed_Field_Enterprise_Labelling", "Labels every asset as iec62443:DualHomedDevice which is in networks of zone iec62443:FieldZone and iec62443:EnterpriseZone.", queryDualHomed);
    		
		    Policy nist531 = new Policy(plicyUri, "NIST 800-82 best practice 5.5.1", Collections.singletonList(policyImplementation));
		    		
		    policyRepository.save(nist531);
		    log.info("Create policy {}", nist531.getUri());
		}
    }
    
    
    private void addPolicy4(PolicyRepository policyRepository, ImplementationRepository implementationRepository, QueryRepository queryRepository) {
    	String plicyUri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#62443-3-2_ZCR-3-2";
    	if (!policyRepository.existsByUri(plicyUri)) {
    		
    		Implementation impl1 = this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"Label_Whitelist_Firewalls");
    		
    		Implementation impl2 = this.getImplementation(implementationRepository, KNOWLEDGE_BASE_NAMESPACE
    				+"label_Dual_Homed_Devices");
    		
            AnalyticQuery queryBlacklistAndDualHomed = new AnalyticQuery(KNOWLEDGE_BASE_NAMESPACE, "Blacklist_Dualhomed_Query",
            	            "PREFIX : <http://iosb.fraunhofer.de/ICS-Security#>\n" + 
            	            "PREFIX  iec62443: <http://iosb.fraunhofer.de/security/iec62443#>\n" + 
            	            "SELECT ?asset WHERE {\n" + 
            	            "    {?asset a iec62443:DualHomedDevice.}\n" + 
            	            "    UNION\n" + 
            	            "    {\n" + 
            	            "        ?asset a :Firewall.\n" + 
            	            "    	MINUS{ ?asset a :WhitelistFirewall. }\n" + 
            	            "    }\n" + 
            	            "}", Arrays.asList(impl1, impl2), new ArrayList<OntologyDependency>());

            PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE,
                    "iec62443_Zcr_3_2_Policy_Implementation",
                    "A policy implementation for IEC 62443-3-2 requirement ZCR 3.2 based on black list firewall and dual homed device detection.",
                    queryBlacklistAndDualHomed);
            Policy iec1 = new Policy();
            iec1.setUri(plicyUri);
            iec1.setDescription("IEC 62443-3-2 requirement ZCR 3.2");
            iec1.setPolicyImplementations(Collections.singletonList(policyImplementation));

            policyRepository.save(iec1);
            log.info("Create policy {}", iec1.getUri());
        }

    }

    private Implementation getImplementation(ImplementationRepository implementationRepository, String uri) {
        Optional<Implementation> impl = implementationRepository.findByUri(uri);

        if (!impl.isPresent()) {
            throw new RuntimeException("Error adding policy, since " + uri + " is missing.");
        } else {
            return impl.get();
        }
    }

    private OntologyDependency getOntologyDependency(OntologyDependencyRepository ontDepRepository, String uri) {
    	Optional<OntologyDependency> ontDep = ontDepRepository.findByUri(uri);
		
		if(!ontDep.isPresent()) {
			throw new RuntimeException("Error adding policy, since " + uri + " is missing.");
		} else {
			return ontDep.get();
		}
	}


	private void addPolicy5(PolicyRepository policyRepository, ImplementationRepository implementationRepository, QueryRepository queryRepository) {
    	String plicyUri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#NIST_800-82_5-5";
    	if (!policyRepository.existsByUri(plicyUri)) {
    		Optional<Query> query = queryRepository.findByUri(KNOWLEDGE_BASE_NAMESPACE+"Blacklist_Dualhomed_Query");
    	
    		if(!query.isPresent()) {
    			throw new RuntimeException("Error adding policy 5, since " + KNOWLEDGE_BASE_NAMESPACE+"Blacklist_Dualhomed_Query" + " is missing.");
    		}
    		
    		PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE, "NIST_800-82_5-5_Policy_Implementation", "A policy implementation for NIST 800-82 best practice 5.5 based on black list firewall and dual homed device detection.", query.get());
    		PolicyImplementation policyImplementation2 = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE, "Alternative_NIST_800-82_5-5_Policy_Implementation", "A policy implementation for NIST 800-82 best practice 5.5 only dual homed device detection, if no firewall information is available.", query.get());
    		
		    Policy nist53 = new Policy();
		    nist53.setUri(plicyUri);
		    nist53.setDescription("NIST 800-82 best practice 5.5");
		    nist53.setPolicyImplementations(Arrays.asList(policyImplementation, policyImplementation2));
				
		    policyRepository.save(nist53);
		    log.info("Create policy {}", nist53.getUri());
		}
    }

    private void addPolicy6(OntologyDependencyRepository ontDepRepository, PolicyRepository policyRepository,
            ImplementationRepository implementationRepository, QueryRepository queryRepository) {
        String plicyUri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#ICS_ATTACK_M1030";
        if (!policyRepository.existsByUri(plicyUri)) {
            OntologyDependency ontDepAttAck62443 = this.getOntologyDependency(ontDepRepository,
                    KNOWLEDGE_BASE_NAMESPACE + "AttAck_62443_OntologyDependency");

            Implementation impl1 = this.getImplementation(implementationRepository,
                    KNOWLEDGE_BASE_NAMESPACE + "Label_Whitelist_Firewalls");

            Implementation impl2 = this.getImplementation(implementationRepository,
                    KNOWLEDGE_BASE_NAMESPACE + "label_Dual_Homed_Devices");

            AnalyticQuery query = new AnalyticQuery(KNOWLEDGE_BASE_NAMESPACE, "Blacklist_Dualhomed_Query_M1030",
                    "PREFIX : <http://iosb.fraunhofer.de/ICS-Security#>\n"
                            + "PREFIX  iec62443: <http://iosb.fraunhofer.de/security/iec62443#>\n"
                            + "PREFIX attAck: <https://iosb.fraunhofer.de/security/ics-attAck#>\n"
                            + "SELECT ?asset WHERE {\n" + "    {?asset a iec62443:DualHomedDevice.}\n" + "    UNION\n"
                            + "    {\n" + "        ?asset a :Firewall.\n"
                            + "    	MINUS{ ?asset a :WhitelistFirewall. }\n" + "    }\n"
                            + "    MINUS{ ?asset attAck:isA attAck:M1030. }\n" + "}",
                    Arrays.asList(impl1, impl2), Arrays.asList(ontDepAttAck62443));

            PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE,
                    "ICS_ATTACK_mitigation_M1030_Implementation",
                    "A policy implementation for necessary ICS ATT&CK mitigation M1030 detection based on black list firewall and dual homed device detection.",
                    query);

            Policy icsattck1 = new Policy();
            icsattck1.setUri(plicyUri);
            icsattck1.setDescription("ICS ATT&CK mitigation M1030");
            icsattck1.setPolicyImplementations(Arrays.asList(policyImplementation));

            policyRepository.save(icsattck1);
            log.info("Create policy {}", icsattck1.getUri());
        }
    }
    
    private void addPolicy7(PolicyRepository policyRepository, ImplementationRepository implementationRepository,
            QueryRepository queryRepository) {
        String uri = "https://iosb.fraunhofer.de/ICS-Security/policy-based-analysis-kb#Pfsense_Config_Analysis_K8S";
        if (!policyRepository.existsByUri(uri)) {

            SwrlRule mergeRedundant = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE,
                    "Merge_Redundant_Netwoks_and_Build_Network_Hierarchy_K8S",
                    "Merge Redundant Netwoks and Build Network Hierarchy for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    "ICS-Security:Network(?n1)^ICS-Security:Network(?n2)^ICS-Security:prefixBits(?n1,?pb1)^ICS-Security:prefixBits(?n2, ?pb2)^swrlb:equal(?pb1, ?pb2)^ICS-Security:ipV4Address(?n1, ?a1)^ICS-Security:ipV4Address(?n2, ?a2)^swrlb:equal(?a1, ?a2)->sameAs(?n1, ?n2)");

            SwrlRule buildNetworkHierarchy = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Build_Network_Hierarchy_K8S",
                    "Build Network Hierarchy for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    "ICS-Security:Network(?n1)\n" + "^ ICS-Security:Network(?n2)\n"
                            + "^ ICS-Security:ipV4Address(?n1, ?na1)\n" + "^ ICS-Security:ipV4Address(?n2, ?na2)\n"
                            + "^ ICS-Security:prefixBits(?n1, ?np1)\n" + "^ ICS-Security:prefixBits(?n2, ?np2)\n"
                            + "^ swrlb:lessThan(?np2, ?np1)\n" + "^ swrlb:subtract(?diff, ?np1, ?np2)\n"
                            + "^ swrlb:pow(?divisor, 2, ?diff)\n"
                            + "^ swrlb:divide(?res1, ?na1, ?divisor) ^ swrlb:equal(?na2, ?res1) -> ICS-Security:subnet(?n2, ?n1)");

            SwrlRule addAssetsToNetworks = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Add_Assets_to_Network_K8S",
                    "Add Assets to Network for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Arrays.asList(mergeRedundant, buildNetworkHierarchy),
                    new ArrayList<>(),
                    "ICS-Security:Asset(?a) ^ ICS-Security:Network(?n) ^ ICS-Security:IpV4Interface(?i) ^ ICS-Security:isInNetwork(?i, ?n)^ICS-Security:interface(?a,?i) -> ICS-Security:isInNetwork(?a, ?n)");

            SwrlRule relateToParentNetworks = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE,
                    "Create_Relations_to_Parent_Networks_K8S", "Create Relations to Parent Networks for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Arrays.asList(mergeRedundant, buildNetworkHierarchy),
                    new ArrayList<>(),
                    "ICS-Security:IpV4Interface(?i) ^ ICS-Security:isInNetwork(?i, ?n)^ICS-Security:parentNetwork(?n,?pn) -> ICS-Security:isInNetwork(?i, ?pn)");

            SwrlRule swrlPreparePfsense = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Prepare_pfSense_Rules_K8S",
                    "Prepare pfSense Rules for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), new ArrayList<>(), new ArrayList<>(),
                    "ICS-Security:PfConfiguration(?config)^ICS-Security:containsRule(?config,?firstRule)^ICS-Security:pfNextRule(?firstRule, ?secondRule)->ICS-Security:containsRule(?config, ?secondRule)");

            Topic pfsenseTopic = new Topic(KNOWLEDGE_BASE_NAMESPACE, "pfsense_effective_config_k8s",
                    "topic for effektive configuration pfsense worker for k8s", "pfsense_effective_config_k8s");
            ProcessingModule pfsense = new ProcessingModule(KNOWLEDGE_BASE_NAMESPACE,
                    "Create_PfSense_Effective_Configuration_K8S", "Create PfSense Effective Configuration for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    pfsenseTopic, Arrays.asList(addAssetsToNetworks, relateToParentNetworks, swrlPreparePfsense),
                    Collections.emptyList(), "fw_effective_configuration", "fec:feature-rancher-test");

            SwrlRule furtherConnect = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Further_Connect_Flows_to_Network_K8S",
                    "Further Connect Flows to Network for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.STATIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Collections.singletonList(pfsense), new ArrayList<>(),
                    ":AllowedIpV4Flow(?aif)^:srcNetwork(?aif, ?srcNet)^:parentNetwork(?subNet, ?srcNet)->:srcNetwork(?aif, ?subNet)");

            SwrlRule labelOverlappingSrc = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Overlapping_Flows_Src_K8S",
                    "Label Overlapping Flows Source for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.DYNAMIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Collections.singletonList(furtherConnect),
                    new ArrayList<>(),
                    ":AllowedTcpFlow(?atf)^:DisallowedTcpFlow(?dtf)^:srcPortRange(?atf, ?aSrcPR)^:srcPortRange(?dtf, ?dSrcPR)^:maxPort(?aSrcPR, ?aSrcPMax)^:minPort(?dSrcPR, ?dSrcPMin)^:minPort(?aSrcPR, ?aSrcPMin)^:maxPort(?dSrcPR, ?dSrcPMax)^swrlb:lessThanOrEqual(?aSrcPMax, ?dSrcPMin)^swrlb:lessThanOrEqual(?aSrcPMin, ?dSrcPMax)->:overlapsWith(?dSrcPR, ?aSrcPR)");

            SwrlRule labelOverlappingDst = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Overlapping_Flows_Dst_K8S",
                    "Label Overlapping Flows Destination for k8s",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.DYNAMIC_KNOWLEDGE_EXTENSION),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Collections.singletonList(furtherConnect),
                    new ArrayList<>(),
                    ":AllowedTcpFlow(?atf)^:DisallowedTcpFlow(?dtf)^:dstPortRange(?atf, ?aDstPR)^:dstPortRange(?dtf, ?dDstPR)^:maxPort(?aDstPR, ?aDstPMax)^:minPort(?dDstPR, ?dDstPMin)^:minPort(?aDstPR, ?aDstPMin)^:maxPort(?dDstPR, ?dDstPMax)^swrlb:lessThanOrEqual(?aDstPMax, ?dDstPMin)^swrlb:lessThanOrEqual(?aDstPMin, ?dDstPMax)->:overlapsWith(?dDstPR, ?aDstPR)");

            SwrlRule labelConflicts = new SwrlRule(KNOWLEDGE_BASE_NAMESPACE, "Label_Conflicts_K8S", "Label Conflicts_K8S",
                    phaseService.getPhaseByStaticPhase(PhaseService.StaticPhase.ANALYSIS),
                    topicService.getTopicByUri(SWRL_TOPIC_URI), Arrays.asList(labelOverlappingSrc, labelOverlappingDst),
                    new ArrayList<>(),
                    ":AllowedTcpFlow(?atf)^:DisallowedTcpFlow(?dtf)^\n"
                            + ":AllowedIpV4Flow(?aif)^:DisallowedIpV4Flow(?dif)^\n"
                            + ":usesFlow(?atf, ?aif)^:usesFlow(?dtf, ?dif)^\n"
                            + ":srcNetwork(?aif, ?srcNet)^:srcNetwork(?dif, ?srcNet)^\n"
                            + ":dstNetwork(?aif, ?dstNet)^:dstNetwork(?dif, ?dstNet)^\n"
                            + ":portRange(?atf, ?aPR)^:portRange(?dtf, ?dPR)^\n"
                            + ":overlapsWith(?aPR, ?dPR)->:inConflictWith(?dtf, ?atf)");

            AnalyticQuery queryConfig = new AnalyticQuery(KNOWLEDGE_BASE_NAMESPACE, "Config_Analysis_Query_K8S",
                    "PREFIX : <http://iosb.fraunhofer.de/ICS-Security#>\n"
                            + "SELECT ?firewall1 ?firewall2 ?f1 ?f2 WHERE {\n"
                            + "    ?firewall1 :firewallConfig ?config1.\n" + "    ?config1 :flow ?f1.\n"
                            + "    ?firewall2 :firewallConfig ?config2.\n" + "    ?config2 :flow ?f2.\n"
                            + "    ?tcpFlow1 :usesFlow ?f1.\n" + "    ?tcpFlow2 :usesFlow ?f2.\n"
                            + "    ?tcpFlow1 :inConflictWith ?tcpFlow2.\n" + "}",
                    Collections.singletonList(labelConflicts), new ArrayList<>());
            
            
            PolicyImplementation policyImplementation = new PolicyImplementation(KNOWLEDGE_BASE_NAMESPACE,
                    "Config_Analysis_Policy_Implementation_K8S", "Is a policy implementation for Config_Analysis_Example in K8S",
                    queryConfig);

            Policy configAnalysis = new Policy(uri, "PfSense Config Analysis_K8S",
                    Collections.singletonList(policyImplementation));

            policyRepository.save(configAnalysis);
            log.info("Create policy {}", configAnalysis.getUri());
        }

    }

}