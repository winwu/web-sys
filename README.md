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

## Dump mysql schema

```
mysqldump -u user -h localhost --no-data -p websys > database.sql
```


## APIs

Request example:

### User

* [POST] /users/signup 
* [POST] /users/login
* [GET]  /users/me
* [GET]  /users/parse
* [GET]  /users/refresh
* [DEL]  /users/{USERNAME}

#### [POST] /users/signup

Request Body (application/json)

```json
{
    "email": "superadmin@test.test",
    "password": "12345678",
    "roles": [
      {"id": 2,"name": "ROLE_SYSTEM_ADMIN"}
    ],
    "username": "superadmin"
}
```

#### [POST] /users/login

RequestParam (application/x-www-form-urlencoded)

```
username: "admin"
password: "12345678"
```


#### [GET] /users/me

Response format:

```json
{
  "id": 1,
  "username": "superadmin",
  "email": "superadmin@test.test",
  "permissions": [
    {
      "name": "admin-users-read",
      "resource": "users"
    },
    {
      "name": "admin-users-write",
      "resource": "users"
    },
    {
      "name": "admin-users-delete",
      "resource": "users"
    },
    {
      "name": "admin-users-all",
      "resource": "users"
    },
    {
      "name": "news-read",
      "resource": "news"
    },
    {
      "name": "news-write",
      "resource": "news"
    },
    {
      "name": "news-delete",
      "resource": "news"
    },
    {
      "name": "news-users-all",
      "resource": "news"
    },
    {
      "name": "product-read",
      "resource": "product"
    },
    {
      "name": "product-write",
      "resource": "product"
    },
    {
      "name": "product-delete",
      "resource": "product"
    },
    {
      "name": "product-all",
      "resource": "product"
    }
  ],
  "enabled": true,
  "authorities": [
    {
      "authority": "ROLE_SYSTEM_ADMIN"
    },
    {
      "authority": "ROLE_ADMIN"
    },
    {
      "authority": "ROLE_ADMIN_MARKETING"
    },
    {
      "authority": "ROLE_ADMIN_SALES"
    }
  ],
  "accountNonExpired": false,
  "accountNonLocked": false,
  "credentialsNonExpired": false,
  "permissionsNames": [
    "admin-users-read",
    "admin-users-write",
    "admin-users-delete",
    "admin-users-all",
    "news-read",
    "news-write",
    "news-delete",
    "news-users-all",
    "product-read",
    "product-write",
    "product-delete",
    "product-all"
  ]
}
```

#### [GET] /users/parse

Response format:

```json
{
    "sub": "moving3",
    "auth": [
        {
            "authority": "ROLE_ADMIN_MARKETING"
        }
    ],
    "exp": 1621875598,
    "iat": 1621871998
}
```