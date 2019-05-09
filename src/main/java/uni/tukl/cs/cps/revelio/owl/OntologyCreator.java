package uni.tukl.cs.cps.revelio.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.parser.ISysMLParser;
import uni.tukl.cs.cps.revelio.sysML.*;
import uni.tukl.cs.cps.revelio.parser.Enums;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OntologyCreator implements IOntologyCreator {

    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory dataFactory;

    private final List<OWLClassAxiom> classAxioms;
    private final List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private final List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private final List<OWLIndividualAxiom> individualAxioms;
    private final List<OWLAnnotationAxiom> annotationAxioms;

    private final Map<String, OWLIndividual> individuals;

    private final String portClass;
    private final String connectionClass;
    private final String ontologyIRI;

    private final ISysMLParser parser;

    public OntologyCreator(String ontologyIRI, String portClass, String connectionClass, ISysMLParser parser) {
        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.ontologyIRI = ontologyIRI;
        this.portClass = portClass;
        this.connectionClass = connectionClass;
        this.parser = parser;
        this.classAxioms = new ArrayList<>();
        this.objectPropertyAxioms = new ArrayList<>();
        this.dataPropertyAxioms = new ArrayList<>();
        this.individualAxioms = new ArrayList<>();
        this.annotationAxioms = new ArrayList<>();
        this.individuals = new HashMap<>();
        this.dataFactory = ontologyManager.getOWLDataFactory();
    }

    private OWLObjectProperty getHasPartRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.HasPart.toString()));
    }

    private OWLObjectProperty getHasPortRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.HasPort.toString()));
    }

    private OWLObjectProperty getIsConnectedToRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.IsConnectedTo.toString()));
    }

    private IRI getDataPropertyIRI(String name) {
        return this.getIRI("has" + name);
    }

    private IRI getIRI(String name) {
        return IRI.create(ontologyIRI, name);
    }

    private OWLClass getOWLClass(String name) {
        return dataFactory.getOWLClass(getIRI(name));
    }

    private OWLAnnotationAxiom getCommentAxiom(String comment, IRI iri) {

        OWLLiteral literal = dataFactory.getOWLLiteral(comment);
        OWLAnnotationProperty commentAnnotation = dataFactory.getRDFSComment();

        OWLAnnotation annotation = dataFactory.getOWLAnnotation(commentAnnotation, literal);
        OWLAnnotationAxiom commentAxiom = dataFactory.getOWLAnnotationAssertionAxiom(iri, annotation);

        return commentAxiom;
    }

    public void generateAxioms() {
        for (Block block : parser.getBlockMap().values()) {

            OWLClass owlBlockClass = getOWLClass(block.getName());
            OWLClass owlParentClass = getOWLClass(block.getSuperClass());

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlBlockClass, owlParentClass);

            classAxioms.add(axiom);

            addCommentsAxioms(block.getComments(), owlBlockClass.getIRI());

            addBlockAttributesAxioms(block.getAttributes(), owlBlockClass);

            addBlockConnectorsAxioms(block.getConnectors());

            addBlockPortsAxioms(block.getPorts(), owlBlockClass);
        }

        for (Association association : parser.getAssociations()) {

            OWLClass owlOwnerClass = getOWLClass(parser.getBlockMap().get(association.getOwner().getType()).getName());
            OWLClass owlOwnedClass = getOWLClass(parser.getBlockMap().get(association.getOwned().getType()).getName());

            OWLClassExpression classExpression = getOWLClassExpression(association, owlOwnedClass);

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlOwnerClass, classExpression);

            classAxioms.add(axiom);
        }
    }

    private void addCommentsAxioms(List<OwnedComment> comments, IRI iri) {
        for (OwnedComment comment : comments) {
            annotationAxioms.add(getCommentAxiom(comment.getBody(), iri));
        }
    }

    private void addBlockAttributesAxioms(List<OwnedAttribute> attributes, OWLClass blockClass) {
        for (OwnedAttribute attribute : attributes) {
            if (attribute.getDataType() != null) { // data property attribute

                OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(getDataPropertyIRI(attribute.getName()));

                OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, attribute.getDataType().getDatatype(dataFactory));

                dataPropertyAxioms.add(rangeAxiom);

                OWLClassExpression classExpression = dataFactory.getOWLDataSomeValuesFrom(dataProperty, attribute.getDataType());
                OWLClassAxiom dataPropertyRestrictionAxiom = dataFactory.getOWLSubClassOfAxiom(blockClass, classExpression);

                classAxioms.add(dataPropertyRestrictionAxiom);
            } else { //instance attribute of internal block diagram

                OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(getIRI(attribute.getName()));
                OWLClass individualType = getOWLClass(parser.getBlockMap().get(attribute.getType()).getName());

                individuals.put(attribute.getId(), individual);

                individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(individualType, individual));

                addCommentsAxioms(attribute.getComments(), individual.getIRI());
            }
        }
    }

    private void addBlockPortsAxioms(List<Port> ports, OWLClass block) {
        for (Port port : ports) {
            OWLClass owlParentClass = getOWLClass(port.getSuperClass().toString());
            OWLClass owlPortClass = getOWLClass(portClass);
            OWLClassExpression classExpression = dataFactory.getOWLObjectSomeValuesFrom(getHasPortRelation(), owlPortClass);

            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(owlParentClass, owlPortClass));
            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(block, classExpression));
        }
    }

    private OWLIndividual addHasPortAxiom(End end) {
        Port port = parser.getPortMap().get(end.getRole());

        OWLIndividual partWithPort = individuals.get(end.getPartWithPort());
        OWLIndividual portIndividual = dataFactory.getOWLNamedIndividual(port.getName());
        OWLClass portType = getOWLClass(port.getSuperClass().toString());

        if (port.getSuperClass() == Enums.Port.FlowPort) {
            OWLClass parentPortType = portType;

            if (Enums.FlowPortDirection.In.toString().equals(port.getDirection())) {
                portType = getOWLClass(Enums.FlowPort.InputFlowPort.toString());
            } else if (Enums.FlowPortDirection.Out.toString().equals(port.getDirection())) {
                portType = getOWLClass(Enums.FlowPort.OutputFlowPort.toString());
            } else {
                portType = getOWLClass(Enums.FlowPort.InputOutputFlowPort.toString());
            }

            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(portType, parentPortType));
        }

        individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(portType, portIndividual));
        individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getHasPortRelation(), partWithPort, portIndividual));

        return portIndividual;
    }

    private void addBlockConnectorsAxioms(List<OwnedConnector> connectors) {
        for (OwnedConnector connector : connectors) {
            if (connector.getFirstEnd().getPartWithPort() != null && connector.getSecondEnd().getPartWithPort() != null) {

                OWLIndividual firstPort = addHasPortAxiom(connector.getFirstEnd());
                OWLIndividual secondPort = addHasPortAxiom(connector.getSecondEnd());
                OWLIndividual connection = dataFactory.getOWLNamedIndividual(getConnectionName(connector));

                individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(getOWLClass(connectionClass), connection));
                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getIsConnectedToRelation(), connection, firstPort));
                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getIsConnectedToRelation(), connection, secondPort));

            } else {

                OWLIndividual firstIndividual = individuals.get(connector.getFirstEnd().getRole());
                OWLIndividual secondIndividual = individuals.get(connector.getSecondEnd().getRole());

                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getHasPartRelation(), firstIndividual, secondIndividual));
            }
        }
    }

    private String getConnectionName(OwnedConnector connector) {
        return parser.getPortMap().get(connector.getFirstEnd().getRole()).getName()
                + "_" +
                parser.getPortMap().get(connector.getSecondEnd().getRole()).getName();
    }

    private OWLClassExpression getOWLClassExpression(Association association, OWLClass owlOwnedClass) {
        OWLClassExpression classExpression;

        if (association.hasExactCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getLowerValue().getValue());
            classExpression = dataFactory.getOWLObjectExactCardinality(cardinality, getHasPartRelation(), owlOwnedClass);

        } else if (association.hasMinCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getLowerValue().getValue());
            classExpression = dataFactory.getOWLObjectMinCardinality(cardinality, getHasPartRelation(), owlOwnedClass);

        } else if (association.hasMaxCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getUpperValue().getValue());
            classExpression = dataFactory.getOWLObjectMaxCardinality(cardinality, getHasPartRelation(), owlOwnedClass);

        } else {

            classExpression = dataFactory.getOWLObjectSomeValuesFrom(getHasPartRelation(), owlOwnedClass);
        }

        return classExpression;
    }

    public Stream<OWLClassAxiom> classAxioms() {
        return classAxioms.stream();
    }

    public Stream<OWLObjectPropertyAxiom> objectPropertyAxioms() {
        return objectPropertyAxioms.stream();
    }

    public Stream<OWLDataPropertyAxiom> dataPropertyAxioms() {
        return dataPropertyAxioms.stream();
    }

    public Stream<OWLIndividualAxiom> individualAxioms() {
        return individualAxioms.stream();
    }

    public Stream<OWLAnnotationAxiom> annotationAxioms() {
        return annotationAxioms.stream();
    }

    public Stream<OWLAxiom> axioms() {

        List<OWLAxiom> axioms = new ArrayList<>();

        axioms.addAll(classAxioms);
        axioms.addAll(objectPropertyAxioms);
        axioms.addAll(dataPropertyAxioms);
        axioms.addAll(individualAxioms);
        axioms.addAll(annotationAxioms);

        return axioms.stream();
    }

    public void saveOntology(File file) throws OWLOntologyStorageException, OWLOntologyCreationException {
        OWLOntology ontology = ontologyManager.createOntology(IRI.create(file.toURI()));
        ontology.addAxioms(axioms());
        ontologyManager.saveOntology(ontology);
    }
}
