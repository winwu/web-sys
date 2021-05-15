# Web-Sys Admin

## My Environments:

* Editor: IDEA
* Java version: 11 or later
* Apache Maven version: 3.6.3
* Spring Boot 2.4.4 RELEASE
* MySQL Database

## Dependencies Required

Please check `pox.xml` file which includes the required dependencies.

## Some Setups:

1. Due to the need to hide some environment setting from local, the `application.properties` file is default ignored. Please copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties` and update the MySQL info, such as user and password.


## The Swagger

After boot this web application, the swagger page is available on  `http://localhost:{YOUR_PORT}/swagger-ui.html`


## TODOs

https://stackoverflow.com/questions/51218227/user-class-for-spring-security-application
https://howtodoinjava.com/spring-security/custom-userdetailsservice-example-for-spring-3-security/