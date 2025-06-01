## Spring Boot 모니터링 (2) - 프로메테우스 기본 기능 활용 방법(promql)

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-2-%ED%94%84%EB%A1%9C%EB%A9%94%ED%85%8C%EC%9A%B0%EC%8A%A4-%EA%B8%B0%EB%B3%B8-%EA%B8%B0%EB%8A%A5-%ED%99%9C%EC%9A%A9-%EB%B0%A9%EB%B2%95promql

## 1\. Prometheus에서 메트릭을 조회하는 기본 방법

Prometheus는 수집된 메트릭을 Web UI를 통해 직접 조회할 수 있도록 기능을 제공한다. 가장 일반적인 방식은 메트릭 이름을 그대로 검색창에 입력하는 것이다. 예를 들어 http\_server\_requests\_seconds\_count는 HTTP 요청 횟수를 의미하는 카운터 메트릭이다.

[##_Image|kage@KPB0C/btsOmrBld1I/lcFtEq3OITmQj74gJlUHf0/img.png|CDM|1.3|{"originWidth":2542,"originHeight":770,"style":"alignCenter"}_##]

메트릭을 조회하면 다양한 레이블(Label)이 함께 나타나며, 이 레이블은 Micrometer의 태그(Tag)와 동일한 역할을 한다. 각 메트릭은 다음과 같은 레이블로 구분된다

Prometheus에서 메트릭 값은 레이블(Label)로 구분되며, 이는 Micrometer의 태그(Tag)와 동일한 개념이다.

| **레이블 이름** | **의미** |
| --- | --- |
| **error** | 예외 발생 여부 |
| **exception** | 예외 클래스 이름 |
| **instance** | 수집 대상 인스턴스 정보 |
| **job** | Prometheus 설정에서 정의한 job 이름 |
| **method** | HTTP 요청 메서드(GET, POST 등) |
| **outcome** | 요청 결과(success, client error 등) |
| **status** | HTTP 상태 코드(200, 404 등) |
| **uri** | 요청 URI(/api/users, /login 등) |

## 2\. Evaluation Time과 그래프 시각화

**Evaluation time 변경**

-   Prometheus Web UI에서 특정 과거 시점의 메트릭을 조회할 수 있음.

[##_Image|kage@MzvKQ/btsOmVoDZrz/Wd4UUy90080CQC12hbswbK/img.png|CDM|1.3|{"originWidth":1230,"originHeight":626,"style":"alignCenter"}_##]

**Graph 탭**

-   메트릭을 시계열 그래프로 시각화할 수 있음.

[##_Image|kage@cp7Z1n/btsOnvwfMMB/T3Kr1iTnEEqV05hGq4yZt1/img.png|CDM|1.3|{"originWidth":2512,"originHeight":988,"style":"alignCenter"}_##]

## 3\. 레이블 기반 필터링 기법

#### **레이블 필터링**

Prometheus에서는 중괄호 {} 안에 조건을 넣어 레이블 기반 필터링이 가능하다.

| **연산자** | **의미** | **예시 사용법** |
| --- | --- | --- |
| **\=** | 정확히 일치 | uri="/log" |
| **!=** | 일치하지 않음 | uri!="/health" |
| **\=~** | 정규식으로 일치 | \`method=~"GET |
| **!~** | 정규식으로 일치하지 않음 | uri!~"/actuator.\*" |

**예시 필터링**

-   **uri=/log , method=GET 조건으로 필터 :** http\_server\_requests\_seconds\_count{uri="/log", method="GET"}

[##_Image|kage@dttO4q/btsOk4m9WdE/BKbDOeRL1YJZ3CLcdYrbV1/img.png|CDM|1.3|{"originWidth":1890,"originHeight":432,"style":"alignCenter","caption":"'"}_##]

-   **/actuator/prometheus 는 제외한 조건으로 필터 :** http\_server\_requests\_seconds\_count{uri!="/actuator/prometheus"}

[##_Image|kage@bjAb2x/btsOnu5bZ8r/kyKMJ2pKEEyMSdoO66KAUK/img.png|CDM|1.3|{"originWidth":1944,"originHeight":500,"style":"alignCenter"}_##]

-   **method 가 GET , POST 인 경우를 포함해서 필터 :** http\_server\_requests\_seconds\_count{method=~"GET|POST"}

[##_Image|kage@OVRAC/btsOk4AGaki/2IkACoqN9K9cum7hCHLUk0/img.png|CDM|1.3|{"originWidth":2016,"originHeight":468,"style":"alignCenter"}_##]

-   **/actuator 로 시작하는 uri 는 제외한 조건으로 필터:** http\_server\_requests\_seconds\_count{uri!~"/actuator.\*"}

[##_Image|kage@OVRAC/btsOk4AGaki/2IkACoqN9K9cum7hCHLUk0/img.png|CDM|1.3|{"originWidth":2016,"originHeight":468,"style":"alignCenter"}_##]

## 4\. 연산자와 내장 함수 활용

#### **연산자와 내장 함수**

| **종류** | **설명** | **예시** |
| --- | --- | --- |
| **+, -, \*, /, %, ^** | 산술 연산 | rate(metric\[1m\]) \* 100 |
| **sum(metric)** | 전체 합계 | sum(http\_server\_requests\_seconds\_count) |
| **sum by(label)(metric)** | 레이블별 그룹 합계 | sum by(method, status)(http\_server\_requests\_seconds\_count) |
| **count(metric)** | 메트릭 개수 | count(http\_server\_requests\_seconds\_count) |
| **topk(k, metric)** | 상위 k개 추출 | topk(2, http\_server\_requests\_seconds\_count) |

#### **내장 함수 테스트**

[##_Image|kage@kCRp4/btsOlgATrW2/gnmHXUhiNfmZ5jKYFREPdk/img.png|CDM|1.3|{"originWidth":2522,"originHeight":572,"style":"alignCenter"}_##]

**sum() - 전체 함**

[##_Image|kage@V4Eww/btsOl7C4itT/dAVZQqzzCLyFki8urVK2H0/img.png|CDM|1.3|{"originWidth":2502,"originHeight":402,"style":"alignCenter"}_##]

**sumBy() - SQL 의 GroupBy 와 비슷**

[##_Image|kage@dKwcZk/btsOkOY4CRk/vdPfAMjyW5alMoXdNmYyeK/img.png|CDM|1.3|{"originWidth":2530,"originHeight":583,"style":"alignCenter"}_##]

**count() - 매트릭 개수. 즉, http\_server-request\_seconds\_count 시에, 나오는 메트릭 개수**

[##_Image|kage@cti9Hk/btsOljRNK5O/XQ0rNAC7kRIIrCjlKMkU20/img.png|CDM|1.3|{"originWidth":2520,"originHeight":402,"style":"alignCenter"}_##]

**topk(k, metric) - 상위 k개 노출**

[##_Image|kage@mfV4n/btsOmhZL5vR/usxLQ4QLauL32WpD8c9duk/img.png|CDM|1.3|{"originWidth":2524,"originHeight":468,"style":"alignCenter"}_##]

## 5\. 시간 범위 기반 쿼리

시간 축 기반의 분석은 시계열 데이터에 있어 핵심적인 요소이며, Prometheus는 다음과 같은 방식으로 이를 처리한다.



| **문법** | **설명** | **예시** |
| --- | --- | --- |
| **offset** | 과거 특정 시점 조회 | http\_server\_requests\_seconds\_count offset 10m |
| **\[범위\]** | 범위 벡터 조회 | http\_server\_requests\_seconds\_count\[1m\] |

범위 벡터를 사용하여 단순 조회만 하는 경우, 그래프가 자동으로 그려지지는 않는다. 따라서 이 값을 기반으로 **rate() 또는 increase()**와 같은 함수와 함께 사용해야 한다.

[##_Image|kage@WC0Af/btsOnenYIM1/srBf6YPFFvLAAl3kzlNfBK/img.png|CDM|1.3|{"originWidth":2546,"originHeight":362,"style":"alignCenter"}_##]

**예시 promql**

```
rate(http_server_requests_seconds_count[1m])
increase(http_server_requests_seconds_count{uri="/log"}[5m])
```

-   **rate()**: 초당 평균 증가율을 계산한다.
-   **increase()**: 해당 시간 동안 실제로 증가한 값을 보여준다.

**rate()**

[##_Image|kage@ceinby/btsOl98JcBA/Ko0yEDKf5RgB6w7pAhlNmk/img.png|CDM|1.3|{"originWidth":2486,"originHeight":898,"style":"alignCenter"}_##]

**increase()**

[##_Image|kage@bJTBsC/btsOmafv3Tk/vpzJV9kAoM1xu9ntWLUPNk/img.png|CDM|1.3|{"originWidth":2518,"originHeight":862,"style":"alignCenter"}_##]

### **정리**

| **기능** | **설명** |
| --- | --- |
| **메트릭 검색** | 검색창에서 메트릭 이름을 입력 |
| **레이블 필터링** | {} 안에 조건 추가 |
| **산술/집계 연산** | sum, count, topk 등 |
| **시간 기반 조회** | offset, \[1m\] 문법 |
| **차트 시각화** | Graph 탭에서 확인 가능 |