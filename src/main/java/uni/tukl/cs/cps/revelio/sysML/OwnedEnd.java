package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class OwnedEnd extends SysMLNode {

    private String aggregation;

    private String xmiType;

    private UpperValue upperValue;

    private LowerValue lowerValue;

    public OwnedEnd(NamedNodeMap attributes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(attributes, Enums.XML_Attribute.Type.toString());
        this.aggregation = getAttributeValue(attributes, Enums.XML_Attribute.Aggregation.toString());
        this.xmiType = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
    }

    public OwnedEnd(Node node) {
        this(node.getAttributes());
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node child = node.getChildNodes().item(i);
            if (child.getNodeName().equals(Enums.XML_Tag.UpperValue.toString())) {
                this.upperValue = new UpperValue(child.getAttributes());
            } else if (child.getNodeName().equals(Enums.XML_Tag.LowerValue.toString())) {
                this.lowerValue = new LowerValue(child.getAttributes());
            }
        }
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

    public LowerValue getLowerValue() {
        return lowerValue;
    }

    public void setLowerValue(LowerValue lowerValue) {
        this.lowerValue = lowerValue;
    }

    public UpperValue getUpperValue() {
        return upperValue;
    }

    public void setUpperValue(UpperValue upperValue) {
        this.upperValue = upperValue;
    }
}
