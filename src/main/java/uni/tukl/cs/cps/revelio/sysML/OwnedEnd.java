package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.NamedNodeMap;

public class OwnedEnd extends SysMLNode {

    private String aggregation;

    private String xmiType;

    public OwnedEnd(NamedNodeMap attributes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(attributes, Enums.XML_Attribute.Type.toString());
        this.aggregation = getAttributeValue(attributes, Enums.XML_Attribute.Aggregation.toString());
        this.xmiType = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
    }

    public String getXmiType() {
        return xmiType;
    }

    public void setXmiType(String xmiType) {
        this.xmiType = xmiType;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }
}
