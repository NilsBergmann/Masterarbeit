# Formalizing functional Requirements 
**Author:** Nils Bergmann

A formal language for specifying functional requirements using LTL, PLTL and/or MTL logic operators.
This formal specification can be used to generate runnable Java monitors.  

- MappingDSL maps domain names to identifiers and Java classe.
- MonitorDSL allows the user to formulate functional requirements.
- generationTarget contains classes used when running the generated code.

Created as a part of my masters thesis *"Design einer formalen Sprache für die
Spezifikation und Verifikation funktionaler Anforderungen an autonome Systeme"* at TU Dortmund.

### Tested on

- Device A
  - Eclipse DSL: Version 2019-09 R (4.13.0)
  - Java: Version 1.8.0.0_191
  - OS: Windows 10 1803
  - Date: 2020-01-17

- Device B
  - Eclipse DSL: Version 2019-12 (4.14.0)
  - Java: Version 13.0.2
  - OS: Windows 10 1809
  - Date: 2020-01-17

### Used libraries:

- JScience 4.3.1 http://jscience.org/
- sqlite-jdbc 3.27.2.1 https://github.com/xerial/sqlite-jdbc

### Usage:
- Import with Eclipse DSL
- Right Click > `/bergmann.masterarbeit.mappingdsl/src/bergmann/masterarbeit/mappingdsl/MappingDSL.xtext` > Click `Generate Xtext-Artifacts`, ignore errors caused by files that are missing at this point
- Right Click > `/bergmann.masterarbeit.monitordsl/src/bergmann/masterarbeit/MonitorDsl.xtext` > Click `Generate Xtext-Artifacts`, ignore errors caused by files that are missing at this point
- Right click DSL project > `Run as...`> `Eclipse Application` to launch a new Elipse instance.
