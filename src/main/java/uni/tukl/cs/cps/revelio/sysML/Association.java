package uni.tukl.cs.cps.revelio.sysML;

import uni.tukl.cs.cps.revelio.parser.Enums;

public class Association extends SysMLNode {

    private OwnedEnd owner;

    private OwnedEnd owned;

    public Association() {

    }

    public Association(PackagedElement packagedElement, OwnedEnd owner, OwnedEnd owned) {
        this.id = packagedElement.getId();
        this.name = packagedElement.getName();
        this.owner = owner;
        this.owned = owned;
    }

    public OwnedEnd getOwner() {
        return owner;
    }

    public void setOwner(OwnedEnd owner) {
        this.owner = owner;
    }

    public OwnedEnd getOwned() {
        return owned;
    }

    public void setOwned(OwnedEnd owned) {
        this.owned = owned;
    }

    public boolean hasExactCardinalityRestriction() {
        if (owned == null || owned.getLowerValue() == null || owned.getUpperValue() == null)
            return  false;

        if ((owned.getUpperValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralInteger.toString()) || owned.getUpperValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralUnlimitedNatural.toString()))
                && (owned.getLowerValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralInteger.toString()) || owned.getLowerValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralUnlimitedNatural.toString()))) {
            try {
                return Integer.parseInt(owned.getLowerValue().getValue()) == Integer.parseInt(owned.getUpperValue().getValue());
            } catch (Exception ex) { }
        }

        return false;
    }

    public boolean hasMinCardinalityRestriction() {
        if (owned == null || owned.getLowerValue() == null || owned.getUpperValue() == null)
            return  false;

        if (owned.getUpperValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralUnlimitedNatural.toString()) && owned.getLowerValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralInteger.toString())) {
            try {
                return Integer.parseInt(owned.getLowerValue().getValue()) > 0 && owned.getUpperValue().getValue().equals("*");
            } catch (Exception ex) { }
        }

        return false;
    }

    public boolean hasMaxCardinalityRestriction() {
        if (owned == null || owned.getLowerValue() == null || owned.getUpperValue() == null)
            return  false;

        if (owned.getUpperValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralUnlimitedNatural.toString()) && owned.getLowerValue().getXmiType().equals(Enums.XMI_Type.UML_LiteralInteger.toString())) {
            try {
                return Integer.parseInt(owned.getUpperValue().getValue()) > 0 && owned.getLowerValue().getValue() == null;
            } catch (Exception ex) { }
        }

        return false;
    }
}
