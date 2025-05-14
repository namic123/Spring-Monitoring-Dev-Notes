## Spring Boot Actuator -  프로덕션 환경 준비 : 모니터링 시각화를 위한 첫 단계

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-Actuator-%ED%94%84%EB%A1%9C%EB%8D%95%EC%85%98-%ED%99%98%EA%B2%BD-%EC%A4%80%EB%B9%84-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%8B%9C%EA%B0%81%ED%99%94%EB%A5%BC-%EC%9C%84%ED%95%9C-%EC%B2%AB-%EB%8B%A8%EA%B3%84

## 1\. 프로덕션 준비 기능이란?

프로덕션 준비 기능이란, 단순히 애플리케이션을 개발해서 실행하는 수준을 넘어, 실제 운영 환경에서 시스템이 안정적으로 동작하고 있는지를 관찰하고 통제할 수 있게 해주는 기능들을 말한다. 예를 들어, 현재 서비스가 정상적으로 실행 중인지, 시스템 자원이 과다하게 소모되지는 않는지, 로그 레벨은 적절한지 등을 지속적으로 확인할 수 있어야 한다. (즉, 모니터링) 

운영 환경에서 문제가 발생했을 때 이를 실시간으로 감지하고 신속히 대응할 수 있도록, 다음과 같은 기능들이 필요하다.

| **기능명** | **설명** |
| --- | --- |
| **Metric (지표)** | CPU, 메모리, DB 커넥션 등 수치를 실시간 수집 |
| **Trace (추적)** | HTTP 요청, 로직 흐름 등을 따라가며 병목 탐지 |
| **Audit (감사)** | 설정 변경, 인증 실패 등 주요 이벤트 기록 |
| **Health Check** | 애플리케이션의 정상 동작 여부를 진단 |
| **Log Monitoring** | 로그 전송 상태와 로그 레벨 조정 기능 |

#### **Spring Boot Actuator**

Spring Boot는 spring-boot-starter-actuator라는 모듈을 통해, 위에서 언급한 프로덕션 준비 기능을 **간단한 설정만으로 바로 적용**할 수 있도록 제공한다.

**주요 기능**

-   /actuator/health: 애플리케이션 헬스 체크
-   /actuator/metrics: 메트릭 데이터 확인 (예: JVM, HTTP 요청 수 등)
-   /actuator/loggers: 로그 레벨 변경
-   /actuator/beans: 등록된 빈 목록 확인
-   /actuator/env: 환경변수 값 확인

## 2\. Spring Boot Actuator 도입 및 설정

#### **액추에이터 시작: 운영 환경 준비의 첫걸음**

서비스를 실제 운영 환경에 배포하면 단순히 기능이 잘 작동하는지 여부만으로는 충분하지 않다. **서버가 정상적으로 작동 중인지**, **내부 자원은 어떤 상태인지**, **애플리케이션에 등록된 빈은 무엇이 있는지** 등을 수시로 점검해야 한다. 이때, Spring Boot는 이를 위한 전용 기능으로 **Actuator(액추에이터)** 를 제공한다.

액추에이터 기능을 사용하기 위해서는 다음과 같은 의존성을 build.gradle에 추가해야 한다.

```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

프로젝트 실행 후, http://localhost:8080/actuator 로 접속하면 기본적으로 health 엔드포인트가 노출된다.

[##_Image|kage@d8UkKr/btsNVI5jLxu/Vyg9ZPCUijflKpvLS4gCI0/img.png|CDM|1.3|{"originWidth":934,"originHeight":684,"style":"alignCenter"}_##]

기본적으로 노출되는 엔드포인트는 health 뿐이다. http://localhost:8080/actuator/health 로 접근하면 아래와 같은 응답이 출력된다.

[##_Image|kage@bQOafO/btsNULBHg2A/xGNkJ6dic4B1VpO0eSkLJ0/img.png|CDM|1.3|{"originWidth":770,"originHeight":340,"style":"alignCenter"}_##]

이는 애플리케이션이 현재 정상 작동 중임을 의미한다.

**더 많은 엔드포인트 노출하기**

Actuator는 매우 다양한 기능을 제공하지만, **기본적으로는 health와 info만 외부에 노출**된다. 모든 엔드포인트를 웹에서 확인하고자 한다면 application.yml 파일에 아래와 같은 설정을 추가해야 한다:

```
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

이 설정은 Actuator의 모든 엔드포인트를 외부에 노출하겠다는 의미이다. 보안상 민감한 정보가 포함될 수 있으므로, 실무에서는 필요한 항목만 선별적으로 노출하는 방식이 권장된다.

#### **전체 엔드포인트 확인**

설정 이후 다시 http://localhost:8080/actuator 로 접속해보면 앞서 출력된 결과와 다르게 아래와 같이 수많은 엔드포인트가 나타난다.

