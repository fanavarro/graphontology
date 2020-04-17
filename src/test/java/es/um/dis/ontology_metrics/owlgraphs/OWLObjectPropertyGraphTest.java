package es.um.dis.ontology_metrics.owlgraphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

public class OWLObjectPropertyGraphTest {

	private static final String PIZZA_ONTOLOGY_PATH = "/ontologies/pizza.owl";
	private static final String NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
	private static final IRI SEAFOOD_TOPPING = IRI.create(NS + "SeafoodTopping");
	private static final IRI PIZZA = IRI.create(NS + "Pizza");
	private static final IRI PIZZA_BASE = IRI.create(NS + "PizzaBase");
	private static final IRI PIZZA_TOPPING = IRI.create(NS + "PizzaTopping");
	private static final IRI HAS_BASE = IRI.create(NS + "hasBase");
	private static final IRI IS_TOPPING_OF = IRI.create(NS + "isToppingOf");
	

	@Test
	public void testShortestPath1() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLObjectPropertyGraph graph = new OWLObjectPropertyGraph(ontology, new StructuralReasonerFactory());
		
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
		
		OWLObjectPropertyGraph graph = new OWLObjectPropertyGraph(ontology, new StructuralReasonerFactory());
		
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
		assertTrue(output.getPath().get(0).getEdges().contains(IS_TOPPING_OF)); // is topping of
		assertEquals(output.getPath().get(0).getTarget().toStringID(), NS + "Pizza");
		
		assertEquals(output.getPath().get(1).getSource().toStringID(), NS + "Pizza");
		assertEquals(1, output.getPath().get(1).getEdges().size());
		assertTrue(output.getPath().get(1).getEdges().contains(HAS_BASE)); // has base
		assertEquals(output.getPath().get(1).getTarget().toStringID(), NS + "PizzaBase");
		
	}
	
	@Test
	public void testShortestPath3() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLObjectPropertyGraph graph = new OWLObjectPropertyGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass seafoodTopping = manager.getOWLDataFactory().getOWLClass(SEAFOOD_TOPPING);
		OWLClass pizzaBase = manager.getOWLDataFactory().getOWLClass(PIZZA_BASE);
		
		Algorithm<OWLClass, IRI> shortestPath = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setSourceNode(seafoodTopping);
		input.setTargetNode(pizzaBase);
		
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) graph.applyAlgorithm(shortestPath, input);
		
		assertNotNull(output);
		assertNull(output.getPath());		
	}

}
