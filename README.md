# OWL Revelio
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

### Why Revelio?

<i>Revelio</i> is a revealing Charm from Harry Potter, which has several variations and applications. When <i>Revelio</i> is used directly on a person, it removes magical disguises. <b>OWL Revelio</b> reveals the Ontology hidden in SysML models.
