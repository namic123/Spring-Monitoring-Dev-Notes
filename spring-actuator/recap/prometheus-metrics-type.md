## Spring Boot 모니터링 (3) - Prometheus 메트릭 유형: 게이지(Gauge)와 카운터(Counter)의 차이와 활용법

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-3-Prometheus-%EB%A9%94%ED%8A%B8%EB%A6%AD-%EC%9C%A0%ED%98%95-%EA%B2%8C%EC%9D%B4%EC%A7%80Gauge%EC%99%80-%EC%B9%B4%EC%9A%B4%ED%84%B0Counter%EC%9D%98-%EC%B0%A8%EC%9D%B4%EC%99%80-%ED%99%9C%EC%9A%A9%EB%B2%95

## 1\. Prometheus의 메트릭 분류

Prometheus는 시스템의 상태를 시계열 데이터로 수집하여 분석하는 도구이며, 메트릭은 그 특성에 따라 크게 두 가지로 구분된다. 바로 **게이지(Gauge)와 카운터(Counter)이다**. 이러한 분류는 메트릭의 시간에 따른 변화 양상을 바르게 이해하는 데 핵심적인 기준이 된다.

각 유형은 분석 방식이 다르며, 사용하는 함수와 시각화 방식도 달라지므로 사전에 그 차이를 명확히 숙지해 두는 것이 좋다.

## 2\. 게이지(Gauge): 변동 가능한 상태 표현

게이지는 시간에 따라 값이 증가하거나 감소할 수 있는 **현재 상태를 나타내는 메트릭**이다. 자원 사용률, 연결 수, 대기열 길이 등 시스템의 상태를 실시간으로 보여주는 데 유용하다.

예를 들어 다음과 같은 메트릭이 이에 해당한다

-   **system\_cpu\_usage**: 현재 CPU 사용률
-   **jvm\_memory\_used\_bytes:** JVM 메모리 사용량
-   **db\_active\_connections**: 데이터베이스 활성 커넥션 수

게이지 메트릭은 수집된 시점의 값을 그대로 시각화함으로써, 자원의 변화 흐름을 직관적으로 파악할 수 있다. 또한, 경보 설정 시 임계값을 기준으로 비교가 가능하여 모니터링 대상의 **‘상태’를 나타내는 데 적합**하다.

[##_Image|kage@ux6kM/btsOl0rKJzq/ayY3t5IR0OWG2M1JfFRSa1/img.png|CDM|1.3|{"originWidth":1884,"originHeight":818,"style":"alignCenter","caption":"cpu usage 현재 상태 시각화"}_##]

## 3\. 카운터(Counter): 누적되는 이벤트 추적

카운터는 **값이 단조롭게 증가하는 메트릭**으로, 시스템 내에서 **특정 이벤트가 발생할 때마다 값을 1씩 증가**시킨다. 한 번 올라간 값은 다시 감소하지 않으며, 시스템이 재시작되기 전까지는 **지속적으로 누적**된다.

대표적인 카운터 메트릭은 다음과 같다

-   http\_server\_requests\_seconds\_count: HTTP 요청 수
-   log\_error\_count: 오류 로그 발생 횟수
-   job\_failures\_total: 배치 작업 실패 횟수

[##_Image|kage@be5Jgv/btsOphEOQ1J/Uz3ntoBoialrk7QfSzyVAK/img.png|CDM|1.3|{"originWidth":1878,"originHeight":806,"style":"alignCenter","caption":"http server request 요청 count"}_##]

-   **~15:14 : 약 65 건 요청 누적**
-   **15:15 ~ : 약 100건 요청 누적**

## 4\. 증가량 분석을 위한 함수들

카운터 메트릭의 시간 단위별 분석을 위해 Prometheus는 세 가지 주요 함수를 제공한다

-   **increase(metric\[시간\]):** 지정된 시간 동안 카운터 값이 얼마나 증가했는지를 계산한다.
-   **rate(metric\[시간\]):** 범위 내 총 증가량을 시간(초)으로 나눠 평균 증가율을 계산한다.
-   **irate(metric\[시간\])**: 가장 최근 두 샘플만을 기준으로 하여, 순간 증가율을 측정한다.

#### **예시**

**increase**

increase()는 **특정 시간 범위 내에서의 총 증가량**을 계산한다. 이를 통해 특정 시간에 실제로 몇 번의 이벤트가 발생했는지를 확인할 수 있다.

```
increase(http_server_requests_seconds_count{uri="/log"}[1m])
```

[##_Image|kage@b0GVeC/btsOooSfUiU/uvB5tQ0eurRTlNgjKZnxBK/img.png|CDM|1.3|{"originWidth":1892,"originHeight":808,"style":"alignCenter"}_##]

**rate**

rate()는 increase()와 유사하지만, **단위 시간당 평균 증가율**을 구한다. 예를 들어 60초 범위라면 그 안의 평균 증가량을 초 단위로 나눈 수치이다.

```
rate(http_server_requests_seconds_count{uri="/log"}[2m])
```

[##_Image|kage@bAlYT5/btsOmXBBhf8/pdum4DGy3m3syEqmdvXB60/img.png|CDM|1.3|{"originWidth":1882,"originHeight":788,"style":"alignCenter"}_##]

**irate**

irate()는 rate()와 유사하지만, 범위 벡터에서 가장 **최근 2개 데이터 포인트만을 사용하여 순간 증가율**을 계산한다. 데이터의 급격한 변동을 감지할 때 유용하다.

```
irate(http_server_requests_seconds_count{uri="/log"}[30s])
```

[##_Image|kage@bktwKX/btsOosAjFM9/9KbdSmCcpYmzXsJhScyLZ0/img.png|CDM|1.3|{"originWidth":1894,"originHeight":814,"style":"alignCenter"}_##]

-   increase()는 전체 누적값에서 구간 단위 증분을 도출하고,
-   rate()는 트래픽의 평균 흐름을,
-   irate()는 급격한 변화 감지를 위해 사용된다.

## 5\. 메트릭 유형 정리

| **유형** | **특성** | **대표 예시** | **사용 방식** |
| --- | --- | --- | --- |
| **게이지 (Gauge)** | 시시각각 변동 | CPU 사용률, 메모리 사용량 | 값 그대로 시각화 |
| **카운터 (Counter)** | 단조 증가, 누적 | HTTP 요청 수, 로그 발생 수 | increase(), rate() 등으로 시간 단위 분석 |