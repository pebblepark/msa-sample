# MSA - Spring Cloud

# Eureka Service Discovery

- Spring version: `2.4.5`
- Eureka Server

**application.yml**

```yaml
server:
  port: 8761

spring:
  application:
    name: discoveryservice

# eureka 서버에 자신을 등록하지 않도록 설정
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

**EurekaAplication.java**

```java
@SpringBootApplication
@EnableEurekaServer         // 추가
public class EurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }

}

```

# User Service

- Eureka Discovery Client, Spring Boot DevTools, Lombok, Spring Web

**UserserviceApplication.java**

```java
@SpringBootApplication
@EnableDiscoveryClient      // 추가
public class UserserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

}
```

**application.yml**

```yaml
server:
  port: 0

spring:
  application:
    name: user-service

eureka:
  instance:
    instance-id: ${spring.cloud.client.hostname}:${spring.application.instance-id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
#   service-url:
#     defaultZone: http://127.0.0.1:8761/eureka # 서버의 위치, 해당 서버에 유레카 클라이언트 등록
```

- 서버 포트 다르게해서 실행하는 방법 3가지
  - (Default) application.yml : `server.port=9001`
  - vm options 추가 후 실행: `-Dserver.port=9002`
  - mvn 명령어로 실행: `mvn spring-boot:run -Dspring-boot.run.jvmArguments='Dserver.port=9003'`
  - jar 파일 직접 실행: `java -jar -Dserver.port=9004 ./target/userservice-0.0.1-SNAPSHOT.jar`

**랜덤 포트 지정시 포트 번호를 바꾸지 않아도 실행시 자동으로 현재 사용중이지 않은 포트를 할당해준다.**

- 랜덤 포트 지정 : `server.port=0`
- 랜덤 포트를 지정하면 유레카 서버 대시보드에서 인스턴스를 여러개 실행시켜도 항상 포트 0으로 보여줘서 1개로 보임
  - 따라서 `instance-id` 부여해주자
  ```yaml
  eureka:
    instance:
      instance-id: ${spring.cloud.client.hostname}:${spring.application.instance_id:${random.value}}
  ```

# API Gateway Service

- 인증 및 권한 부여
- 서비스 검색 통합
- 응답 캐싱
- 정책, 회로 차단기 및 Qos 다시 시도
- 속도 제한
- 부하 분산
- 로깅, 추적, 상관 관계
- 헤더, 쿼리 문자열 및 청구 변환
- IP 허용 목록에 추가

### Netflix Ribbon

- Spring Cloud에서의 MSA간 통신
  1. RestTemplate
  2. Feign Client
- 기존 loadBalancing은 Ribbon 이용
  - **Client Side** Load Balancer
  - 서비스 호출 시: `ip:port`로 호출 x -> 서비스 이름으로 호출
  - Health Check
  - Spring Cloud Ribbon은 Spring Boot 2.4에서 Maintenance 상태

### Netflix Zuul

- Routing
- API Gateway
- [Spring Cloud Zuul은 Spring Boot 2.4에서 Maintenance 상태](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode)

### Netflix Zuul 구현

- First Service, Second Service

  - Spring Boot: `2.3.8`
  - Lombok, Spring Web, Eureka Discovery Client

    ### First-service

    ```yaml
    server:
      port: 8081

    spring:
      application:
        name: first-service

    eureka:
      client:
        register-with-eureka: false
        fetch-registry: false
    ```

    ### Second-service

    ```yaml
    server:
      port: 8082

    spring:
      application:
        name: second-service

    eureka:
      client:
        register-with-eureka: false
        fetch-registry: false
    ```

- Zuul Service
  - Spring Boot: `2.3.8`
  - Lombok, Spring Web, Zuul
  - API Gateway의 역할
    - zuul-service의 특정 path로 접근하면 유레카에 등록된 first-service와 second-service로 자동 포워딩
  - ZuulFilter를 통해 요청의 사전/사후처리

## Spring Cloud Gateway

- 비동기 방식이 가능
- DevTools, Eureka Discovery Client, Gateway

**application.yml**

```yaml
server:
  port: 8080

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka

spring:
  application:
    name: apigateway
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081
          predicates:
            - Path=/first-service/**
        - id: second-service
          uri: http://localhost:8082
          predicates:
            - Path=/second-service/**
```

- Spring Cloud Gateway 가 API Gateway 역할을 해준다.
  - `http://localhost:8080/first-service/welcome` 호출 : apigateway를 통해 first-service가 호출된다.
    - apigateway에 설정해놓은 predicates.Path의 값이 uri의 값에 붙게됨
    - `http://localhost:8081/first-service/**`처럼 동작

_application.yml 파일 대신 java 코드로 Filter 정보를 등록할 수 있다._

**FilterConfig.java**

```java
@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                    .filters(f -> f.addRequestHeader("first-request", "first-request-header")
                                    .addResponseHeader("first-response", "first-response-header"))
                    .uri("http://localhost:8081"))
                .route(r -> r.path("/second-service/**")
                    .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                    .addResponseHeader("second-response", "second-response-header"))
                    .uri("http://localhost:8082"))
                .build();
    }
}
```
