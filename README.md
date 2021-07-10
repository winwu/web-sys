# Web-Sys Admin

## Environments & setups:

### Overview

Language / Frameworks / Tools / Database | Version 
--- | --- |
Java | 11.0.2 or later |
Javac | 11.0.2 or later |
Apache Maven | 3.6.3 |
Spring Boot | 2.4.4 |
MySQL | 8.0.23 |
Redis | 6.2.4 |
elasticsearch | 7.13.1 |
logstash | 7.13.1 |
kibana | 7.13.1 |


* Editor: IDEA
    * install a plugin: click File -> Settings -> plugins
    * useful plugins: 
        * Spring Assistant
    
* Java (version: 11 or later)
    1. install JDK
    2. set up environment path for JDK
    3. type `java -version` and `javac -version` to make sure these 2 commands are installed.
    
* Apache Maven (version: 3.6.3)
    * maven install dependencies based on pom.xml <dependency>. All the dependency files put in the folder named ".m2", like `/Users/[username]/.m2/repository`.
    1. install Maven
    2. set up environment path for Maven. (e.g `export PATH="$PATH:/Users/win/project/apache-maven-3.6.3/bin"`)
    3. type `mvn -v` to make sure it's installed.
    
* Spring Boot 2.4.4 RELEASE
  
* MySQL Database (version: 8.0.23)

* Redis (version: 6.2.4)
  * install redis by Brew
    1. brew update
    2. brew install redis
  
  * Run Redis server `brew services start redis`
  * Stop Redis server `brew services stop redis`
  * Redis client `redis-cli`
  
* ELK
  * elasticsearch (version: 7.13.1)
    * [install via homebrew](https://www.elastic.co/guide/en/elasticsearch/reference/7.13/brew.html)
    * related commands:
      * brew services start elastic/tap/elasticsearch-full (or just run:
        elasticsearch)
      * brew services stop elasticsearch
      * brew info elasticsearch
    * after start, `http://localhost:9200/` should be available
  * logstash (version: 7.13.1)
    * [install via homebrew](https://www.elastic.co/guide/en/logstash/7.13/installing-logstash.html#brew)
    * related commands:
      * brew services start elastic/tap/logstash-full
      ```
      cd src/main/resources
      logstash -f logstash.conf
      ```
    * after start, `http://localhost:9600/` should be available
  * kibana  (version: 7.13.1)
    * [install via homebrew](https://www.elastic.co/guide/en/kibana/current/brew.html)
    * related commands:
      * brew install elastic/tap/kibana-full
      * brew services start kibana-full
    * after start, `http://localhost:5601` should be available.
    * test:
      1. access http://localhost:5601/app/dev_tools#/console
      2. try `GET /_cat/health?v=true` on console.

---

## Dependencies Required

Please check `pox.xml` file which includes the required dependencies.

## Packaging and execute


### Packing
1. Open IDEA
2. File > Project structure > Artifacts, click "+", choose "JAR", choose "From modules with dependencies". 
3. Choose Main Class "WebsysApplication"
4. Click "OK" twice
5. Click the "Maven" panel on the right side, click "Lifecycle > package" then wait for the packaging process. Once it finished, will show something like this:

(ref: README/packaging1.png, packaging2.png)

```
[INFO] --- maven-jar-plugin:3.2.0:jar (default-jar) @ demo ---
[INFO] Building jar: /Users/[username]/project/websys/target/demo-0.0.1-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.4.4:repackage (repackage) @ demo ---
[INFO] Replacing main artifact with repackaged archive
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Execute

use the jar location path, and run:

```
java -jar demo-0.0.1-SNAPSHOT.jar 
```


## Local Setups

Some unnecessary local information needs to be hidden so that the `application.properties` file ignored by default.
Please copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties` and update some of needed info such as user and password by your preferences.



## Other commands

* Dump mysql schema
```
mysqldump -u user -h localhost --no-data -p websys > database.sql
```


--- 

## API Overviews

METHOD        | ENDPOINT  
--------------|:--------------|
POST          | /users/signup
POST          | /users/login
GET           | /users/me
GET           | /users/parse
GET           | /users/refresh
DEL           | /users/{USERNAME}


## API Request example

### User

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


## The Swagger

After boot this web application, the swagger page is available on  `http://localhost:{YOUR_PORT}/swagger-ui.html`

## How to and some references:

* https://stackoverflow.com/questions/53822337/spring-requestbody-without-using-a-pojo