package es.um.dis.ontology_metrics.owlserializers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.github.fanavarro.graphlib.serializers.JSONSerializerValidator;

import es.um.dis.ontology_metrics.owlgraphs.OWLAxiomaticGraph;

public class OWLJGFSerializerAxiomEdgesTest {

	private static final String PIZZA_ONTOLOGY_PATH = "/ontologies/pizza.owl";
	private static final List<AxiomType<?>> IGNORED_AXIOMS = Arrays.asList(AxiomType.DISJOINT_CLASSES,
			AxiomType.DISJOINT_DATA_PROPERTIES, AxiomType.DISJOINT_OBJECT_PROPERTIES, AxiomType.DISJOINT_UNION);
	@Test
	public void testSerialize() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLAxiomaticGraph graph = new OWLAxiomaticGraph(ontology, new StructuralReasonerFactory(), IGNORED_AXIOMS);
		
		OWLJGFSerializerAxiomEdges serializer = new OWLJGFSerializerAxiomEdges();
		String s = serializer.serialize(graph, "Pizza ontology");
		assertNotNull(s);
		assertFalse(s.isEmpty());
		System.out.println(s);
		JSONSerializerValidator.validateJGF(s);
	}

}
