package es.um.dis.ontology_metrics.owlgraphs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OWLCompleteGraph extends OWLGraph<IRI> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7188853508410783941L;

	public OWLCompleteGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
	}

	public OWLCompleteGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
	}

	public OWLCompleteGraph(OWLReasoner reasoner) {
		super(reasoner);
	}
	
	public OWLCompleteGraph(OWLReasoner reasoner, boolean includeImportsClosure) {
		super(reasoner, includeImportsClosure);
	}

	@Override
	public Map<IRI, Set<OWLClass>> getAdjacentNodesByEdgeMap(OWLClass node) {
		Map<IRI, Set<OWLClass>> adjacentNodesWithEdges = new HashMap<IRI, Set<OWLClass>>();
		
		/* Add object property edges */
		getOntology().objectPropertiesInSignature(Imports.fromBoolean(getIncludeImportsClosure()))
				.filter(x -> (getReasoner().objectPropertyDomains(x, true).collect(Collectors.toSet()).contains(node)))
				.forEach(objectProperty -> {
					Set<OWLClass> adjacentNodes = getReasoner().getObjectPropertyRanges(objectProperty, true).entities()
							.collect(Collectors.toSet());
					adjacentNodesWithEdges.put(objectProperty.getIRI(), adjacentNodes);
				});
		
		/* Add sub class of */
		adjacentNodesWithEdges.put(OWLGraphVocabulary.RDFS_SUBCLASS_OF, getReasoner().superClasses(node, true).collect(Collectors.toSet()));
		
		/* Add super class of */
		adjacentNodesWithEdges.put(OWLGraphVocabulary.UMBEL_SUPER_CLASS_OF, getReasoner().subClasses(node, true).collect(Collectors.toSet()));

		return adjacentNodesWithEdges;
	}

}
