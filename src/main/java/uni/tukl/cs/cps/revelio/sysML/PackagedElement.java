package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import uni.tukl.cs.cps.revelio.parser.Enums;

import java.util.ArrayList;
import java.util.List;

public class PackagedElement extends SysMLNode {

    private List<OwnedAttribute> attributeList;

    private NodeList childNodes;

    public PackagedElement(NamedNodeMap attributes, NodeList childNodes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
        this.attributeList = new ArrayList<>();
        this.childNodes = childNodes;
    }

    public List<OwnedAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<OwnedAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public NodeList getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(NodeList childNodes) {
        this.childNodes = childNodes;
    }
}
