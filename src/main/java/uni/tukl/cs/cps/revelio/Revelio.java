package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uni.tukl.cs.cps.revelio.exceptions.InvalidSysMLFileException;
import uni.tukl.cs.cps.revelio.sysML.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Revelio implements SysML2OWLParser {

    private final OWLDataFactory dataFactory;
    private final OWLOntologyManager ontologyManager;

    private String ontologyPrefix;
    private Map<String, Block> blockMap;
    private List<Association> associations;

    private List<OWLClass> classes;
    private List<OWLObjectProperty> objectProperties;
    private List<OWLDataProperty> dataProperties;
    private List<OWLIndividual> individuals;

    public Revelio(String filePath, String ontologyPrefix) throws InvalidSysMLFileException {

        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.dataFactory = ontologyManager.getOWLDataFactory();
        this.ontologyPrefix = ontologyPrefix;

        Document doc = null;

        try {
            File file = new File(filePath);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = dBuilder.parse(file);
            if(!doc.getDocumentElement().getNodeName().equals(Enums.XML_Tag.XMI.toString())) {
                throw new InvalidSysMLFileException("The file provided is not a valid XMI file.");
            }
            parseBlockDiagram(doc);

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new InvalidSysMLFileException(ex);
        }
    }

    private void parseBlockDiagram(Document doc) {

        blockMap = parseNodesByTag(doc, Enums.XML_Tag.BlockDiagram.toString()).stream()
                .map(t -> new Block(t.getBaseClass())).collect(Collectors.toMap(Block::getId, block -> block));

        NodeList packagedElements = doc.getElementsByTagName(Enums.XML_Tag.PackagedElement.toString());
        for (int i = 0; i < packagedElements.getLength(); i++) {

            PackagedElement packagedElement = new PackagedElement(packagedElements.item(i).getAttributes());

            if (packagedElement.getType().equals(Enums.XMI_Type.UML_Class.toString()) && blockMap.containsKey(packagedElement.getId())) {
                setBlockName(packagedElement);
            } else if (packagedElement.getType().equals(Enums.XMI_Type.UML_Association.toString())) {
                associations = parseAssociations(packagedElements.item(i).getChildNodes(), packagedElement);
            }
        }
    }

    private void setBlockName(PackagedElement packagedElement) {
        Block block = blockMap.get(packagedElement.getId());
        block.setName(packagedElement.getName());

        System.out.println("Block Name: " + block.getName());
    }

    private List<SysMLTag> parseNodesByTag(Document doc, String tagName) {
        NodeList nodeBlocks = doc.getElementsByTagName(tagName);
        List<SysMLTag> tags = new ArrayList<>();
        for (int i = 0; i < nodeBlocks.getLength(); i++) {
            SysMLTag tag = new SysMLTag(tagName, nodeBlocks.item(i).getAttributes().getNamedItem(Enums.XML_Attribute.BaseClass.toString()).getNodeValue());
            tags.add(tag);
        }
        return tags;
    }

    private List<Association> parseAssociations(NodeList ownedEnds, PackagedElement packagedElement) {
        OwnedEnd owner = null;
        OwnedEnd owned = null;

        List<Association> umlAssociations = new ArrayList<>();

        boolean foundPart = false;
        for (int j = 0; j < ownedEnds.getLength() && owner == null; j++) {
            if (ownedEnds.item(j).getNodeName().equals(Enums.XML_Tag.OwnedEnd.toString())) {
                if (!foundPart) {
                    foundPart = true;
                    owned = new OwnedEnd(ownedEnds.item(j).getAttributes());
                } else {
                    owner = new OwnedEnd(ownedEnds.item(j).getAttributes());
                    Association association = new Association(packagedElement, owner, owned);
                    umlAssociations.add(association);

                    System.out.println("Association (" + association.getOwned().getAggregation() + "): " + association.getName() + " -> "
                            + blockMap.get(association.getOwner().getType()).getName() + " hasPart " + blockMap.get(association.getOwned().getType()).getName());
                }
            }
        }

        return umlAssociations;
    }

    @Override
    public List<OWLClass> GetClasses() {
        if (classes == null) {
            classes = new ArrayList<>();
            for (Block block: blockMap.values()) {
                IRI iri = IRI.create(ontologyPrefix, block.getName());
                OWLClass owlClass = dataFactory.getOWLClass(iri);
                classes.add(owlClass);
            }
        }
        return classes;
    }

    @Override
    public List<OWLObjectProperty> GetObjectProperties() {
        return objectProperties;
    }

    @Override
    public List<OWLDataProperty> GetDataProperties() {
        return dataProperties;
    }

    @Override
    public List<OWLIndividual> GetIndividuals() {
        return individuals;
    }
}
