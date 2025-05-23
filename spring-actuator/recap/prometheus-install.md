## Spring Boot 모니터링 (1) - 프로메테우스 개념 및 설치와 기본 설정 방법

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-1-%ED%94%84%EB%A1%9C%EB%A9%94%ED%85%8C%EC%9A%B0%EC%8A%A4-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EC%84%A4%EC%B9%98%EC%99%80-%EA%B8%B0%EB%B3%B8-%EC%84%A4%EC%A0%95-%EB%B0%A9%EB%B2%95

## 1\. Prometheus와 시계열 수집 구조 및 Grafana

Prometheus는 **pull 방식**의 시계열 수집기이자 저장소다. 애플리케이션이 노출하는 /actuator/prometheus 엔드포인트를 주기적으로 호출하여 데이터를 수집하고, 이를 TSDB에 저장한다.

Grafana는 Prometheus의 데이터를 시각화하는 프론트엔드 도구이다. Prometheus를 데이터 소스로 등록하고 PromQL로 데이터를 쿼리하여 실시간 대시보드 형태로 보여줄 수 있다.

사용자는 CPU 사용률, 요청 처리 시간, 메모리 누수 등 다양한 항목을 원하는 형태로 시각화할 수 있으며, Alert 기능도 함께 사용할 수 있다.

#### **Prometheus**

-   **메트릭 수집기 + 시계열 DB**
-   애플리케이션에서 노출한 메트릭을 **주기적으로 pull 방식으로 수집**하여 자체 저장소(TSDB)에 저장
-   Micrometer를 통해 노출된 /actuator/prometheus 데이터를 읽음
-   PromQL이라는 자체 쿼리 언어를 사용하여 원하는 메트릭 데이터를 추출 가능

#### **Grafana**

-   **시각화 툴**
-   Prometheus를 포함한 다양한 데이터 소스를 연결할 수 있으며,
-   메트릭 데이터를 기반으로 **대시보드 형태의 시각화** 제공

#### **전체 흐름 요약**

