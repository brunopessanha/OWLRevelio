# OWLRevelio
Translates restriced SysML Block Diagrams & Internal Block Diagrams into OWL Ontology

### Currently supported features mapping from SysML to OWL
- Blocks -> Classes
- Comments inside Blocks -> Comment Annotation
- Attributes -> Data Properties
- Generalization -> Subclass
- Part Association -> Generates Min, Max, Exactly and Some (existential) sub class restriction using <i><b>hasPart</b></i> object property
- Internal Blocks -> Individuals

### Supported Tools
- Papyrus: https://www.eclipse.org/papyrus/
