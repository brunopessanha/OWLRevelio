package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.model.*;

import java.util.List;

interface SysML2OWLParser {

    List<OWLClass> GetClasses();
    List<OWLObjectProperty> GetObjectProperties();
    List<OWLDataProperty> GetDataProperties();
    List<OWLIndividual> GetIndividuals();

}
