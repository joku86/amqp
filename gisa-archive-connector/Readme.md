Archive-Connector
===================
Implementierung der persistirungsspezifischen Funktionalitäten für ein Archivierungsservice (HBase)

### Startargumente
Pflicht:  
-q "Queuname?Type,Quename2?Type"  
-r "Pfad zu der hbase-site.xml"  
-c "AMQP-Server details"  
-t "Tabelle"  
Optional:  
-p "Pfad für die Logs"  


start the consumer with
-q QueueName?Type,QueueName?Type
-r receiver for the data 
-p /tmp/logs/gisa-bridge.log

-q "tiqService1-q-Daten1?DATA,tiqService1-q-Logs1?LOG" -r "ws://localhost:8025/tiq/hbase" -p "E:/tmp/AMQP-Consumer.log"
-q "tiqService1-q-Daten1?DATA,tiqService1-q-Logs1?LOG" -r "/tmp/hbase-site.xml" -p "E:/tmp/AMQP-Consumer.log" -t "HBAse_TABLE"
