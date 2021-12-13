# Memoria justificativa del proyecto
---------------------------------------------------------------------

## Iteración 1
---------------------------------------------------------------------

### Pruebas WS-BPEL

 - URL a los documentos WSDL que es necesario utilizar:
    Con el servidor OpenESB corriendo:
    http://localhost:8123/CA1/CA1/TelcoFlowInterfacePort?wsdl
    http://localhost:8123/CA1/CA1/BillingFlowInterfacePort?wsdl

    Ambas URLs devuelven el mismo documento WSDL, que contiene los dos bindings (hay dos socios, uno para el cliente "principal" y otro para que el "BillingService" pueda notificarle al flujo los cambios de estado de la factura) y los services correspondientes, con las direcciones a las cuales enviar las peticiones SOAP. Este documento contiene además los imports de los WSDL Abstractos de:

    - TelcoFlowInterface.wsdl
        Contiene la definición del PortType que contiene las operaciones para interactuar con el flujo (en este caso, solo una), el PartnerLinkType correspondiente, y las definiciones de los mensajes de entrada y salida de la operación.

    - BillingFlowInterface.wsdl
        Contiene la definición del PortType con las operaciones para que el BillingService interactúe con el flujo, el PartnerLinkType correspondiente, y las definiciones de los mensajes de entrada de ambas operaciones (ninguna de ellas devuelve respuesta, por lo que tienen mensaje de salida).

 - Nombre del fichero Postman con las peticiones:
    Practica-IA.postman_collection.json

### Justificaciones de diseño

- Eliminamos del MockTelcoService el mapa "phoneCallsByUser"
    Decidimos hacer esto ya que el hecho de tener ese mapa complica innecesariamente la operación de añadir llamadas, ya que habría que registrarlas dos veces (una en el mapa principal, y otra en el mapa del cliente concreto).
    Es cierto que esto hace que la operación de buscar las llamadas de un cliente se hace más ineficiente, ya que no tenemos otra opción que recorrer todo el mapa de llamadas e ir descartando las que no cumplen los criterios de búsqueda.
    Creemos que está justificado ya que esto no deja de ser un Mock, y la cantidad de llamadas que va a haber es muy poca por lo que la ineficiencia no se nota. Y si pensamos en el sistema real, que registraría decenas de miles de llamadas, con toda seguridad ese sistema tendría por debajo una base de datos y no unos simples mapas de Java; y ya esa BD almacenaría las llamadas y quien la diseñase se encargaría de optimizar al máximo posible las consultas más habituales.

- Asumimos que los DNIs de los clientes son únicos
    En la operación "findCustomerByDNI", que nos devuelve los datos de un cliente en función de su DNI, decidimos que tiene más sentido que el tipo de retorno de la operación sea un "Customer" y no una lista de "Customer", dada su semántica. Por este motivo, tenemos que asumir que los DNIs son únicos (aunque que en el mundo real hay casos muy puntuales en los que pueden no serlo), ya que no podemos devolver varios clientes en un solo objeto Customer.
    La operación recorre el mapa de clientes en busca de uno que coincida con el DNI buscado, devuelve ese y deja de buscar. La alternativa recorrerse siempre todo el mapa de clientes y devolver una lista (que prácticamente siempre tendría un solo elemento)

### Problemas conocidos en el diseño / implementación de la práctica

- Si el servidor Jetty no está ejecutando los servicios de "rs-telco-wscontrib", el flujo no gestiona correctamente la excepción y termina de forma abrupta. Después de eso, cualquier petición que se haga vuelve a fallar con un error interno del servidor de OpenESB relacionado con la gestión de los conjuntos de correlación. El problema se soluciona volviendo a desplegar el flujo en el servidor OpenESB.

### Resumen de contribución de cada miembro del grupo (consistente con commits en repositorio GIT)

#### En el Modelo de TelcoService

