package es.um.dis.ontology_metrics.owlgraphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OWLObjectPropertyGraph extends OWLGraph {

	public OWLObjectPropertyGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory,
			boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
	}

	public OWLObjectPropertyGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
	}

	@Override
	public Map<AxiomType<?>, Set<OWLClass>> getAdjacentNodesWithEdges(OWLClass node) {
		Map<AxiomType<?>, Set<OWLClass>> adjacentNodesWithEdges = new HashMap<AxiomType<?>, Set<OWLClass>>();
		Stream<OWLObjectProperty> objectProperties = getOntology()
				.objectPropertiesInSignature(Imports.fromBoolean(getIncludeImportsClosure()));
//		objectProperties = objectProperties
//				.filter(x -> (getReasoner().objectPropertyDomains(x, true).collect(Collectors.toSet()).contains(node)));
//		
//		Set<OWLClass> adjacentNodes = objectProperties.map(x -> {
//			return getReasoner().getObjectPropertyRanges(x, true).entities().collect(Collectors.toSet());
//		}).flatMap(Collection::stream).collect(Collectors.toSet());
		
		Set<OWLClass> adjacentNodes = objectProperties
		.filter(x -> (getReasoner().objectPropertyDomains(x, true).collect(Collectors.toSet()).contains(node))).map(x -> {
			return getReasoner().getObjectPropertyRanges(x, true).entities().collect(Collectors.toSet());
		}).flatMap(Collection::stream).collect(Collectors.toSet());
		
		adjacentNodesWithEdges.put(AxiomType.OBJECT_PROPERTY_RANGE, adjacentNodes);
		return adjacentNodesWithEdges;
	}

}
