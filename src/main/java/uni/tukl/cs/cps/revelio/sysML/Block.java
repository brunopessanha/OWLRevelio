package uni.tukl.cs.cps.revelio.sysML;

import java.util.ArrayList;
import java.util.List;

public class Block extends SysMLNode {

    private List<OwnedAttribute> attributes;

    public Block(String id) {
        this.id = id;
        this.attributes = new ArrayList<>();
    }

    public List<OwnedAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OwnedAttribute> attributes) {
        this.attributes = attributes;
    }
}
