package uni.tukl.cs.cps.revelio.sysML;

public abstract class Value extends SysMLNode {

    protected String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
