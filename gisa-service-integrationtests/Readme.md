GISA-Service-Integrationstests
===================

Modul mit den Integrationgstests für die Servicemodule

###AMQP-Broker:Qpid
Für den Broker werden die in der Pom eingefügte Dependencies benötigt.  
Wenn kein LM-Server läuft wird der Test 3 Stunden laufen ohne eine Nachricht zugestellt zu haben da der Connector in den Wartemodus wechselt.


 
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