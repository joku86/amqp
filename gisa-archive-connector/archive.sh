#!/bin/bash
java -jar gisa-archive-connector.jar -q "tiqsolutions-q-Vertrag1Anlagendaten?DATA,tiqsolutions-q-Vertrag1Logmeldungen?LOG" -r "hbase-site.xml" -c "test.connect.gisa.de,5671,true,gisa,tiqsolutions,sae1yedu3Aid3ie" -t "TABLE_NAME"
