This project is created for the ["Spring Session JDBC - disable session persistence for anonymous requests"](https://stackoverflow.com/questions/52628602/spring-session-jdbc-disable-session-persistence-for-anonymous-requests) question on StackOverflow.

Run the `PersistentSessionApplication` class to start the server. There are several endpoints:

* [http://localhost:8080/h2-console)](http://localhost:8080/h2-console) - H2 console
* [http://localhost:8080/all)](http://localhost:8080/all) - no protected
* [http://localhost:8080/auth)](http://localhost:8080/auth) - authenticated

The H2 console should be configured with the default Spring Boot settings:

    Driver class: org.h2.Driver
    JDBC URL: jdbc:h2:mem:testdb
    User Name: sa
    Password:

As you cURL to the /auth endpoint:

    curl -v http://user:user@localhost:8282/auth

you'll see multiple sessions created in spring_session table. This is fine.

As you cURL to the /all endpoint:

    curl -v http://localhost:8282/all

no sessions are created in the spring_session table. This is the desired working config - no sessions stored for the unauthenticated users.


