package com.github.brunopessanha.revelio.sysML;

import com.github.brunopessanha.revelio.parser.Enums;
import org.w3c.dom.NamedNodeMap;

public class LowerValue extends Value {

    LowerValue(NamedNodeMap attributes) {
        this.id = getAttributeValue(attributes, Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(attributes, Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(attributes, Enums.XML_Attribute.Type.toString());
        this.value = getAttributeValue(attributes, Enums.XML_Attribute.Value.toString());
        this.xmiType = getAttributeValue(attributes, Enums.XML_Attribute.XMI_Type.toString());
    }
}
