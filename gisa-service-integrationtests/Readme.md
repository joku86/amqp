GISA-Service-Integrationstests
===================

Modul mit den Integrationgstests für die Servicemodule

###AMQP-Broker:Qpid
In dem Ordner qpid-broker/6.0.0/bin liegen die Scripte und der Konfigurationsordner von einer Queue mit einem Exchange  
Zum Starten des Brokers kann das Script "startscript.sh" ausgeführt werden.  
 
#Verbindungsdaten zum Exchange:	
* Host=localhost
* Port=5671 
* UseSSL=true 
* VirtualHost=secureVH
* Benutzername=admin
* Passwort=admin 
* exchange=amq.direct 
* routingKey=forTestQueue

#Verbindungsdaten zu der Queue:	
* Host=localhost
* Port=5671 
* UseSSL=true 
* VirtualHost=secureVH
* Benutzername=admin
* Passwort=admin 
* Queue=testQueue
	

Nützliche Links:
https://qpid.apache.org/releases/qpid-0.24/java-broker/book/Java-Broker-Concepts-Exchanges.html