package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.NamedNodeMap;

public class PackagedElement extends SysMLNode {

    public PackagedElement(NamedNodeMap attributes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
    }

}
