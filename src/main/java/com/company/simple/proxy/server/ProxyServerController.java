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
