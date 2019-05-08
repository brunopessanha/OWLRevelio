package uni.tukl.cs.cps.revelio.sysML;

import java.util.ArrayList;
import java.util.List;

public class Block extends SysMLNode {

    private List<OwnedAttribute> attributes;

    private List<OwnedComment> comments;

    private List<OwnedConnector> connectors;

    private String superClass;

    public Block(String id, String superClass) {
        this.id = id;
        this.superClass = superClass;
        this.attributes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.connectors = new ArrayList<>();
    }

    public List<OwnedAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OwnedAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public List<OwnedComment> getComments() {
        return comments;
    }

    public void setComments(List<OwnedComment> comments) {
        this.comments = comments;
    }

    public List<OwnedConnector> getConnectors() {
        return connectors;
    }

    public void setConnectors(List<OwnedConnector> connectors) {
        this.connectors = connectors;
    }
}
