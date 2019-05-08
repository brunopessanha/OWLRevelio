package uni.tukl.cs.cps.revelio.parser;

import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.sysML.Association;
import uni.tukl.cs.cps.revelio.sysML.Block;
import uni.tukl.cs.cps.revelio.sysML.Port;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface ISysML2OWLParser {

    Stream<OWLClassAxiom> classAxioms();

    Stream<OWLObjectPropertyAxiom> objectPropertyAxioms();

    Stream<OWLAnnotationAxiom> annotationAxioms();

    Stream<OWLDataPropertyAxiom> dataPropertyAxioms();

    Stream<OWLIndividualAxiom> individualAxioms();

    Stream<OWLAxiom> axioms();

    Map<String, Block> getBlockMap();

    Map<String, Port> getPortMap();

    List<Association> getAssociations();
}
