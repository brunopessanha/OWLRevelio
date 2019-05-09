package uni.tukl.cs.cps.revelio.owl;

import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.stream.Stream;

public interface IOntologyCreator {

    Stream<OWLClassAxiom> classAxioms();

    Stream<OWLObjectPropertyAxiom> objectPropertyAxioms();

    Stream<OWLDataPropertyAxiom> dataPropertyAxioms();

    Stream<OWLIndividualAxiom> individualAxioms();

    Stream<OWLAnnotationAxiom> annotationAxioms();

    Stream<OWLAxiom> axioms();

    void generateAxioms();

    void saveOntology(File file) throws OWLOntologyStorageException, OWLOntologyCreationException;

}
