package com.github.brunopessanha.revelio.sysML;

import java.util.ArrayList;
import java.util.List;

public class Block extends SysMLNode {

    private List<OwnedAttribute> attributes;

    private List<OwnedComment> comments;

    private List<OwnedConnector> connectors;

    private List<Port> ports;

    private String superClass;

    public Block(String id, String superClass) {
        this.id = id;
        this.superClass = superClass;
        this.attributes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.connectors = new ArrayList<>();
        this.ports = new ArrayList<>();
    }

    public List<OwnedAttribute> getAttributes() {
        return attributes;
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

    public List<OwnedConnector> getConnectors() {
        return connectors;
    }

    public List<Port> getPorts() {
        return ports;
    }
}