- Carlos Torres:
    Operación getCallsById (devolver la lista de llamadas de un cliente concreto, pasando el ID del cliente, y posibles opciones adicionales como restricciones temporales y opciones de paginación)
    Operación getCallsByMonth (devolver las llamadas de un cliente para un mes y año concreto, operación que será usada para generar las facturas)
    Operación changeCallStatus (cambiar el estado de las llamadas de un cliente en un mes y año concretos, también usada para generar las facturas)
    Todos los casos de prueba relacionados con estas tres operaciones.

- Ismael Verde:
    Operación addCustomer (añadir un nuevo cliente al sistema)
    Operación updateCustomer (actualizar la información de un cliente en el sistema)
    Operación removeCustomer (eliminar un cliente del sistema)
    Operación findCustomerByID (buscar un cliente en el sistema por su ID)
    Todos los casos de prueba relacionados con esas cuatro operaciones

- Pablo Roade:
    Operación getCustomersByName (buscar clientes en el sistema por su nombre)
    Operación getCustomerByDNI (buscar un cliente en el sistema por su DNI)
    Operación addCall (registrar una nueva llamada en el sistema)
    Todos los casos de prueba relacionados con esas tres operaciones.

#### En el flujo BPEL

    Dada la naturaleza lineal del flujo, no es operativo trabajar de forma separada y que cada uno de nosotros haga una parte, ya que para hacer algo hay que tener completado lo anterior. Para esta parte de la práctica los tres miembros del equipo trabajamos juntos en una sola máquina, en la de Carlos con OpenESB y la de Ismael con SoapUI para generar las peticiones.


## Iteración 2
---------------------------------------------------------------------

### Partes opcionales incluidas y miembros del grupo que han participado
- Swagger: Carlos Torres e Ismael Verde
- Hipermedia: Carlos Torres e Ismael Verde

### Pruebas WS REST
- Nombre del fichero Postman con las peticiones a probar:
    _Practica-IA-it2.postman_collection.json_
- Comandos maven necesarios para ejecutar las pruebas:
    Fichero de script _defensa.sh_

### Pruebas WS-BPEL
- URL a los documentos WSDL que es necesario utilizar:
- Nombre del fichero SoapUI con las peticiones:

### Justificaciones de diseño
- La paginación en el servicio llama al modelo pidiéndole amount+1 elementos para poder comprobar si hay más o no, antes de generar el Link a "next"
- Encapsulamos las excepciones InputValidation e InstanceNotFound en los códigos de error 400 y 404 respectivamente debido a que la semantica es exactamente la misma. Las excepciones específicas de la aplicación (MonthNotClosed, CustomerHasCalls y UnexpectedCallStatus) las sacamos todas con el código 409 Conflict, cuya especificación dice que "indicates a request conflict with current state of the target resource", que creemos encaja muy bien con lo que es una excepción propia de la aplicación. Además, las respuestas 409 se consideran no cacheables, que es el comportamiento que buscábamos ya que todos estos errores pueden desaparecer si repetimos la misma petición un tiempo después. Estas 3 excepciones las encapsulamos en un tipo de excepción genérico, ApplicationException para serializarlas todas de la misma manera. 

### Problemas conocidos en el diseño / implementación de la práctica
- N/A

### Resumen de contribución de cada miembro del grupo (consistente con commits en repositorio GIT)
- Pablo Roade:
  - Operaciones addCall (en todas las capas), findCustomerbyName y findCustomerByDNI (en Modelo y Servicio, ya que no están en Cliente).
  - DTO CustomerDtoJaxb.
  - Excepciones del Cliente y Acceso a servicio.

- Carlos Torres:
  - Operaciones de buscar llamadas (ambas) y cambiar el estado de las llamadas.
  - DTO PhoneCallDtoJaxB y adaptador XML para LocalDateTime
  - Parte de Hipermedia y parte de Swagger

- Ismael Verde:
  - Operaciones addCustomer, updateCustomer, findCustomerbyID y removeCustomer
  - Excepciones de Servicio y Modelo 
  - Parte de Hipermedia y parte de Swagger
