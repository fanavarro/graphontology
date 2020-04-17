package es.um.dis.ontology_metrics.owlserializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fanavarro.graphlib.Graph;
import com.github.fanavarro.graphlib.serializers.JGFSerializer;

import es.um.dis.ontology_metrics.owlgraphs.OWLGraph;

public abstract class OWLJGFSerializer<E> extends JGFSerializer<OWLClass, E>{
	
	@Override
	protected ObjectNode serializeNode(OWLClass node, Graph<OWLClass, E> graph) {
		ObjectNode jsonGraphNode = super.serializeNode(node, graph);
		jsonGraphNode.put(ID, node.toStringID());
		jsonGraphNode.put(LABEL, node.getIRI().getFragment());
		return jsonGraphNode;
	}
	
	@Override
	protected ObjectNode getMetadataNode(OWLClass node, Graph<OWLClass, E> graph) {
		OWLOntology ontology = this.getOntologyFromGraph(graph);
		if(ontology == null){
			return super.getMetadataNode(node, graph);
		}
		return this.getMetadata(node, ontology);
	}
	
	/**
	 * Get the metadata node from the specified entity in the specified ontology.
	 *
	 * @param entity the entity
	 * @param ontology the ontology
	 * @return An ObjectNode with the metadata.
	 */
	protected ObjectNode getMetadata(OWLEntity entity, OWLOntology ontology) {
		ObjectNode metadataNode = this.getMapper().createObjectNode();
		if (entity != null) {
			Map<IRI, List<String>> nodeAnnotations = this.getAnnotations(entity, ontology);
			for (Entry<IRI, List<String>> entry : nodeAnnotations.entrySet()) {
				IRI annotationIRI = entry.getKey();
				List<String> annotationValues = entry.getValue();
				if (annotationValues.isEmpty()) {
					continue;
				}
				if (annotationValues.size() == 1) {
					metadataNode.put(annotationIRI.toString(), annotationValues.get(0));
				} else {
					ArrayNode array = metadataNode.putArray(annotationIRI.toString());
					for (String value : annotationValues) {
						array.add(value);
					}
				}
			}
		}
		return metadataNode;
	}
	
	protected Map<IRI, List<String>> getAnnotations(OWLEntity owlEntity, OWLOntology ontology) {
		Map<IRI, List<String>> annotationValues = new HashMap<>();
		EntitySearcher.getAnnotationAssertionAxioms(owlEntity, ontology).forEach(annotationAssertionAxiom -> {
			IRI propertyIRI = annotationAssertionAxiom.getProperty().getIRI();
			if (!annotationValues.containsKey(propertyIRI)) {
				annotationValues.put(propertyIRI, new ArrayList<String>());
			}

			if (annotationAssertionAxiom.annotationValue() instanceof OWLLiteral) {
				OWLLiteral literal = annotationAssertionAxiom.annotationValue().asLiteral().orElse(null);
				if (literal != null) {
					String annotationValue = "\"" + literal.getLiteral() + "\"";
					if (literal.hasLang()) {
						annotationValue = annotationValue + "@" + literal.getLang();
					}
					annotationValues.get(propertyIRI).add(annotationValue);
				}
			}
		});

		return annotationValues;
	}
	
	protected OWLOntology getOntologyFromGraph(Graph<OWLClass, E> graph){
		OWLOntology ontology = null;
		if (graph instanceof OWLGraph) {
			OWLGraph<E> owlGraph = (OWLGraph<E>) graph;
			ontology = owlGraph.getOntology();
		}
		return ontology;
	}
	
	protected OWLEntity getOWLEntity(IRI entityIRI, OWLOntology ontology) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		if (ontology.containsDataPropertyInSignature(entityIRI)) {
			return df.getOWLDataProperty(entityIRI);
		}
		if (ontology.containsObjectPropertyInSignature(entityIRI)) {
			return df.getOWLObjectProperty(entityIRI);
		}

		return null;
	}
}
