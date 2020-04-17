package es.um.dis.ontology_metrics.owlgraphs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OWLObjectPropertyGraph extends OWLGraph<IRI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4832037571888965116L;

	public OWLObjectPropertyGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory,
			boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
	}

	public OWLObjectPropertyGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
	}

	@Override
	public Map<IRI, Set<OWLClass>> getAdjacentNodesWithEdges(OWLClass node) {
		Map<IRI, Set<OWLClass>> adjacentNodesWithEdges = new HashMap<IRI, Set<OWLClass>>();
		Stream<OWLObjectProperty> objectProperties = getOntology()
				.objectPropertiesInSignature(Imports.fromBoolean(getIncludeImportsClosure()));
		objectProperties
				.filter(x -> (getReasoner().objectPropertyDomains(x, true).collect(Collectors.toSet()).contains(node)))
				.forEach(objectProperty -> {
					Set<OWLClass> adjacentNodes = getReasoner().getObjectPropertyRanges(objectProperty, true).entities()
							.collect(Collectors.toSet());
					adjacentNodesWithEdges.put(objectProperty.getIRI(), adjacentNodes);
				});

		return adjacentNodesWithEdges;
	}

}
