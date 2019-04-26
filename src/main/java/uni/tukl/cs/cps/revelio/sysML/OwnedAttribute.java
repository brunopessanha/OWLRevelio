package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.NamedNodeMap;

public class OwnedAttribute extends SysMLNode {

    private String xmiType;

    public OwnedAttribute(NamedNodeMap attributes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.xmiType = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
    }

    public String getXmiType() {
        return xmiType;
    }

    public void setXmiType(String xmiType) {
        this.xmiType = xmiType;
    }
}
