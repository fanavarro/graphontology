package es.um.dis.ontology_metrics.owlgraphs;

import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.github.fanavarro.graphlib.AbstractGraph;

public abstract class OWLGraph<E> extends AbstractGraph<OWLClass, E>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7920832353678460851L;
	private OWLOntology ontology;
	private OWLReasoner reasoner;
	private boolean includeImportsClosure;
	
	public OWLGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory){
		this(ontology, reasonerFactory, false);
	}
	public OWLGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure){
		this(reasonerFactory.createNonBufferingReasoner(ontology), includeImportsClosure);
	}
	public OWLGraph(OWLReasoner reasoner, boolean includeImportsClosure) {
		this.ontology = reasoner.getRootOntology();
		this.reasoner = reasoner;
		this.includeImportsClosure = includeImportsClosure;
	}
	public OWLGraph(OWLReasoner reasoner) {
		this(reasoner, false);
	}
	@Override
	public Set<OWLClass> getNodes() {
		return ontology.classesInSignature(Imports.fromBoolean(includeImportsClosure)).collect(Collectors.toSet());
	}
	
	public OWLOntology getOntology(){
		return ontology;
	}
	
	public OWLReasoner getReasoner(){
		return reasoner;
	}
	
	public boolean getIncludeImportsClosure(){
		return includeImportsClosure;
	}

}
