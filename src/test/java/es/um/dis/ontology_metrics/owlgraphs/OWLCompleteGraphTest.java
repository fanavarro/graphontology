package es.um.dis.ontology_metrics.owlgraphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.github.fanavarro.graphlib.algorithms.Algorithm;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathAlgorithm;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathInput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathOutput;

public class OWLCompleteGraphTest {
	private static final String PIZZA_ONTOLOGY_PATH = "/ontologies/pizza.owl";
	private static final String NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
	private static final IRI FISH_TOPPING = IRI.create(NS + "FishTopping");
	private static final IRI PIZZA = IRI.create(NS + "Pizza");
	private static final IRI PIZZA_BASE = IRI.create(NS + "PizzaBase");
	private static final IRI PIZZA_TOPPING = IRI.create(NS + "PizzaTopping");
	private static final IRI HAS_BASE = IRI.create(NS + "hasBase");
	private static final IRI FOOD = IRI.create(NS + "Food");

	@Test
	public void testShortestPath1() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLCompleteGraph graph = new OWLCompleteGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass pizza = manager.getOWLDataFactory().getOWLClass(PIZZA);
		OWLClass pizzaBase = manager.getOWLDataFactory().getOWLClass(PIZZA_BASE);
		
		Algorithm<OWLClass, IRI> shortestPath = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setSourceNode(pizza);
		input.setTargetNode(pizzaBase);
		
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) graph.applyAlgorithm(shortestPath, input);
		
		assertNotNull(output);
		assertNotNull(output.getPath());
		assertEquals(1, output.getPath().size());
		
		assertEquals(output.getPath().get(0).getSource().toStringID(), NS + "Pizza");
		assertEquals(1, output.getPath().get(0).getEdges().size());
		assertTrue(output.getPath().get(0).getEdges().contains(HAS_BASE));
		assertEquals(output.getPath().get(0).getTarget().toStringID(), NS + "PizzaBase");
		
	}
	
	@Test
	public void testShortestPath2() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLCompleteGraph graph = new OWLCompleteGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass pizzaTopping = manager.getOWLDataFactory().getOWLClass(PIZZA_TOPPING);
		OWLClass pizzaBase = manager.getOWLDataFactory().getOWLClass(PIZZA_BASE);
		
		Algorithm<OWLClass, IRI> shortestPath = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setSourceNode(pizzaTopping);
		input.setTargetNode(pizzaBase);
		
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) graph.applyAlgorithm(shortestPath, input);
		
		assertNotNull(output);
		assertNotNull(output.getPath());
		assertEquals(2, output.getPath().size());
		
		assertEquals(output.getPath().get(0).getSource().toStringID(), NS + "PizzaTopping");
		assertEquals(1, output.getPath().get(0).getEdges().size());
		assertTrue(output.getPath().get(0).getEdges().contains(OWLGraphVocabulary.RDFS_SUBCLASS_OF));
		assertEquals(output.getPath().get(0).getTarget().toStringID(), NS + "Food");
		
		assertEquals(output.getPath().get(1).getSource().toStringID(), NS + "Food");
		assertEquals(1, output.getPath().get(1).getEdges().size());
		assertTrue(output.getPath().get(1).getEdges().contains(OWLGraphVocabulary.UMBEL_SUPER_CLASS_OF)); // has base
		assertEquals(output.getPath().get(1).getTarget().toStringID(), NS + "PizzaBase");
		
	}
	
	@Test
	public void testShortestPath3() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLCompleteGraph graph = new OWLCompleteGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass seafoodTopping = manager.getOWLDataFactory().getOWLClass(FISH_TOPPING);
		OWLClass pizzaBase = manager.getOWLDataFactory().getOWLClass(PIZZA_BASE);
		
		Algorithm<OWLClass, IRI> shortestPath = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setSourceNode(seafoodTopping);
		input.setTargetNode(pizzaBase);
		
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) graph.applyAlgorithm(shortestPath, input);
		
		assertNotNull(output);
		assertNotNull(output.getPath());
		
		assertEquals(output.getPath().get(0).getSource().getIRI(), FISH_TOPPING);
		assertEquals(1, output.getPath().get(0).getEdges().size());
		assertTrue(output.getPath().get(0).getEdges().contains(OWLGraphVocabulary.RDFS_SUBCLASS_OF));
		assertEquals(output.getPath().get(0).getTarget().getIRI(), PIZZA_TOPPING);
		
		assertEquals(output.getPath().get(1).getSource().getIRI(), PIZZA_TOPPING);
		assertEquals(1, output.getPath().get(1).getEdges().size());
		assertTrue(output.getPath().get(1).getEdges().contains(OWLGraphVocabulary.RDFS_SUBCLASS_OF));
		assertEquals(output.getPath().get(1).getTarget().getIRI(), FOOD);
		
		assertEquals(output.getPath().get(2).getSource().getIRI(), FOOD);
		assertEquals(1, output.getPath().get(2).getEdges().size());
		assertTrue(output.getPath().get(2).getEdges().contains(OWLGraphVocabulary.UMBEL_SUPER_CLASS_OF));
		assertEquals(output.getPath().get(2).getTarget().getIRI(), PIZZA_BASE);
		
	}

}
