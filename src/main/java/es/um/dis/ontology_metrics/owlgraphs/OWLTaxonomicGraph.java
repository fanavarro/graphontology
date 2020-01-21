package es.um.dis.ontology_metrics.owlgraphs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
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
	public Map<IRI, Set<OWLClass>> getAdjacentNodesWithEdges(OWLClass node) {
		Map <IRI, Set<OWLClass>> adjacentClasses = new HashMap<IRI, Set<OWLClass>>();
		adjacentClasses.put(OWLGraphVocabulary.RDFS_SUBCLASS_OF, getReasoner().superClasses(node, true).collect(Collectors.toSet()));
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
		LeastCommonNodeInput<OWLClass, IRI> leastCommonNodeInput = new LeastCommonNodeInput<OWLClass, IRI>();
		leastCommonNodeInput.setGraph(this);
		leastCommonNodeInput.setNode1(a);
		leastCommonNodeInput.setNode2(b);
		
		Algorithm<OWLClass, IRI> leastCommonNodeAlgorithm = new LeastCommonNodeAlgorithm<OWLClass, IRI>();
		LeastCommonNodeOutput<OWLClass, IRI> leastCommonNodeOutput = (LeastCommonNodeOutput<OWLClass, IRI>) this.applyAlgorithm(leastCommonNodeAlgorithm, leastCommonNodeInput);
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
		Algorithm<OWLClass, IRI> shortestPathAlgorithm = new ShortestPathAlgorithm<OWLClass, IRI>();
		ShortestPathInput<OWLClass, IRI> input = new ShortestPathInput<OWLClass, IRI>();
		input.setGraph(this);
		input.setSourceNode(a);
		input.setTargetNode(b);
		ShortestPathOutput<OWLClass, IRI> output = (ShortestPathOutput<OWLClass, IRI>) this.applyAlgorithm(shortestPathAlgorithm, input);
		if(output.getPath() != null){
			distance = output.getPath().size();
		}
		return distance;
	}
	

}
