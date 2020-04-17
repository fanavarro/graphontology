package es.um.dis.ontology_metrics.owlserializers;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fanavarro.graphlib.Graph;

public class OWLJGFSerializerAxiomEdges extends OWLJGFSerializer<OWLAxiom> {

	@Override
	protected ObjectNode serializeEdge(OWLClass sourceNode, OWLAxiom edge, OWLClass targetNode,
			Graph<OWLClass, OWLAxiom> graph) {
		ObjectNode edgeJson = super.serializeEdge(sourceNode, edge, targetNode, graph);
		edgeJson.put(SOURCE, sourceNode.toStringID());
		edgeJson.put(TARGET, targetNode.toStringID());
		return edgeJson;
	}

	@Override
	protected ObjectNode getMetadataEdge(OWLAxiom edge, Graph<OWLClass, OWLAxiom> graph) {
		return null;
	}

}
