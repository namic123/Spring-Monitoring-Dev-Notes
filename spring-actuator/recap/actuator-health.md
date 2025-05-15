## Spring Boot Actuator - Spring Boot 애플리케이션 상태 점검, Health Check

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-Actuator-Spring-Boot-%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%EC%83%81%ED%83%9C-%EC%A0%90%EA%B2%80-Health-Check

## 1\. 헬스 체크란 무엇인가?

헬스 체크(Health Check)는 애플리케이션의 상태를 실시간으로 진단하고, 현재 시스템이 정상 동작 중인지 판단할 수 있도록 돕는 기능이다. 단순히 "서버가 살아 있다"는 수준을 넘어서, **데이터베이스 연결**, **디스크 사용량**, **네트워크 통신** 등 다양한 구성 요소의 상태를 종합적으로 판단한다.

Spring Boot에서는 /actuator/health 엔드포인트를 통해 이 기능을 기본 제공하며, 다음과 같이 확인할 수 있다.

```
GET http://localhost:8080/actuator/health
```

[##_Image|kage@bi2sJa/btsNYrV46q3/hKBWHZQy9q0A0ZzYoiitS0/img.png|CDM|1.3|{"originWidth":763,"originHeight":303,"style":"alignCenter"}_##]

이는 전체 시스템이 정상 상태임을 의미하지만, 내부 구성 요소의 상태는 보여주지 않기 때문에 보다 구체적인 정보를 원한다면 추가 설정이 필요하다.

## 2\. 세부 정보 설정 및 예시

헬스 상태의 구성 요소별 상세 정보를 확인하려면 application.yml 또는 application.properties에 다음과 같은 설정을 추가해야한다.

```
management:
  endpoint:
    health:
      show-details: always
```

이 설정을 적용하면, /actuator/health 요청 시 다음과 같은 응답을 받게 된다

[##_Image|kage@cpAAGE/btsNYVoQvqm/zP77GfKansQ4PXtU1BeK90/img.png|CDM|1.3|{"originWidth":1338,"originHeight":884,"style":"alignCenter"}_##]

여기서 db, diskSpace, ping 등은 각각의 헬스 컴포넌트이며, 세부 정보가 함께 제공된다.

만약 세부 정보까지는 필요 없고 컴포넌트별 상태만 간략히 보고 싶다면 다음과 같이 설정할 수 있다.

```
management:
  endpoint:
    health:
      show-components: always # 이 부분이 다름
```

[##_Image|kage@X3VQK/btsNXTrITSL/leCj6oPFCzft6Kt6DzuvuK/img.png|CDM|1.3|{"originWidth":739,"originHeight":605,"style":"alignCenter"}_##]

## 3\. 장애 감지를 위한 DOWN 상태 처리

운영 환경에서는 구성 요소 중 하나라도 문제가 생긴다면 전체 시스템 상태를 **DOWN**으로 간주하는 것이 일반적이다.

예를 들어, 데이터베이스 연결에 실패한 경우 아래와 같은 응답을 받게 된다.

```
{
  "status": "DOWN",
  "components": {
    "db": {
      "status": "DOWN"
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

이처럼 status: DOWN은 장애 발생을 즉시 알려주며, 모니터링 시스템이 이를 감지하여 경고를 발송하거나, 장애 대응 절차를 자동으로 트리거할 수 있도록 돕는다.

## 4\. 기본 제공 인디케이터 목록

Spring Boot는 다양한 구성 요소에 대한 헬스 정보를 자동으로 감지하고 제공한다. 아래는 대표적인 인디케이터다.

| **인디케이터** | **설명** |
| --- | --- |
| **db** | 데이터베이스 연결 상태 |
| **diskSpace** | 디스크 공간 상태 |
| **mongo / redis** | 기타 데이터 소스 연결 상태 |
| **ping** | 단순 생존 확인 |

이 외에도 Actuator의 자동 설정에 의해 다양한 컴포넌트의 상태를 자동으로 확인할 수 있다. 보다 자세한 목록은 아래 공식 문서를 참고하기 바란다.

[https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health.auto-configured-health-indicators](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health.auto-configured-health-indicators)

[Endpoints :: Spring Boot

If you add a @Bean annotated with @Endpoint, any methods annotated with @ReadOperation, @WriteOperation, or @DeleteOperation are automatically exposed over JMX and, in a web application, over HTTP as well. Endpoints can be exposed over HTTP by using Jersey

docs.spring.io](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health.auto-configured-health-indicators)

## 5\. 커스텀 인디케이터 구현

기본 제공 인디케이터 외에도, 다음과 같은 요구사항이 존재할 수 있다.

-   외부 API 응답 상태 점검
-   비즈니스 조건에 따른 시스템 내부 상태 확인
-   특정 큐 시스템이나 메일 서버 상태 확인 등

이 경우, 커스텀 헬스 인디케이터를 직접 구현할 수 있다. 예시는 다음과 같다.

```
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean apiAlive = callExternalService();
        return apiAlive ? Health.up().build() : Health.down().withDetail("error", "API not responding").build();
    }
}
```

이 클래스는 HealthIndicator 인터페이스를 구현하고, @Component로 등록되면 자동으로 헬스 체크 대상에 포함된다.

자세한 구현 방법은 아래 공식 문서를 참고할 수 있다.

[https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health.writing-custom-health-indicators](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health.writing-custom-health-indicators)

## 6\. 마무리 요약

-   /actuator/health 엔드포인트는 애플리케이션 상태 진단의 핵심 도구다.
-   show-details, show-components 옵션을 통해 상태 정보의 출력 범위를 조정할 수 있다.
-   Spring Boot는 DB, 디스크 등 다양한 헬스 인디케이터를 기본 제공하며, 필요 시 직접 구현도 가능하다.
-   헬스 체크는 단순한 확인 기능이 아니라, 장애 대응과 운영 자동화의 첫걸음이다.

운영 환경에서 예기치 않은 장애를 미리 감지하고 빠르게 대응하기 위해서, 헬스 체크 기능은 반드시 활성화하고 정기적으로 점검하는 것이 바람직하다.