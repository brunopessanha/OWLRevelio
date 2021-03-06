package com.github.brunopessanha.revelio.sysML;

import com.github.brunopessanha.revelio.parser.Enums;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SysMLNode {

    protected String tag;

    protected String id;

    protected String name;

    protected String type;

    protected String xmiType;

    public SysMLNode() {

    }

    public SysMLNode(NamedNodeMap attributes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(attributes, Enums.XML_Attribute.Type.toString());
        this.xmiType = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
    }

    String getAttributeValue(NamedNodeMap attributes, String attributeId) {
        if (attributes != null) {
            Node attribute = attributes.getNamedItem(attributeId);
            if (attribute != null) {
                return attribute.getNodeValue();
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getXmiType() {
        return xmiType;
    }

    public void setXmiType(String xmiType) {
        this.xmiType = xmiType;
    }
}
