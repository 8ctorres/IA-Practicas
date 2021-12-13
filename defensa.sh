#!/bin/bash

cd rs-telco-client
echo "Ejercicio 1 - Añadir dos clientes"

mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCustomer 'Cliente Primero' 12345678J 'Elviña s/n' 981111111"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCustomer 'Cliente Segundo' 87654321H 'Maria Pita, 123' 981222222"

read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicios 2,3,4,5 en Postman"
read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicio 6 - Crear cuatro llamadas"

mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCall 1 '2021-10-01T11:00:00' 100 9811000001 LOCAL"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCall 1 '2021-10-02T12:00:00' 200 9811000002 NATIONAL"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCall 1 '2021-10-03T13:00:00' 300 9811000003 INTERNATIONAL"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCall 1 '2021-10-04T14:00:00' 400 9811000004 LOCAL"

read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicio 7 en Postman"
read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicio 8 - Cambiar estado de las llamadas"

mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-changeCallStatus 1 11 2021 BILLED"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-changeCallStatus 1 10 2021 PAID"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-changeCallStatus 1 10 2021 BILLED"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-changeCallStatus 1 10 2021 PAID"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-changeCallStatus 1 10 2021 PAID"

read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicio 9 en Postman"
read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicio 10 - Obtener llamadas entre dos fechas - Paginación automática"

mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-getCalls 1 '2021-10-01T00:00:00' '2021-10-30T23:59:59'"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-getCalls 1 '2021-10-01T00:00:00' '2021-10-30T23:59:59' LOCAL"

read -rsp $'Pulsar enter para continuar...\n' -n1


echo "Ejercicio 11 - Eliminar clientes"

mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-removeCustomer 1"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-removeCustomer 2"
mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-removeCustomer 487"
