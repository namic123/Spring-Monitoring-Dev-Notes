## Spring Boot Actuator - loggers 엔드포인트를 통한 동적 로깅 관리

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-Actuator-loggers-%EC%97%94%EB%93%9C%ED%8F%AC%EC%9D%B8%ED%8A%B8%EB%A5%BC-%ED%86%B5%ED%95%9C-%EB%8F%99%EC%A0%81-%EB%A1%9C%EA%B9%85-%EA%B4%80%EB%A6%AC

## 1\. 운영 환경에서 로그 레벨 조정의 필요성

운영 중인 시스템에서 오류가 발생했을 때, 가장 먼저 확인해야 할 대상은 로그다. 하지만 보통 로그 레벨을 INFO 수준으로 설정해두는 것이 일반적이므로, 상세한 정보가 필요한 경우에는 부족한 경우가 많다. 이러한 상황에서 애플리케이션을 재시작하지 않고, **로그 레벨을 실시간으로 조정**할 수 있다면 매우 유용하다.

Spring Boot Actuator는 이를 위한 기능을 /actuator/loggers 엔드포인트를 통해 제공한다.

## 2\. LogController를 통한 테스트 로그 생성

로그 레벨 변경 기능을 실습하기 위해, 아래와 같은 테스트용 컨트롤러를 정의하였다

```
@Slf4j
@RestController
public class LogController {
    @GetMapping("/log")
    public String log() {
        log.trace("trace log");
        log.debug("debug log");
        log.info("info log");
        log.warn("warn log");
        log.error("error log");
        return "ok";
    }
}
```

이 컨트롤러는 TRACE부터 ERROR까지 모든 로그 레벨의 메시지를 출력하도록 구성되어 있다. 기본적으로는 INFO 수준 이하만 출력되기 때문에, 그 이상은 설정을 통해 조정해야 한다.

## 3\. /actuator/loggers 엔드포인트 구조

애플리케이션이 실행된 후, 아래와 같이 loggers 엔드포인트에 접근하면 전체 로거 목록과 설정된 레벨 정보를 확인할 수 있다.

```
GET http://localhost:8080/actuator/loggers
```

**응답 예시**

[##_Image|kage@baSwSC/btsNXTsYdq6/lSfhv3fEXK2JQzXHMWwiAk/img.png|CDM|1.3|{"originWidth":751,"originHeight":595,"style":"alignCenter"}_##]

-   **configuredLevel:** 개발자가 명시적으로 설정한 값
-   **effectiveLevel:** 실제 적용된 최종 로그 레벨

또한, 특정 로거만 조회하고 싶다면 다음과 같이 요청할 수 있다.

```
GET /actuator/loggers/hello.controller
```

[##_Image|kage@C0wEC/btsNYjdPXWS/75d6PbiCtW1bsdDc5lKCK0/img.png|CDM|1.3|{"originWidth":930,"originHeight":326,"style":"alignCenter"}_##]

## 4\. 운영 중 로그 레벨 실시간 변경

운영 중 문제가 발생했을 때는 로그 레벨을 일시적으로 TRACE나 DEBUG로 올리는 것이 도움이 된다. 아래는 TRACE로 변경하는 예다.

```
POST http://localhost:8080/actuator/loggers/hello.controller
Content-Type: application/json

{
  "configuredLevel": "TRACE"
}
```

[##_Image|kage@cfP5LN/btsN0BjkNOt/kc95k7TEQ3NGKcBzgkSjsk/img.png|CDM|1.3|{"originWidth":1481,"originHeight":360,"style":"alignCenter","width":1173,"height":285}_##]

응답은 204 No Content이며, 설정이 성공적으로 반영되었다는 의미다.

이후 다시 로그를 확인하면 다음과 같이 TRACE 로그까지 포함된 결과를 확인할 수 있다

[##_Image|kage@ck3m8C/btsNZrPExoL/MdYqTZWiMvpUxgNTkhWEvK/img.png|CDM|1.3|{"originWidth":436,"originHeight":330,"style":"alignCenter"}_##][##_Image|kage@raUw7/btsNZebSS9y/Bli7umtvPrHsieKWBAjZqK/img.png|CDM|1.3|{"originWidth":750,"originHeight":635,"style":"alignCenter"}_##]

## 5\. 마무리 및 실무 팁

Spring Boot Actuator의 loggers 엔드포인트는 **운영 환경에서 로그 레벨을 유연하게 제어할 수 있는 핵심 도구**다. 다음은 실무에서의 주요 활용 팁이다

-   평소에는 INFO 수준으로 설정하여 로그 과다 출력 방지
-   이슈 발생 시, 해당 클래스나 패키지 로그 레벨을 DEBUG 또는 TRACE로 조정
-   문제 해결 후 다시 INFO 수준으로 복구

#### **요약**

| **기능** | **설명** |
| --- | --- |
| **/actuator/loggers** | 전체 로거의 로그 레벨 확인 |
| **/actuator/loggers/{name}** | 특정 로거에 대한 정보 조회 |
| **POST /actuator/loggers/{name}** | 실시간 로그 레벨 조정 |
|  **운영 팁** | 장애 대응 시 일시적 로그 레벨 조정 후 복구 |

운영 환경에서 애플리케이션의 안정성과 가시성을 동시에 확보하기 위해, **loggers 엔드포인트는 선택이 아닌 필수**다.