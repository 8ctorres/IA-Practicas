# Running the project example
---------------------------------------------------------------------

## Running the telco service with Maven/Jetty.

    cd rs-telco/rs-telco-service
    mvn jetty:run


## Running the telco client application

- Configure `rs-telco/rs-telco-client/src/main/resources/ConfigurationParameters.properties`
  for specifying the client project service implementation (XML or JSON) and the port number 
  of the web server in the endpoint address (7070 for Jetty)
  
- Change to `rs-telco-client` folder

    cd rs-telco/rs-telco-client


- AddCustomer

    mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-addCustomer 'New Customer'"

- FindCustomer

    mvn exec:java -Dexec.mainClass="es.udc.rs.telco.client.ui.TelcoServiceClient" -Dexec.args="-findCustomer 1"

