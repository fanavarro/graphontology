package es.um.dis.ontology_metrics.owlgraphs;

import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import es.um.dis.graphlib.Graph;

public abstract class OWLGraph extends Graph<OWLClass, IRI>{
	private OWLOntology ontology;
	private OWLReasoner reasoner;
	private boolean includeImportsClosure;
	
	public OWLGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory){
		this(ontology, reasonerFactory, false);
	}
	public OWLGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure){
		this.ontology = ontology;
		this.includeImportsClosure = includeImportsClosure;
		reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
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
