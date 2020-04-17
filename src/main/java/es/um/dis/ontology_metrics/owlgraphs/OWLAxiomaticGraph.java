package es.um.dis.ontology_metrics.owlgraphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.github.fanavarro.graphlib.algorithms.shortest_path.PathNode;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathAlgorithm;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathInput;
import com.github.fanavarro.graphlib.algorithms.shortest_path.ShortestPathOutput;

public class OWLAxiomaticGraph extends OWLGraph<OWLAxiom> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7770042274902855272L;

	private Collection<AxiomType<?>> ignoredAxioms;

	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory) {
		super(ontology, reasonerFactory);
		this.ignoredAxioms = new HashSet<>();
	}

	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure) {
		super(ontology, reasonerFactory, includeImportsClosure);
		this.ignoredAxioms = new HashSet<>();
	}

	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory,
			Collection<AxiomType<?>> ignoredAxioms) {
		super(ontology, reasonerFactory);
		this.ignoredAxioms = ignoredAxioms;
	}

	public OWLAxiomaticGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure,
			Collection<AxiomType<?>> ignoredAxioms) {
		super(ontology, reasonerFactory, includeImportsClosure);
		this.ignoredAxioms = ignoredAxioms;
	}

	public OWLAxiomaticGraph(OWLReasoner reasoner, boolean includeImportsClosure,
			Collection<AxiomType<?>> ignoredAxioms) {
		super(reasoner, includeImportsClosure);
		this.ignoredAxioms = ignoredAxioms;
	}

	@Override
	public Map<OWLAxiom, Set<OWLClass>> getAdjacentNodesWithEdges(OWLClass node) {
		Map<OWLAxiom, Set<OWLClass>> adjacentNodesWithEdges = new HashMap<>();
		EntitySearcher.getReferencingAxioms(node, getOntology(), Imports.fromBoolean(this.getIncludeImportsClosure()))
				.filter(axiom -> (!ignoreAxiom(axiom, node))).forEach(axiom -> {
					adjacentNodesWithEdges.putIfAbsent(axiom, new HashSet<OWLClass>());
					axiom.classesInSignature().filter(c -> (!node.equals(c))).forEach(c -> {
						adjacentNodesWithEdges.get(axiom).add(c);
					});
				});

		return adjacentNodesWithEdges;
	}

	private boolean ignoreAxiom(OWLAxiom axiom, OWLClass node) {
		if ((axiom instanceof OWLLogicalAxiom) && !ignoredAxioms.contains(axiom.getAxiomType())) {
			if (axiom instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom aux = (OWLSubClassOfAxiom) axiom;
				return !node.equals(aux.getSubClass());
			} else if (axiom instanceof OWLEquivalentClassesAxiom) {
				OWLEquivalentClassesAxiom aux = (OWLEquivalentClassesAxiom) axiom;
				OWLClassExpression firstClassExpression = aux.classExpressions().findFirst().get();
				return !node.equals(firstClassExpression);
			} else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
				OWLObjectPropertyDomainAxiom aux = (OWLObjectPropertyDomainAxiom) axiom;
				return !node.equals(aux.getDomain());
			}
		}
		return true;
	}

	public boolean existsPath(OWLClass a, OWLClass b) {
		ShortestPathAlgorithm<OWLClass, OWLAxiom> shortestPathAlgorithm = new ShortestPathAlgorithm<OWLClass, OWLAxiom>();
		ShortestPathInput<OWLClass, OWLAxiom> input = new ShortestPathInput<OWLClass, OWLAxiom>();
		input.setGraph(this);
		input.setMaxDepth(-1);
		input.setSourceNode(a);
		input.setTargetNode(b);
		ShortestPathOutput<OWLClass, OWLAxiom> output = shortestPathAlgorithm.apply(input);
		List<PathNode<OWLClass, OWLAxiom>> path = output.getPath();
		return path != null && !path.isEmpty();
	}

	public boolean isAxiomaticallyRelated(OWLClass a, OWLClass b) {
		return existsPath(a, b) || existsPath(b, a);
	}

}
