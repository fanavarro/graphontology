package es.um.dis.ontology_metrics.owlgraphs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.github.fanavarro.graphlib.Graph;
import com.github.fanavarro.graphlib.SimpleTreeImpl;
import com.github.fanavarro.graphlib.Tree;

public class CastTest {
	private static final String PIZZA_ONTOLOGY_PATH = "/ontologies/pizza.owl";
	@Test
	public void test() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(ontology);
		OWLCompleteGraph completeGraph = new OWLCompleteGraph(reasoner);
		OWLAxiomaticGraph axiomaticGraph = new OWLAxiomaticGraph(reasoner);
		Tree<OWLClass, OWLAxiom> tree = new SimpleTreeImpl<>();
		assertTrue(completeGraph instanceof OWLGraph);
		assertTrue(completeGraph instanceof Graph);
		
		assertTrue(axiomaticGraph instanceof OWLGraph);
		assertTrue(axiomaticGraph instanceof Graph);
		
		assertTrue(tree instanceof Tree);
		assertTrue(tree instanceof Graph);
		assertFalse(tree instanceof OWLGraph);
		
		Set<Graph<OWLClass, OWLAxiom>> set = new HashSet<>();
		set.add(tree);
		set.add(axiomaticGraph);
	}

}
