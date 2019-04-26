package uni.tukl.cs.cps.revelio.sysML;

public class SysMLTag  {

    private String base;

    private String tagName;

    public SysMLTag (String tagName, String base) {
        this.tagName = tagName;
        this.base = base;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