-   /actuator/health: 애플리케이션 상태 확인
-   /actuator/beans: 스프링 빈 정보 확인
-   /actuator/env: 환경 변수 조회
-   /actuator/metrics: 애플리케이션 메트릭 정보
-   /actuator/loggers: 로그 레벨 확인 및 변경
-   /actuator/mappings: 등록된 RequestMapping 정보
-   /actuator/scheduledtasks: 예약 작업 목록
-   /actuator/heapdump: 힙 메모리 덤프 (JVM 메모리 상태)
-   /actuator/threaddump: 스레드 상태 덤프

## 3\. 엔드포인트 노출 설정 원칙

#### **Actuator 엔드포인트 설정의 원칙**

Actuator는 여러 가지 운영 지표를 엔드포인트로 제공하지만, 이들 엔드포인트를 사용하기 위해서는 **두 가지 설정 조건이 동시에 충족**되어야 한다.

**1\. 엔드포인트 활성화 (Enabled)**

-   엔드포인트 자체의 기능을 **켜거나 끄는 설정**이다.
-   예: shutdown과 같이 기본적으로 꺼져 있는 엔드포인트도 존재한다.

**2\. 엔드포인트 노출 (Exposure)**

-   활성화된 엔드포인트를 **어떤 채널에 노출할지** 설정한다.
    -   주로 사용되는 채널은 web(HTTP)과 jmx이다.
-   노출 설정이 없으면, 기본적으로 거의 모든 엔드포인트는 보이지 않는다.

**모든 엔드포인트를 웹에 노출하는 예**

```
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

-   \*는 모든 엔드포인트를 HTTP로 노출하라는 의미다.
-   하지만, **기본적으로 비활성화된 엔드포인트는 여전히 노출되지 않는다.**
    -   대표적으로 shutdown이 이에 해당된다.

**shutdown 엔드포인트 활성화 및 노출**

```
management:
  endpoint:
    shutdown:
      enabled: true
  endpoints:
    web:
      exposure:
        include: "*"
```

-   shutdown 엔드포인트는 서버를 종료할 수 있는 기능이다.

**실행 및 테스트**

-   POSTMAN과 같은 테스트로 다음 경로로 요청을 보낸다.
-   **POST** http://localhost:8080/actuator/shutdown

[##_Image|kage@byNCdy/btsNUHe2JLa/hcAMLzAIkJZRuRqhI2W1H1/img.png|CDM|1.3|{"originWidth":1654,"originHeight":670,"style":"alignCenter"}_##][##_Image|kage@vEypF/btsNV5y9ZH0/m5dF4sXh9zZr7MYxkk2vq1/img.png|CDM|1.3|{"originWidth":1436,"originHeight":206,"style":"alignCenter"}_##]

단, GET 요청으로는 동작하지 않으며, 반드시 POST 방식이어야 한다.

이 기능은 매우 위험할 수 있기 때문에 기본적으로는 꺼져 있으며, 운영 환경에서는 실수 방지를 위해 **보안 설정과 함께 제한적으로 사용하는 것이 원칙**이다.

**일부 엔드포인트만 노출하기**

예를 들어, env와 beans를 제외한 나머지만 노출하고자 할 경우 아래와 같이 설정할 수 있다.

```
management:
  endpoints:
    web:
      exposure:
        include: "*"
        exclude: "env,beans"
```

-   include: \*로 전부 노출
-   exclude: "env,beans"로 민감한 정보는 제외

**JMX에 특정 엔드포인트만 노출**

```
management:
  endpoints:
    jmx:
      exposure:
        include: "health,info"
```

JMX는 **Java Management Extensions**의 약자로, 자바 애플리케이션의 **실행 중 상태를 모니터링하고 제어할 수 있게 해주는 표준 관리 프레임워크**다. 이 기능은 JVM에 내장되어 있으며, 시스템 자원이나 사용자 정의 자바 객체를 MBean(Management Bean)이라는 형태로 관리 대상 객체로 노출하고, 외부에서 이를 조회하거나 제어할 수 있도록 해준다.

Spring Boot Actuator는 내부적으로 대부분의 기능을 **HTTP 엔드포인트와 JMX 엔드포인트** 두 방식으로 모두 제공한다. 하지만 **기본적으로 JMX는 비활성화되지 않고 켜져 있으며**, HTTP는 일부 엔드포인트만 노출되는 구조다.

그러나, 최근에 JMX 엔드포인트 방식은 실무에서 잘 활용되지 않는다고 하며, 자세한 설명은 생략한다.

## 4\. 마무리 및 요약

Spring Boot Actuator는 단순한 개발 도구가 아니라, 운영 환경에서의 생존력을 보장해주는 핵심 구성요소다. health, metrics, loggers 같은 기능을 통해 개발자는 애플리케이션의 상태를 실시간으로 관찰할 수 있고, Prometheus, Grafana와 연동하면 대시보드 기반의 시각화까지 가능해진다.

스프링 부트에서 제공하는 전체 엔드포인트 목록과 자세한 설명은 아래 공식 문서를 통해 확인할 수 있다.

[https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints)