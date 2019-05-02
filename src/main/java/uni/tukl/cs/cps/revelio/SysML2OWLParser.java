package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.model.*;

import java.util.stream.Stream;

interface SysML2OWLParser {

    Stream<OWLClassAxiom> classAxioms();

    Stream<OWLObjectPropertyAxiom> objectPropertyAxioms();

    Stream<OWLDataPropertyAxiom> dataPropertyAxioms();

    Stream<OWLIndividualAxiom> individualAxioms();

}
