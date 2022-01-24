# User Service

User Service is a demo project that can provide project reference.

## Prerequisite

- The java version should be at least **_11_**.

* Install gradle  
  `brew install gradle`
* Install Java 11  
  `brew install openjdk@11`

## Configure git hooks

Git hooks are automatically enabled when you run `./gradlew clean build`

## Import code style scheme（optional）

- Go to `preferences -> Editor -> Code style -> Kotlin -> Import scheme -> Intellij code style xml`
- Choose `config/intellij/code-stye.xml` from project directory

By now, detekt is enabled. If you see any issues, try to restart Intellij

## Quick Start

- Here is a quick start on Spring Boot application in Kotlin main，find the class, right-click and run it:

```
@SpringBootApplication
class UserServiceApplication
fun main(args: Array<String>) {
	runApplication<UserServiceApplication>(*args)
}
```

In another way，you can build project with command，like below:

- ### Run project in local environment

`./gradlew bootRun`

- ### Run other environment to start

`./gradlew bootRun -Pprofile=sit`

- ### Access local browser

http://localhost:8089/v1/users

- ### Actuator

http://localhost:8089/actuator

- ### Swagger

http://localhost:8089/api-docs/swagger-ui.html

## Scripts

- ### Run this command for code style checking:

`./gradlew detekt`

- ### Get coverage reportRunning unit tests with terminal

`./gradlew test`

- ### Get coverage report

`./gradlew jacocoTestReport`

## Configuration

Currently, our config is totally controlled by the project environment variable `profile`, it has three preset
values: `local`, `pre-prod` and `production`. here is the relationship mapping between project executing scripts
and `profile`:

| Executing Script      | APP_ENV    |
| --------------------- | ---------- |
| `./gradlew bootRun`          | `local`    |
| `./gradlew bootRun -Pprofile=local`      | `local`    |
| `./gradlew bootRun -Pprofile=docker`      | `docker`    |
| `./gradlew bootRun -Pprofile=sit` | `pre-prod` |
| `./gradlew bootRun -Pprofile=production`     | `production`     |
