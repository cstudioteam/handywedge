# handywedge-calendar-service


To run the server, please execute the following:

```
mvn clean package jetty:run
```

You can then view the swagger listing here:

```
Swagger / OpenAPI v2: http://localhost:8080/v1//swagger.json
Swagger / OpenAPI v3: http://localhost:8080/v1//openapi.json
```

Note that if you have configured the `host` to be something other than localhost, the calls through
swagger-ui will be directed to that host and not localhost!