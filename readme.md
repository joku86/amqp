GISA-Service-Base
===================
 Parent Projekt f�r die Implementierung der Services f�r GISA-Platform.
 Momentan existierende Projekte sind:
###1 Archive-Connector
	Implementierung von Verbindungsaufbau zum HBase oder einer Datenbank (weiter als Datenziel genannt ).
	F�hrt eine Initialisierung des Datenziels (create if not exist).
	Bietet eine M�glichkeit der Transformation f�r die Datens�tze die nicht in dem  Datenzielformat ankommen. 
	Beinhaltet die Implementierung der Methoden zur Daten�bermittlung an das Datenziel.
	In der Implementierung sind Ausfallkriterien und Reaktionen auf diese eingeschlossen. 
	
###2 Websocket-Connector
	Implementierung von Verbindungsaufbau zum Websocketserver(weiter als Datenziel genannt).
	Bietet eine M�glichkeit der Transformation f�r die Datens�tze die nicht in dem  Datenzielformat ankommen. 
	Beinhaltet die Implementierung der Methoden zur Daten�bermittlung an das Datenziel.	
	In der Implementierung sind Ausfallkriterien und Reaktionen auf diese eingeschlossen. 