package uni.tukl.cs.cps.revelio.parser;

public class Enums {

    public enum XML_Tag {
        XMI("xmi:XMI"),
        BlockDiagram("Blocks:Block"),
        ParticipantProperty("Blocks:ParticipantProperty"),
        PackagedElement("packagedElement"),
        OwnedAttribute("ownedAttribute"),
        OwnedEnd("ownedEnd"),
        LowerValue("lowerValue"),
        UpperValue("upperValue"),
        Type("type"),
        OwnedComment("ownedComment"),
        Body("body"),
        End("end");

        private String tagName;

        XML_Tag(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public String toString() {
            return tagName;
        }
    }

    public enum XML_Attribute {

        XMI_ID("xmi:id"),
        XMI_Type("xmi:type"),
        BaseClass("base_Class"),
        BaseProperty("base_Property"),
        Name("name"),
        Aggregation("aggregation"),
        General("general"),
        Type("type"),
        Value("value"),
        Href("href"),
        PartWithPort("partWithPort"),
        Role("role"),
        AnnotatedElement("annotatedElement");

        private String attributeName;

        XML_Attribute(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public String toString() {
            return attributeName;
        }

    }

    public enum XMI_Type {

        UML_Class ("uml:Class"),
        UML_Association("uml:Association"),
        UML_Property("uml:Property"),
        UML_Generalization("uml:Generalization"),
        UML_DataType("uml:DataType"),
        UML_LiteralUnlimitedNatural("uml:LiteralUnlimitedNatural"),
        UML_LiteralInteger("uml:LiteralInteger"),
        UML_Comment("uml:Comment"),
        UML_Connector("uml:Connector");

        private String type;

        XMI_Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public enum Association {

        HasPart ("hasPart");

        private String name;

        Association(String name) { this.name = name; }

        @Override
        public String toString() {
            return name;
        }
    }

}
