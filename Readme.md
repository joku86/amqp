GISA-Service-Base
===================
 Parent Projekt für die Implementierung der Services für GISA-Platform.
 Momentan existierende Projekte sind:
### 1 Archive-Connector
Implementierung von Verbindungsaufbau zum HBase oder einer Datenbank (weiter als Datenziel genannt ).  
	Fhrt eine Initialisierung des Datenziels (create if not exist).  
	Bietet eine Möglichkeit der Transformation für die Datensätze die nicht in dem  Datenzielformat ankommen. 
	Beinhaltet die Implementierung der Methoden zur Datenübermittlung an das Datenziel.  
	In der Implementierung sind Ausfallkriterien und Reaktionen auf diese eingeschlossen. 
	
### 2 Livemonitoring-Connector 
Implementierung von Verbindungsaufbau zum Websocket-Server(weiter als Datenziel genannt).  
	Bietet eine Möglichkeit der Transformation für die Datensätze die nicht in dem  	Datenzielformat ankommen.  
	Beinhaltet die Implementierung der Methoden zur Datenübermittlung an das Datenziel. 	
	In der Implementierung sind Ausfallkriterien und Reaktionen auf diese eingeschlossen. 
	
	  
---
 Current Version: 0.2.0