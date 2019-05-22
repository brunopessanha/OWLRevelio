# OWL Revelio
Translates restricted SysML Block Diagrams & Internal Block Diagrams into OWL Ontology

### Currently supported features mapping from SysML to OWL
- Blocks -> Classes
- Comments -> Comment Annotation
- Attributes -> Data Properties
- Generalization -> Subclass
- Part Association -> Generates Min, Max, Exactly and Some (existential) sub class restriction using <i><b>hasPart</b></i> object property
- Internal Blocks -> Individuals
- Ports -> Individuals with object property relation <i><b>hasPort</b></i>  with the individual of a internal block
- Connection -> Individuals with with object property relation <i><b>isConnectedTo</b></i>  with the individuals of ports
- Data Types -> Only XSD_FLOAT, XSD_Integer and XSD_Boolean are currently supported

### Default OWL Elements

#### Classes
<ul>
  <li>
    Part
    <ul>
      <li>
        FlowPort
        <ul>
          <li>InputFlowPort</li>
          <li>InputOutputFlowPort</li>
          <li>OutputFlowPort</li>
        </ul>
      </li>
      <li>
        FullPort
      </li>
      <li>
        ProxyPort
      </li>
    </ul>
  </li>
  <li>
    Port 
  </li>
  <li>
    Connection
  </li>
</ul>

#### Object Properties
<ul>
  <li>
    hasPart 
  </li>
  <li>
    hasPort 
  </li>
  <li>
    isConnectedTo
  </li>
</ul>

### Supported Tools
- Papyrus: https://www.eclipse.org/papyrus/

### Revelio?

<i>Revelio</i> is a revealing Charm from Harry Potter, which has several variations and applications. When <i>Revelio</i> is used directly on a person, it removes magical disguises. <b>OWL Revelio</b> reveals the Ontology hidden in SysML models.
