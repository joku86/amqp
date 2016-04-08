GISA-Service-Integrationstests
===================

Modul mit den Integrationgstests für die Servicemodule

###AMQP-Broker:Qpid
Für den Broker werden die Qpid Dependencies benötigt.  
Es wird ein LM-Server benötigt. 
Die von Evermind erhaltene Anwendung muss etwas abgeändert werden oder den Branch  feature/setup\_for\_integrations_tests benutzen.
Der Archive Connector lässt sich nicht aus dem Test Starten dieser muss für die Tests manuell ausgeführt werden.
 


 
#Verbindungsdaten zum Exchange:	
* Host=localhost
* Port=5671 
* UseSSL=true 
* VirtualHost=secureVH
* Benutzername=admin
* Passwort=admin 
* exchange=amq.direct 
* routingKeyData=tiqsolar.An1Dat
* routingKeyLog=tiqsolar.An1Log
 

#Verbindungsdaten zu der Queue:	
* Host=localhost
* Port=5671 
* UseSSL=true 
* VirtualHost=secureVH
* Benutzername=admin
* Passwort=admin 
* Daten-Queue=dataQueue
* Log-Queue=logQueue
	

Nützliche Links:
https://qpid.apache.org/releases/qpid-0.24/java-broker/book/Java-Broker-Concepts-Exchanges.html