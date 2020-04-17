package es.um.dis.ontology_metrics.owlgraphs;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLGraphVocabulary {
	public static IRI UMBEL_SUPER_CLASS_OF = IRI.create("http://umbel.org/umbel#superClassOf");
	public static IRI RDFS_SUBCLASS_OF = OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI();
	public static IRI RDFS_LABEL = OWLRDFVocabulary.RDFS_LABEL.getIRI();
}
