package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uni.tukl.cs.cps.revelio.exceptions.InvalidSysMLFileException;
import uni.tukl.cs.cps.revelio.owl.OntologyManager;
import uni.tukl.cs.cps.revelio.parser.Enums;
import uni.tukl.cs.cps.revelio.parser.SysML2OWLParser;
import uni.tukl.cs.cps.revelio.sysML.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Revelio implements SysML2OWLParser {

    private Map<String, Block> blockMap;
    private List<Association> associations;
    private OntologyManager ontologyManager;

    public Revelio(String filePath, String ontologyPrefix, String rootClass) throws InvalidSysMLFileException {

        this.associations = new ArrayList<>();

        Document doc = null;

        try {
            File file = new File(filePath);

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = dBuilder.parse(file);
            if(!doc.getDocumentElement().getNodeName().equals(Enums.XML_Tag.XMI.toString())) {
                throw new InvalidSysMLFileException("The file provided is not a valid XMI file.");
            }

            parseBlockDiagram(doc, rootClass);
            this.ontologyManager = new OntologyManager(ontologyPrefix, rootClass, this);

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new InvalidSysMLFileException(ex);
        }
    }

    private void parseBlockDiagram(Document doc, String rootClass) {

        blockMap = parseNodesByTag(doc, Enums.XML_Tag.BlockDiagram.toString()).stream()
                .map(t -> new Block(t.getBase(), rootClass)).collect(Collectors.toMap(Block::getId, block -> block));

        NodeList packagedElements = doc.getElementsByTagName(Enums.XML_Tag.PackagedElement.toString());
        for (int i = 0; i < packagedElements.getLength(); i++) {

            PackagedElement packagedElement = new PackagedElement(packagedElements.item(i).getAttributes(), packagedElements.item(i).getChildNodes());

            if (packagedElement.getType().equals(Enums.XMI_Type.UML_Class.toString()) && blockMap.containsKey(packagedElement.getId())) {
                parseBlock(packagedElement);

            } else if (packagedElement.getType().equals(Enums.XMI_Type.UML_Association.toString())) {
                associations.addAll(parseAssociation(packagedElement));
            }
        }
    }

    private void parseBlock(PackagedElement packagedElement) {
        Block block = blockMap.get(packagedElement.getId());
        block.setName(packagedElement.getName());

        for (int i = 0; i < packagedElement.getChildNodes().getLength(); i++) {

            SysMLNode childNode = new SysMLNode(packagedElement.getChildNodes().item(i).getAttributes());

            if (childNode.getXmiType() != null) {

                if (childNode.getXmiType().equals(Enums.XMI_Type.UML_Property.toString())) {

                    block.getAttributes().add(new OwnedAttribute(childNode, packagedElement.getChildNodes().item(i).getChildNodes()));

                } else if (childNode.getXmiType().equals(Enums.XMI_Type.UML_Generalization.toString())) {

                    Generalization generalization = new Generalization(childNode);
                    generalization.setGeneral(packagedElement.getChildNodes().item(i).getAttributes().getNamedItem(Enums.XML_Attribute.General.toString()).getNodeValue());
                    block.setSuperClass(blockMap.get(generalization.getGeneral()).getName());

                }  else if (childNode.getXmiType().equals(Enums.XMI_Type.UML_Comment.toString())) {

                    block.getComments().add(new OwnedComment(childNode, packagedElement.getChildNodes().item(i).getChildNodes()));

                } else if (childNode.getXmiType().equals(Enums.XMI_Type.UML_Connector.toString())) {

                    block.getConnectors().add(new OwnedConnector(packagedElement.getChildNodes().item(i).getChildNodes()));
                }
            }
        }
    }

    private List<SysMLTag> parseNodesByTag(Document doc, String tagName) {

        NodeList nodeBlocks = doc.getElementsByTagName(tagName);
        List<SysMLTag> tags = new ArrayList<>();

        for (int i = 0; i < nodeBlocks.getLength(); i++) {
            SysMLTag tag = null;

            if (tagName.equals(Enums.XML_Tag.BlockDiagram.toString())) {
                tag = new SysMLTag(tagName, nodeBlocks.item(i).getAttributes().getNamedItem(Enums.XML_Attribute.BaseClass.toString()).getNodeValue());
            } else if (tagName.equals(Enums.XML_Tag.ParticipantProperty.toString())) {
                tag = new SysMLTag(tagName, nodeBlocks.item(i).getAttributes().getNamedItem(Enums.XML_Attribute.BaseProperty.toString()).getNodeValue());
            }

            if (tag != null) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private List<Association> parseAssociation(PackagedElement packagedElement) {
        OwnedEnd owner = null;
        OwnedEnd owned = null;

        List<Association> umlAssociations = new ArrayList<>();

        boolean foundPart = false;
        for (int i = 0; i < packagedElement.getChildNodes().getLength() && owner == null; i++) {

            if (packagedElement.getChildNodes().item(i).getNodeName().equals(Enums.XML_Tag.OwnedEnd.toString())) {

                if (!foundPart) {
                    foundPart = true;
                    owned = new OwnedEnd(packagedElement.getChildNodes().item(i));
                } else {
                    owner = new OwnedEnd(packagedElement.getChildNodes().item(i));
                    Association association = new Association(packagedElement, owner, owned);
                    umlAssociations.add(association);
                }
            }
        }

        return umlAssociations;
    }

    public Map<String, Block> getBlockMap(){
        return blockMap;
    }

    public List<Association> getAssociations() {
        return associations;
    }

    @Override
    public Stream<OWLClassAxiom> classAxioms() {
       return ontologyManager.classAxioms();
    }

    @Override
    public Stream<OWLObjectPropertyAxiom> objectPropertyAxioms() {
        return ontologyManager.objectPropertyAxioms();
    }

    @Override
    public Stream<OWLDataPropertyAxiom> dataPropertyAxioms() {
        return ontologyManager.dataPropertyAxioms();
    }

    @Override
    public Stream<OWLIndividualAxiom> individualAxioms() {
        return ontologyManager.individualAxioms();
    }

    @Override
    public Stream<OWLAnnotationAxiom> annotationAxioms() {
        return ontologyManager.annotationAxioms();
    }

    @Override
    public Stream<OWLAxiom> axioms() {
        return ontologyManager.axioms();
    }

    public void saveOntology(File file) throws OWLOntologyCreationException, OWLOntologyStorageException {
        ontologyManager.saveOntology(file);
    }
}
