package es.um.dis.ontology_metrics.owlserializers;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fanavarro.graphlib.Graph;

public class OWLJGFSerializerIRIEdges extends OWLJGFSerializer<IRI> {

	@Override
	protected ObjectNode serializeEdge(OWLClass sourceNode, IRI edge, OWLClass targetNode, Graph<OWLClass, IRI> graph) {
		ObjectNode edgeJson = super.serializeEdge(sourceNode, edge, targetNode, graph);
		edgeJson.put(SOURCE, sourceNode.toStringID());
		edgeJson.put(TARGET, targetNode.toStringID());
		return edgeJson;
	}


	@Override
	protected ObjectNode getMetadataEdge(IRI edge, Graph<OWLClass, IRI> graph) {
		OWLOntology ontology = this.getOntologyFromGraph(graph);
		if(ontology == null){
			return super.getMetadataEdge(edge, graph);
		}
		OWLEntity owlEntity = this.getOWLEntity(edge, ontology);
		
		return this.getMetadata(owlEntity, ontology);
	}

}
