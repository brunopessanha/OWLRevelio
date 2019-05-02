package uni.tukl.cs.cps.revelio.parser;

import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.sysML.Association;
import uni.tukl.cs.cps.revelio.sysML.Block;
import uni.tukl.cs.cps.revelio.sysML.ParticipantProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface SysML2OWLParser {

    Stream<OWLClassAxiom> classAxioms();

    Stream<OWLObjectPropertyAxiom> objectPropertyAxioms();

    Stream<OWLDataPropertyAxiom> dataPropertyAxioms();

    Stream<OWLIndividualAxiom> individualAxioms();

    Map<String, Block> getBlockMap();

    Map<String, ParticipantProperty> getPropertyMap();

    List<Association> getAssociations();
}
