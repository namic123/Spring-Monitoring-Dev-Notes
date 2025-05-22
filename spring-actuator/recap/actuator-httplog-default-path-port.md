## Spring Boot Actuator - HTTP 요청/응답 내역 확인하는 방법과 Actuator 기본 포트와 Context 변경 방법

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-Actuator-HTTP-%EC%9A%94%EC%B2%AD%EC%9D%91%EB%8B%B5-%EB%82%B4%EC%97%AD-%ED%99%95%EC%9D%B8%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95%EA%B3%BC-Actuator-%EA%B8%B0%EB%B3%B8-%ED%8F%AC%ED%8A%B8%EC%99%80-Context-%EB%B3%80%EA%B2%BD-%EB%B0%A9%EB%B2%95

## 1\. httpexchanges란 무엇인가?

운영 중인 애플리케이션의 상태를 실시간으로 관찰하고, HTTP 요청 및 응답 흐름을 분석하는 것은 서비스를 운영하면서 중요한 일이다. 이를 돕기 위해 Spring Boot Actuator는 다양한 엔드포인트를 제공하며, 그 중 하나가 바로 /actuator/httpexchanges다.

이 엔드포인트를 활성화하면 최근 애플리케이션에 들어온 HTTP 요청 및 그에 대한 응답 정보를 확인할 수 있다. 그러나 기본적으로는 비활성화되어 있으므로 별도의 설정을 통해 직접 활성화해야 한다.

## 2\. InMemoryHttpExchangeRepository 설정 방법

Spring Boot는 메모리 기반으로 요청 이력을 저장하는 InMemoryHttpExchangeRepository를 기본 제공하고 있다. 이를 활성화하려면 다음과 같이 빈으로 등록해야 한다

```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }

    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
```

이 설정이 완료되면 http://localhost:8080/actuator/httpexchanges 엔드포인트를 통해 HTTP 요청/응답 로그를 확인할 수 있다.

**http://localhost:8080/log 요청을 날렸을때의 기록**

[##_Image|kage@b8yxgt/btsN6qcBjSc/4Jk6zSckSnhCUepXWoxOR1/img.png|CDM|1.3|{"originWidth":1838,"originHeight":1374,"style":"alignCenter"}_##]

## 3\. 저장 용량 조절

기본적으로 이 저장소는 최근 **100개의 요청만** 저장한다. 이 한도를 초과하면 가장 오래된 요청부터 삭제된다. 필요에 따라 저장 개수를 늘릴 수도 있다.

```
@Bean
public InMemoryHttpExchangeRepository httpExchangeRepository() {
    InMemoryHttpExchangeRepository repo = new InMemoryHttpExchangeRepository();
    repo.setCapacity(200); // 최대 저장 요청 수를 200개로 설정
    return repo;
}
```

## 4\. 운영 환경에서의 보안 설정

Actuator는 애플리케이션 내부의 상태, 환경 정보, 로깅 설정, 요청 내역 등 민감한 정보를 노출한다. 따라서 운영 환경에서는 보안상 **세심한 주의가 필요**하다.

**외부에 노출하면 위험한 엔드포인트 예시**

-   /actuator/env : 시스템 환경 변수, 설정 파일 정보
-   /actuator/beans : 등록된 빈 목록 및 경로
-   /actuator/httpexchanges : 요청/응답 내역

이를 막기 위한 대표적인 방법은 다음과 같다.

-   **내부망 접근 제한**
-   **인증 설정 추가**
-   **포트 분리 설정**

## 5\. Actuator 포트 및 경로 커스터마이징

#### **Actuator 전용 포트로 분리**

애플리케이션은 8080, Actuator는 9292 포트에서 운영할 수 있도록 설정할 수 있다.

```
management.server.port=9292
```

**Actuator port 변경**

[##_Image|kage@9vWWM/btsN5vZNnbo/T9MoCAkTncpf9QRytdAnm0/img.png|CDM|1.3|{"originWidth":1054,"originHeight":552,"style":"alignCenter"}_##][##_Image|kage@vxQc6/btsN7Y7fdgA/iHXt3j9jMd3zWAFUS3Pkik/img.png|CDM|1.3|{"originWidth":2128,"originHeight":372,"style":"alignCenter"}_##]

이렇게 하면 일반 사용자는 Actuator에 접근할 수 없으며, 내부 모니터링 시스템에서만 접근하도록 구성할 수 있다.

**엔드포인트 기본 경로 변경**

기본 경로는 /actuator다. 이를 /manage로 변경하고 싶다면 아래 설정을 추가한다.

```
management:
  endpoints:
    web:
      base-path: "/jay-manage"
```

[##_Image|kage@c8M2tF/btsN61cz3Fl/RUGcnV34gJIfPEYgCU7tNk/img.png|CDM|1.3|{"originWidth":920,"originHeight":532,"style":"alignCenter"}_##]

**예: /actuator/health → /jay-manage/health**

## 6\. 요약 정리

| **항목** | **설명** |
| --- | --- |
| **/actuator/httpexchanges** | HTTP 요청/응답 내역 확인 |
| **활성화 방법** | InMemoryHttpExchangeRepository 빈 등록 |
| **저장 개수 제한** | 기본 100개 (수정 가능) |
| **운영환경 사용** | X (개발 및 디버깅용 권장) |
| **보안 조치** | 내부망 제한, 인증 적용 |
| **포트 분리** | management.server.port=9292 |
| **기본 경로 변경** | base-path: "/manage" 설정 가능 |

운영 환경에서 시스템의 상태를 실시간으로 파악하는 것은 더 이상 선택이 아닌 필수다. 그러나 **모니터링의 편의성은 반드시 보안과 함께 병행**되어야 하며, Actuator의 httpexchanges는 디버깅 및 개발용으로 제한적으로 활용하는 것이 가장 안전한 접근 방식이다.