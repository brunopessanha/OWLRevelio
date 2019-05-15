package com.github.brunopessanha.revelio.sysML;

public class SysMLTag  {

    private String base;

    private String tagName;

    private String direction;

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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
