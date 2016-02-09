Livemonitoring-Connector
===================

Implementierung von Verbindungsaufbau zum Websocket-Server(weiter als Datenziel genannt).  
	Bietet eine Möglichkeit der Transformation für die Datensätze die nicht in dem  	Datenzielformat ankommen.  
	Beinhaltet die Implementierung der Methoden zur Datenübermittlung an das Datenziel. 	
	In der Implementierung sind Ausfallkriterien und Reaktionen auf diese eingeschlossen. 
	
	
start the consumer with
-q QueueName?Type,QueueName?Type
-r receiver for the data 
-p /tmp/logs/gisa-bridge.log

-q "tiqService1-q-Daten1?DATA,tiqService1-q-Logs1?LOG" -r "ws://localhost:8025/tiq/hbase" -p "E:/tmp/AMQP-Consumer.log"
-q "tiqService1-q-Daten1?DATA,tiqService1-q-Logs1?LOG" -r "/tmp/hbase-site.xml" -p "E:/tmp/AMQP-Consumer.log" -t "HBAse_TABLE"