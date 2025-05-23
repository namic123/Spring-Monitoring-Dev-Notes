## Spring Boot Actuator - Micrometer를 활용한 표준화된 지표 수집 방식

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-Actuator-Micrometer%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%ED%91%9C%EC%A4%80%ED%99%94%EB%90%9C-%EC%A7%80%ED%91%9C-%EC%88%98%EC%A7%91-%EB%B0%A9%EC%8B%9D

## 1\. Micrometer란 무엇인가?

Micrometer는 **메트릭(Metrics) 수집의 표준화된 추상화 계층**이다. 애플리케이션에서 CPU, JVM, 커넥션 상태 등 다양한 지표를 수집한 후, **특정 모니터링 툴에 종속되지 않고** 공통 인터페이스를 통해 데이터를 전달할 수 있도록 돕는다.

## 2\. 왜 Micrometer가 필요한가?

#### **기존 방식의 문제점**

기존에는 JMX와 같은 방식으로 메트릭을 수집하였고, 이는 모니터링 툴의 변경 시 모든 로직을 수정해야 하는 구조적 문제가 있었다.

| **\* JMX ?   ** - Java에서 리소스를 모니터링하고 관리하기 위한 표준 API |
| --- |

[##_Image|kage@bGXO60/btsOamsPhnQ/uKG6DcFn6IO64K4c0JBXak/img.png|CDM|1.3|{"originWidth":628,"originHeight":331,"style":"alignCenter"}_##]

-   각각의 메트릭(CPU, JVM, Connection 등)을 **JMX 형식에 맞춰 수집**
-   JMX API를 통해 JMX 모니터링 툴에 전달
-   **툴에 종속된 방식** → JMX 형식에 맞춰야만 작동

#### **모니터링 툴 변경 시 발생하는 문제**

[##_Image|kage@bsQRhk/btsN89nQVdm/lNK7N4fq8EBioJPr0qGfjK/img.png|CDM|1.3|{"originWidth":617,"originHeight":338,"style":"alignCenter"}_##]

-   JMX → Prometheus로 모니터링 툴을 바꾸면?
-   **측정 포맷 변경 + API 변경 + 툴 연동 방식 변경** 필요
-   기존 JMX 방식은 쓸 수 없음 → **모든 코드 수정 필요**

**단순히 모니터링 도구만 바꿨을 뿐인데 애플리케이션 로직까지 바뀌어야 하는 심각한 유지보수 문제 발생**

**이러한 문제를 해결하는 것이 바로 마이크로미터 라이브러리이다.**

#### **Micrometer 도입의 효과**

[##_Image|kage@xe6p9/btsN73PzGGD/ypiKmybfMRqzc2QTyCsia0/img.png|CDM|1.3|{"originWidth":628,"originHeight":302,"style":"alignCenter"}_##]

-   CPU, JVM, Connection 등의 데이터를 **Micrometer 표준 형식**으로 수집
-   Micrometer는 다양한 모니터링 툴에 맞는 구현체(JMX, Prometheus 등)를 통해 변환/전달
-   애플리케이션 로직은 **변경 없이** 측정 코드만 유지

#### **마이크로미터 전체 구조**

[##_Image|kage@CzNPu/btsN92BwNNJ/VhG5Wk4y4ylFMQnjMBI8sK/img.png|CDM|1.3|{"originWidth":866,"originHeight":324,"style":"alignCenter"}_##]

-   Micrometer는 **측정 방식의 추상화**를 담당
-   수집된 데이터는 각각의 툴(JMX, Prometheus 등)의 구현체를 통해 전달됨
-   마치 SLF4J가 로그 구현체를 추상화하는 것과 유사한 원리

## 3\. Spring Boot Actuator와의 통합

Spring Boot Actuator는 Micrometer를 기반으로 다양한 메트릭을 자동 수집하고 /actuator/metrics 엔드포인트를 통해 이를 노출한다.  
기본 제공 메트릭 외에도, HTTP 요청 수, JVM 메모리 사용량, 커넥션 풀 상태 등도 자동으로 확인할 수 있다.

#### **메트릭 확인 방법**

#### **1\. metrics 엔드포인트**

Spring Boot 애플리케이션이 실행 중일 때, 다음 주소로 접속하면 현재 애플리케이션에서 사용 가능한 **모든 메트릭 항목 이름 목록**을 확인할 수 있다.

[##_Image|kage@bU0D9b/btsN9vYs8h7/Dp0bfpBsQYenmfH553UG4k/img.png|CDM|1.3|{"originWidth":1018,"originHeight":658,"style":"alignCenter"}_##]

#### **2\. 메트릭 상세 조회**

각 항목의 **구체적인 수치와 구성**을 확인하고 싶다면, 해당 메트릭 이름을 URL 경로 뒤에 붙이면 된다.

[##_Image|kage@NszJW/btsN9ASEy0e/fnMnyTzfKTFjjYK9fLWiS0/img.png|CDM|1.3|{"originWidth":1156,"originHeight":760,"style":"alignCenter"}_##]

#### **3\. Tag를 활용한 필터링**

아래 응답에 포함된 availableTags를 통해 메트릭 데이터를 **더 세밀하게 필터링**할 수 있다

[##_Image|kage@LM9f4/btsN9MMvakJ/dkF2Igvrt1EYNmktbWHV41/img.png|CDM|1.3|{"originWidth":1020,"originHeight":1054,"style":"alignCenter"}_##][##_Image|kage@cnjZ3K/btsOauj1HMs/te8VBciTqz7nQJOYz1YPW0/img.png|CDM|1.3|{"originWidth":1276,"originHeight":802,"style":"alignCenter"}_##]

### **4\. HTTP 요청 메트릭 확인**

HTTP 요청 처리 수, 처리 시간 등도 자동으로 수집된다.

[##_Image|kage@tQfsY/btsN9J91iu7/32BrgyN4MAtLJJ5HkmIyrK/img.png|CDM|1.3|{"originWidth":1118,"originHeight":1364,"style":"alignCenter"}_##]

#### **/log URI 요청에 대한 메트릭만 확인**

```
http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/log
```

#### **/log 요청 중 응답 코드가 200인 항목만 확인**

```
http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/log&tag=status:200
```

## 4\. Spring Boot Actuator가 제공하는 메트릭 종류

#### **기본 제공 메트릭 종류**

#### **1\. JVM 메트릭 (jvm.)**

JVM 내부의 실행 환경과 관련된 메트릭들을 제공

-   메모리 및 버퍼 풀 사용량 (jvm.memory.used, jvm.buffer.memory.used)
-   가비지 컬렉션 정보 (jvm.gc.pause)
-   스레드 수 (jvm.threads.live, jvm.threads.daemon)
-   클래스 로딩/언로딩 수 (jvm.classes.loaded)
-   JIT 컴파일 시간

JVM 튜닝이나 GC 이슈 분석에 유용.

---

#### **2\. 시스템 메트릭 (system., process., disk.)**

운영체제와 프로세스 수준의 메트릭을 제공

-   CPU 사용률 (system.cpu.usage, process.cpu.usage)
-   사용 가능한 디스크 공간 (disk.free)
-   프로세스 가동 시간 (process.uptime)
-   파일 디스크립터 수 (process.files.max, process.files.open)

전체 시스템 리소스 상태를 파악하는 데 활용

---

#### **3\. 애플리케이션 시작 메트릭**

Spring Boot 애플리케이션의 시작 시간에 대한 정보를 제공

-   application.started.time: ApplicationStartedEvent 기준 시간
-   application.ready.time: ApplicationReadyEvent 기준 시간

애플리케이션이 실제로 사용 가능한 상태가 되기까지의 소요 시간을 측정

---

#### **4\. 스프링 MVC 요청 메트릭 (http.server.requests)**

Spring MVC가 처리한 HTTP 요청에 대한 다양한 지표를 제공

-   요청 URI (tag=uri)
-   HTTP 메서드 (tag=method)
-   응답 상태코드 (tag=status)
-   예외 종류 (tag=exception)
-   응답 결과 그룹 (tag=outcome) – SUCCESS, CLIENT\_ERROR, etc.

특정 API의 호출 빈도, 응답 상태, 오류 패턴 등을 분석

---

#### **5\. 데이터소스 및 커넥션 풀 메트릭 (jdbc., hikaricp.)**

JDBC 커넥션 풀 상태를 모니터링

-   활성 커넥션 수 (jdbc.connections.active)
-   대기 중인 커넥션 수
-   히카리CP 커넥션 풀 정보 (hikaricp.connections.\*)

 DB 커넥션 병목 여부를 진단하는 데 유용.

---

#### **6\. 로그 메트릭 (logback.events)**

Logback 로깅 레벨별 로그 발생 횟수를 수집

-   logback.events는 각각의 로그 레벨(trace, debug, info, warn, error) 별 로그 수를 제공합니다.

 예외 상황이나 경고 로그가 갑자기 증가했을 때 이상 징후를 감지

---

#### **7.  톰캣 메트릭 (tomcat.)**

톰캣 내부의 메트릭도 확인

-   기본적으로 세션 관련 메트릭(tomcat.sessions.\*)만 활성화됨
-   전체 톰캣 메트릭 활성화 설정 필요

```
server:
  tomcat:
    mbeanregistry:
      enabled: true
```

톰캣 쓰레드 풀 사용량, 요청 처리 대기 현황 등을 분석

---

#### **8\. 기타 메트릭**

추가적으로 다음과 같은 구성 요소의 메트릭도 지원

-   **RestTemplate, WebClient** 요청 메트릭
-   **캐시**(예: caffeine, ehcache)
-   **스케줄러, Executor** 작업량 및 대기 시간
-   **Spring Data Repository**
-   **MongoDB, Redis** 등

#### **정리**

| **분류** | **메트릭 접두어** | **주요 항목** |
| --- | --- | --- |
| **JVM** | jvm. | 메모리, GC, 쓰레드, 클래스 |
| **시스템** | system., process., disk. | CPU, 디스크, 프로세스 상태 |
| **시작 시간** | application. | 시작 완료, 준비 완료 시간 |
| **MVC 요청** | http.server.requests | URI, 상태, 예외, 응답 시간 |
| **DB/CP** | jdbc., hikaricp. | 커넥션 풀 상태 |
| **로깅** | logback.events | 로그 레벨별 수 |
| **톰캣** | tomcat. | 세션 수, 쓰레드 풀 등 |
| **기타** | cache., scheduler., mongo. | 다양한 외부 구성 요소 |