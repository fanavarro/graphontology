package es.um.dis.ontology_metrics.owlgraphs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.um.dis.graphlib.algorithms.Algorithm;
import es.um.dis.graphlib.algorithms.least_common_node.LeastCommonNodeAlgorithm;
import es.um.dis.graphlib.algorithms.least_common_node.LeastCommonNodeInput;
import es.um.dis.graphlib.algorithms.least_common_node.LeastCommonNodeOutput;
import es.um.dis.graphlib.algorithms.shortest_path.ShortestPathAlgorithm;
import es.um.dis.graphlib.algorithms.shortest_path.ShortestPathInput;
import es.um.dis.graphlib.algorithms.shortest_path.ShortestPathOutput;

public class OWLTaxonomicGraph extends OWLGraph {

	public OWLTaxonomicGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
	}

	public OWLTaxonomicGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
	}

	@Override
	public Map<AxiomType<?>, Set<OWLClass>> getAdjacentNodesWithEdges(OWLClass node) {
		Map <AxiomType<?>, Set<OWLClass>> adjacentClasses = new HashMap<AxiomType<?>, Set<OWLClass>>();
		adjacentClasses.put(AxiomType.SUBCLASS_OF, getReasoner().superClasses(node, true).collect(Collectors.toSet()));
		return adjacentClasses;
	}
	
	public double getTaxonomicSimilarity(OWLClass a, OWLClass b){
		OWLClass root = getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLThing();
		OWLClass leastCommonAncestor = this.getLeastCommonAncestor(a, b, root);
	
		int distRootLeastCommonAncestor = getMinPathDistance(leastCommonAncestor, root);
		int distALeastCommonAncestor = getMinPathDistance(a, leastCommonAncestor);
		int distBLeastCommonAncestor = getMinPathDistance(b, leastCommonAncestor);
		return (double)distRootLeastCommonAncestor/(distRootLeastCommonAncestor + distALeastCommonAncestor + distBLeastCommonAncestor);
	}

	private OWLClass getLeastCommonAncestor(OWLClass a, OWLClass b, OWLClass root) {
		OWLClass leastCommonAncestor = null;
		LeastCommonNodeInput<OWLClass, AxiomType<?>> leastCommonNodeInput = new LeastCommonNodeInput<OWLClass, AxiomType<?>>();
		leastCommonNodeInput.setGraph(this);
		leastCommonNodeInput.setNode1(a);
		leastCommonNodeInput.setNode2(b);
		
		Algorithm<OWLClass, AxiomType<?>> leastCommonNodeAlgorithm = new LeastCommonNodeAlgorithm<OWLClass, AxiomType<?>>();
		LeastCommonNodeOutput<OWLClass, AxiomType<?>> leastCommonNodeOutput = (LeastCommonNodeOutput<OWLClass, AxiomType<?>>) this.applyAlgorithm(leastCommonNodeAlgorithm, leastCommonNodeInput);
		if(leastCommonNodeOutput.getLeastCommonNodes() != null && !leastCommonNodeOutput.getLeastCommonNodes().isEmpty()){
			leastCommonAncestor = leastCommonNodeOutput.getLeastCommonNodes().iterator().next();
		}
		leastCommonAncestor = leastCommonAncestor == null ? root:leastCommonAncestor;
		return leastCommonAncestor;
	}
	private int getMinPathDistance(OWLClass a, OWLClass b){
		int dist1 = getPathDistance(a, b);
		int dist2 = getPathDistance(b, a);
		if(dist1 == -1){
			return dist2;
		}
		if(dist2 == -1){
			return dist1;
		}
		
		return Math.min(dist1, dist2);
	}
	private int getPathDistance(OWLClass a, OWLClass b){
		int distance = -1;
		Algorithm<OWLClass, AxiomType<?>> shortestPathAlgorithm = new ShortestPathAlgorithm<OWLClass, AxiomType<?>>();
		ShortestPathInput<OWLClass, AxiomType<?>> input = new ShortestPathInput<OWLClass, AxiomType<?>>();
		input.setGraph(this);
		input.setSourceNode(a);
		input.setTargetNode(b);
		ShortestPathOutput<OWLClass, AxiomType<?>> output = (ShortestPathOutput<OWLClass, AxiomType<?>>) this.applyAlgorithm(shortestPathAlgorithm, input);
		if(output.getPath() != null){
			distance = output.getPath().size();
		}
		return distance;
	}
	

}