[##_Image|kage@c7gWzZ/btsOa7JoHF7/fURzvhACXtdkEf6lne5W71/img.png|CDM|1.3|{"originWidth":1394,"originHeight":452,"style":"alignCenter"}_##]

#### **구조 설명**

1.  **애플리케이션 → Micrometer**
    -   Spring Boot Actuator + Micrometer 사용
    -   CPU, JVM, DB 커넥션 등 메트릭을 **Micrometer 표준 방식으로 수집**
2.  **Micrometer → Prometheus 구현체**
    -   Micrometer는 다양한 구현체 지원
    -   micrometer-registry-prometheus 를 사용하여 Prometheus가 이해할 수 있는 **/actuator/prometheus** 엔드포인트 노출
3.  **Prometheus → Micrometer 메트릭 수집**
    -   Prometheus는 /actuator/prometheus 엔드포인트를 **정기적으로 polling(pull)** 하여 데이터를 가져감
    -   내부 **TSDB (Time Series DB(시계열 DB))** 에 저장
4.  **Grafana → Prometheus 조회**
    -   Grafana는 Prometheus를 데이터 소스로 등록
    -   PromQL 기반 쿼리를 통해 Prometheus에서 메트릭을 가져와 시각화

#### **프로메테우스 아키텍처**

[##_Image|kage@bMAxsn/btsObl9sZBY/TBdhgVRcbJ3EOAaBQhTuK1/img.png|CDM|1.3|{"originWidth":1280,"originHeight":768,"style":"alignCenter"}_##]

**출처: : [https://prometheus.io/docs/introduction/overview/](https://prometheus.io/docs/introduction/overview/)**

| **구성 요소** | **설명** |
| --- | --- |
| **Prometheus Server** | 핵심 컴포넌트. 대상 시스템으로부터 메트릭을 **pull** 하여 **TSDB에 저장** |
| **Retrieval** | 대상(exporter/서비스)의 메트릭 수집 담당 |
| **TSDB** | 시계열 DB로 저장 (디스크 기반: HDD/SSD) |
| **HTTP server** | PromQL 요청 처리 및 UI/HTTP API 제공 |
| **PushGateway** | 단명성(short-lived) 작업에서 **push 방식**으로 메트릭 전송 (예: 배치작업) |
| **AlertManager** | 사전 정의한 경고 조건이 충족되면 알림 전송 (Email, Slack, PagerDuty 등) |
| **PromQL** | Prometheus의 메트릭 질의 언어. 필터링, 집계 등에 사용 |
| **Grafana** | 시각화 대시보드. Prometheus에 연결하여 PromQL로 데이터 조회 |

## 2\. Prometheus 설치 및 실행 (Window)

#### **Prometheus 설치 방법**

**다운로드 경로**

-   윈도우 사용자 : windows-amd64 설치
-   MAC 사용자 : darwin-amd64 설치

**[https://prometheus.io/download/](https://prometheus.io/download/)**

[##_Image|kage@czP160/btsObcrdCWm/BxoHb3K9Cu34GKeCanKXR0/img.png|CDM|1.3|{"originWidth":2464,"originHeight":1010,"style":"alignCenter"}_##]

## **Windows 사용자**

**다운로드**

-   prometheus-{version}.windows-amd64.zip 압축파일 다운로드 후 압축 해제

**실행 방법**

1.  압축 해제 후 디렉토리 내 prometheus.exe 실행
2.  처음 실행 시 Windows SmartScreen이 차단할 수 있음
    -   **\[추가 정보\] → \[실행\]** 버튼을 눌러 실행 허용

[##_Image|kage@biJOXC/btsObp4VcKp/k0AXk0zcvpL4ZAOyJGrdA0/img.png|CDM|1.3|{"originWidth":1172,"originHeight":612,"style":"alignCenter","caption":"prometheus.exe 실행"}_##]

**실행 확인**

-   브라우저에서 http://localhost:9090 접속 → Prometheus Web UI 확인

[##_Image|kage@d3glYF/btsObrBFghT/yRhKoKgZNIsrn0q6YCvPq0/img.png|CDM|1.3|{"originWidth":1632,"originHeight":956,"style":"alignCenter","caption":"prometheus Web UI"}_##]

## 3\. prometheus.yml 구성

#### **애플리케이션에서 Prometheus를 위한 메트릭 준비하기**

Spring Boot에서 메트릭(지표)은 기본적으로 Micrometer라는 추상화 라이브러리를 통해 수집된다. Prometheus가 이 메트릭을 수집하려면 **자신의 포맷에 맞게 노출된 엔드포인트**가 필요하다.

하지만 직접 Prometheus 포맷을 만들 필요는 없다. **Micrometer가 알아서 처리해주기 때문이다.**

#### **Springboot에 Prometheus 구현체 의존성 추가**

```
// build.gradle
implementation 'io.micrometer:micrometer-registry-prometheus'
```

이 의존성을 추가하면

-   Micrometer가 **Prometheus 포맷으로 메트릭을 변환**한다.
-   Spring Boot Actuator는 자동으로 /actuator/prometheus 엔드포인트를 생성한다.

**Actuator 관련 포스팅 글**

[2025.05.14 - \[Backend/Spring(활용)\] - Spring Boot Actuator - 프로덕션 환경 준비 : 모니터링 시각화를 위한 첫 단계](https://pjs-world.tistory.com/entry/Spring-Boot-Actuator-%ED%94%84%EB%A1%9C%EB%8D%95%EC%85%98-%ED%99%98%EA%B2%BD-%EC%A4%80%EB%B9%84-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%8B%9C%EA%B0%81%ED%99%94%EB%A5%BC-%EC%9C%84%ED%95%9C-%EC%B2%AB-%EB%8B%A8%EA%B3%84)

[##_Image|kage@2HHgB/btsObmN3kLj/3AY1cKza46KkHsTYBD8of1/img.png|CDM|1.3|{"originWidth":1514,"originHeight":896,"style":"alignCenter"}_##]

####  **Prometheus 포맷 특징 및 변환 방식**

| **Spring Actuator 포맷** | **Prometheus 포맷** | **설명** |
| --- | --- | --- |
| **jvm.info** | jvm\_info | . → \_로 자동 변환 |
| **logback.events** | logback\_events\_total | 증가형 메트릭에는 \_total 붙음 |
| **http.server.requests** | 1\. http\_server\_requests\_seconds\_count | 요청 수 |
|   | 2\. http\_server\_requests\_seconds\_sum | 응답 시간의 합계 |
|   | 3\. http\_server\_requests\_seconds\_max | 가장 오래 걸린 응답 시간 |

**Prometheus는 이름에 .을 허용하지 않기 때문에 Micrometer가 자동으로 \_로 바꿔준다.**

#### **Prometheus.yml 수집 설정 구성**

prometheus.yml은 Prometheus 서버의 **중앙 설정 파일**로, 어떤 타겟을 어떤 주기로 수집할지 정의한다. 이제 프로메테우스가 애플리케이션의 /actuator/prometheus를 호출해서 메트릭을 주기적으로 수집하도록 설정해보자.

프로메테우스 폴더에 promethues.yml 수정

[##_Image|kage@1q90L/btsOcae1dIY/MrUPZ8FKw2ae6MkdJPOjK0/img.png|CDM|1.3|{"originWidth":552,"originHeight":330,"style":"alignCenter"}_##]

```
global:
  scrape_interval: 15s   # 전체 기본 수집 주기
  evaluation_interval: 15s

scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["localhost:9090"]

  - job_name: "spring-actuator"     # 우리가 추가한 항목
    metrics_path: "/actuator/prometheus"  # 메트릭 수집 경로
    scrape_interval: 1s             # 수집 주기 (예제용)
    static_configs:
      - targets: ["localhost:9292"] #  애플리케이션 위치
```

#### **설정 항목 상세 설명**

| **항목** | **설명** |
| --- | --- |
| **job\_name** | Prometheus에서 수집 작업 이름. 아무 이름 가능 (spring-actuator) |
| **metrics\_path** | 메트릭이 노출된 엔드포인트 (/actuator/prometheus) |
| **scrape\_interval** | 수집 주기. 예제에서는 1초, 실제 운영은 보통 10초~1분 |
| **static\_configs.targets** | 메트릭을 수집할 대상 서버의 IP:PORT (localhost:9292) |

#### **설정 이후 Prometheus 재시작**

설정 파일을 바꿨다면 반드시 **Prometheus 서버를 재시작**해야 반영.

#### **정상 연동 확인 방법**

**1\. 구성 확인**

[http://localhost:9090/config](http://localhost:9090/config)  
 현재 Prometheus가 읽고 있는 설정을 확인할 수 있다.

[##_Image|kage@m3gUz/btsOao0fVT3/bXohMIpPZiUxV0AHXVLyn1/img.png|CDM|1.3|{"originWidth":2060,"originHeight":1398,"style":"alignCenter"}_##]

**2\. 타겟 상태 확인**

[http://localhost:9090/targets](http://localhost:9090/targets)

-   prometheus: 자체 메트릭
-   spring-actuator: 우리가 연동한 Spring Boot 애플리케이션

> **State**: UP이면 수집 성공  
> **State**: DOWN이면 메트릭 수집 실패 (포트, URL 경로, 애플리케이션 실행 상태 등을 확인해야 함)

[##_Image|kage@cNL7iC/btsObYr9NTR/dIZC7kTIiWOEtoeN7oImi0/img.png|CDM|1.3|{"originWidth":2508,"originHeight":956,"style":"alignCenter"}_##]

#### **메트릭 조회 예시**

[http://localhost:9090](http://localhost:9090) 접속 후 좌측 상단 검색창에서 다음을 입력

```
jvm_info
```

[##_Image|kage@bsiFVK/btsOaXVCp16/aYuSz6jKeMiHB5Hqvb8lX0/img.png|CDM|1.3|{"originWidth":2538,"originHeight":566,"style":"alignCenter"}_##]

**이 명령어는 Prometheus가 Spring Boot에서 수집한 JVM 정보 메트릭을 조회**

#### **운영 환경 주의사항**



| **항목** | **설명** |
| --- | --- |
| **scrape\_interval** | 1초 단위 수집은 개발/테스트 용도로만. 운영에서는 10s ~ 60s 권장 |
| **과다 수집 시 문제** | 요청 부하 증가, 메모리 사용량 상승, Prometheus 디스크 부담 |