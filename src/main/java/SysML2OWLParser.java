import org.semanticweb.owlapi.model.*;

import java.util.List;

interface SysML2OWLParser {

    List<OWLClassAxiom> GetClassAxioms();
    List<OWLObjectPropertyAxiom> GetObjectPropertyAxioms();
    List<OWLDataPropertyAxiom> GetDataPropertyAxioms();
    List<OWLIndividualAxiom> GetIndividualAxioms();

}
