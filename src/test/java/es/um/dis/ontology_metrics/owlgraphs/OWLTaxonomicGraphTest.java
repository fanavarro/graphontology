package es.um.dis.ontology_metrics.owlgraphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.github.fanavarro.graphlib.algorithms.Algorithm;
import com.github.fanavarro.graphlib.algorithms.least_common_node.LeastCommonNodeAlgorithm;
import com.github.fanavarro.graphlib.algorithms.least_common_node.LeastCommonNodeInput;
import com.github.fanavarro.graphlib.algorithms.least_common_node.LeastCommonNodeOutput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathAlgorithm;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathInput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathOutput;

public class OWLTaxonomicGraphTest {
	private static final double DELTA = 0.001;
	private static final String PIZZA_ONTOLOGY_PATH = "/ontologies/pizza.owl";
	private static final String NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
	private static final IRI CHEESEY_PIZZA = IRI.create(NS + "CheeseyPizza");
	private static final IRI AMERICAN_PIZZA = IRI.create(NS + "American");
	private static final IRI ICE_CREAM = IRI.create(NS + "IceCream");
	private static final IRI FOOD = IRI.create(NS + "Food");
	private static final IRI SPICENESS = IRI.create(NS + "Spiceness");

	@Test
	public void testShortestPath1() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLTaxonomicGraph graph = new OWLTaxonomicGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass cheesyPizza = manager.getOWLDataFactory().getOWLClass(CHEESEY_PIZZA);
		OWLClass food = manager.getOWLDataFactory().getOWLClass(FOOD);
		
		Algorithm<OWLClass, IRI> shortestPath = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setSourceNode(cheesyPizza);
		input.setTargetNode(food);
		
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) graph.applyAlgorithm(shortestPath, input);
		
		assertNotNull(output);
		assertNotNull(output.getPath());
		assertEquals(2, output.getPath().size());
		
		assertEquals(output.getPath().get(0).getSource().toStringID(), NS + "CheeseyPizza");
		assertEquals(1, output.getPath().get(0).getEdges().size());
		assertTrue(output.getPath().get(0).getEdges().contains(OWLGraphVocabulary.RDFS_SUBCLASS_OF));
		assertEquals(output.getPath().get(0).getTarget().toStringID(), NS + "Pizza");
		
		assertEquals(output.getPath().get(1).getSource().toStringID(), NS + "Pizza");
		assertEquals(1, output.getPath().get(1).getEdges().size());
		assertTrue(output.getPath().get(1).getEdges().contains(OWLGraphVocabulary.RDFS_SUBCLASS_OF));
		assertEquals(output.getPath().get(1).getTarget().toStringID(), NS + "Food");
	}
	
	@Test
	public void testShortestPath2() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLTaxonomicGraph graph = new OWLTaxonomicGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass cheesyPizza = manager.getOWLDataFactory().getOWLClass(CHEESEY_PIZZA);
		OWLClass iceCream = manager.getOWLDataFactory().getOWLClass(ICE_CREAM);
		
		Algorithm<OWLClass, IRI> shortestPath = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setSourceNode(cheesyPizza);
		input.setTargetNode(iceCream);
		
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) graph.applyAlgorithm(shortestPath, input);
		
		assertNotNull(output);
		assertNull(output.getPath());
	}
	
	@Test
	public void testCommonAncestor1() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLTaxonomicGraph graph = new OWLTaxonomicGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass americanPizza = manager.getOWLDataFactory().getOWLClass(AMERICAN_PIZZA);
		OWLClass iceCream = manager.getOWLDataFactory().getOWLClass(ICE_CREAM);
		OWLClass food = manager.getOWLDataFactory().getOWLClass(FOOD);
		
		Algorithm<OWLClass, IRI> lcaAlgorithm = new LeastCommonNodeAlgorithm<OWLClass, IRI>();
		LeastCommonNodeInput<OWLClass, IRI> input = new LeastCommonNodeInput<OWLClass, IRI>();
		input.setGraph(graph);
		input.setNodes(new HashSet<OWLClass>(Arrays.asList(americanPizza, iceCream)));
		
		LeastCommonNodeOutput<OWLClass, IRI> output = (LeastCommonNodeOutput<OWLClass, IRI>) graph.applyAlgorithm(lcaAlgorithm, input);
		
		assertNotNull(output);
		assertNotNull(output.getLeastCommonNodes());
		assertEquals(1, output.getLeastCommonNodes().size());
		assertTrue(output.getLeastCommonNodes().contains(food));
	}
	
	@Test
	public void testTaxonomicSimilarity1() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLTaxonomicGraph graph = new OWLTaxonomicGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass americanPizza = manager.getOWLDataFactory().getOWLClass(AMERICAN_PIZZA);
		OWLClass food = manager.getOWLDataFactory().getOWLClass(FOOD);
		
		assertEquals(2.0/(3.0+0.0+2.0) ,graph.getTaxonomicSimilarity(americanPizza, food), DELTA);
	}
	
	@Test
	public void testTaxonomicSimilarity2() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(PIZZA_ONTOLOGY_PATH));
		
		OWLTaxonomicGraph graph = new OWLTaxonomicGraph(ontology, new StructuralReasonerFactory());
		
		OWLClass americanPizza = manager.getOWLDataFactory().getOWLClass(AMERICAN_PIZZA);
		OWLClass spiceness = manager.getOWLDataFactory().getOWLClass(SPICENESS);
		
		assertEquals(0.0 ,graph.getTaxonomicSimilarity(americanPizza, spiceness), DELTA);
	}
	
}
