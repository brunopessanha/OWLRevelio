package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class SysMLNode {

    protected String tag;

    protected String id;

    protected String name;

    protected String type;

    protected String getAttributeValue(NamedNodeMap attributes, String attributeId) {
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
}
