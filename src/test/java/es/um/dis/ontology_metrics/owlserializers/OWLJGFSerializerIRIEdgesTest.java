package es.um.dis.ontology_metrics.owlserializers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.github.fanavarro.graphlib.serializers.JSONSerializerValidator;

import es.um.dis.ontology_metrics.owlgraphs.OWLTaxonomicGraph;

public class OWLJGFSerializerIRIEdgesTest {

	private static final String PIZZA_ONTOLOGY_PATH = "/ontologies/pizza.owl";
	
	@Test
	public void testSerialize() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLTaxonomicGraph graph = new OWLTaxonomicGraph(ontology, new StructuralReasonerFactory());
		
		OWLJGFSerializerIRIEdges serializer = new OWLJGFSerializerIRIEdges();
		String s = serializer.serialize(graph, "Pizza ontology");
		assertNotNull(s);
		assertFalse(s.isEmpty());
		JSONSerializerValidator.validateJGF(s);
	}

}
