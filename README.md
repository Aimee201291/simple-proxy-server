# Simple proxy server

Simple proxy server consiste en una aplicación que permite mostrar en el navegador y en un archivo de log los headers de las solicitudes HTTP que reenvía la aplicación.

![](https://i.imgur.com/dTNkm6M.jpg)

![](https://i.imgur.com/jGpHSqO.jpg)

## Requerimientos

- Java 8
- Maven 3.6.1 o superior

## ¿Cómo probar la aplicación?

1. Clone (si tiene instalado Git) o descargue este repositorio.
2. Diríjase a la carpeta donde descargó el repositorio
3. Descomprima el archivo ZIP del repositorio
4. Abra la consola de su sistema, diríjase a la carpeta del proyecto y escriba el siguiente comando:

```bash
mvn clean package -DskipTests
```

5. Diríjase a la carpeta target creada en el paso 4 y escriba el siguiente comando en la consola:

```bash
java -jar simple-proxy-server-1.0.jar
```

6. Una vez iniciada la aplicación, diríjase a su navegador y escriba en la barra de direcciones: http://localhost:8080/https://www.google.com

Asegúrese de que el puerto 8080 no esté ocupado antes de seguir estos pasos.

## ¿Cómo funciona la aplicación?

```java
package com.company.simple.proxy.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class ProxyServerController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyServerController.class);


    @RequestMapping(value = "/**")
    public ResponseEntity<String> showHeaders(HttpServletRequest request) {

        String uri = request.getRequestURI();
        uri = uri.substring(1, uri.length());

        Map<String, String> headersReturned = new HashMap<String, String>();

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(uri, String.class);
            entity.getHeaders().entrySet().forEach((k) -> {
                headersReturned.put(k.getKey(), k.getValue().stream().collect(Collectors.joining()));
            });

            String page = buildPageWithHeaders(uri, headersReturned);
            return new ResponseEntity<String>(page, HttpStatus.OK);

        } catch (Exception e) {
            String page = buildErrorPage(uri);
            return new ResponseEntity<String>(page, HttpStatus.NOT_FOUND);
        }
    }

    public String buildPageWithHeaders(String uri, Map<String, String> headersReturned) {

        String headers = "";
        String page = "";
        page = "<html>\n" +
                "<header>" +
                "<title>Welcome</title>" +
                "<style>\n" + "body { background: rgb(184, 221, 255 ) }" + "div { background: white; padding: 5px; overflow-x: scroll }" +
                "li { list-style-type: none }" +
                "</style>\n" +
                "</header>" +
                "<body>\n" +
                "<h1>HTTP Headers</h1>" +
                "<div>\n";
        for (Map.Entry<String, String> entry : headersReturned.entrySet()) {
            page = page +
                    "<li> <strong>" + entry.getKey() + ":</strong> " + entry.getValue() + "</li>\n";
            headers = headers + entry.getKey() + " : " + entry.getValue() + "\n";
        }

        page = page + "</div>\n" +
                "</body>\n" +
                "</html>";

        logger.info("Request to " + uri + ", headers returned from the proxy server: \n\n" + headers);
        return page;
    }

    public String buildErrorPage(String uri) {

        String page = "";
        page = "<html>\n" +
                "<header>" +
                "<title>Welcome</title>" +
                "<style>\n" + "body { background: rgb(184, 221, 255 ) }" + "div { background: white; padding: 5px; overflow-x: scroll }" +
                "</style>\n" +
                "</header>" +
                "<body>\n" +
                "<h1>404 Error :(</h1>" +
                "<div>\n" +
                "<p> You must check the URL or use an absolute URL, for example: https://www.google.com </p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";

        logger.info("Request to " + uri + ", 404 error \n\n");
        return page;
    }
}
```

Cuando la aplicación recibe la solicitud HTTP, el controlador **ProxyServerController** se encarga
de reenviar la misma y luego devuelve una página HTML con los headers de la solicitud HTTP que reenvía.

### Método buildPageWithHeaders

Este método recibe una cadena con la URI de la solicitud y un objeto de tipo **Map<String, String>** el cual contiene
los headers de la solicitud HTTP que la aplicación reenvía. Este método se encarga de construir una página
HTML con los headers de la solicitud HTTP que reenvía la aplicación, así como escribir en un archivo
de log dichos headers. El archivo de log **proxi-server.log** se almacena en la carpeta **target** del proyecto.

### Método buildErrorPage

Este método recibe una cadena con la URI de la solicitud. Este método se encargar de construir una página
HTML con un mensaje de error indicando que se debe comprobar la URL escrita en la barra de direcciones del
navegador, o usar una URL absoluta.

### Método showHeaders

Este método recibe un objeto de tipo **HttpServletRequest**, el mismo es usado para obtener la URI 
de la solicitud HTTP recibida. En este método se utiliza un objeto de tipo **RestTemplate** para hacer una petición GET
usando la URI recibida. A través del método **getHeaders** se obtienen los headers devueltos por la aplicación.

Este método es el encargado de devolver al cliente la página HTML con los headers de la solicitud HTTP que reenvía
la aplicación.
 

## Tecnologías usadas

- Java 8
- Spring
