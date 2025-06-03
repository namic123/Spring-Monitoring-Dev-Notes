## Spring Boot 모니터링 (4) - Grafana 설치부터 Prometheus 연동 및 대시보드 구성하기

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-4-Grafana-%EC%84%A4%EC%B9%98%EB%B6%80%ED%84%B0-Prometheus-%EC%97%B0%EB%8F%99-%EB%B0%8F-%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C-%EA%B5%AC%EC%84%B1%ED%95%98%EA%B8%B0

## 1\. Grafana란 무엇인가?

Grafana는 Prometheus, InfluxDB, Elasticsearch와 같은 모니터링 지향 데이터 소스에서 메트릭 데이터를 수집하고, 이를 시각화하여 분석 가능한 형태로 제공하는 **오픈소스 기반 대시보드 플랫폼**이다. 단순히 시각화 도구에 그치지 않고, **알림 기능 및 사용자 권한 관리 기능을 제공**함으로써 실시간 시스템 모니터링과 운영 이슈 대응에 최적화된 솔루션으로 많이 활용되고 있다.

주요 기능은 다음과 같다

-   **다양한 데이터 소스 지원** (Prometheus, PostgreSQL, MySQL 등)
-   **시각적 대시보드 생성** (그래프, 게이지, 테이블 등)
-   **PromQL, SQL, InfluxQL** 등 다양한 쿼리 언어 지원
-   Slack, Email 등으로 **실시간 알림 설정 가능**
-   **사용자/팀 기반 권한 분리와 협업 환경 제공**

#### **대표 사용 시나리오**

| **시나리오** | **설명** |
| --- | --- |
| **시스템 상태 모니터링** | CPU, 메모리, 디스크, 네트워크 사용량 등 리소스 모니터링 |
| **애플리케이션 지표 시각화** | HTTP 요청 수, 응답 시간, 예외 발생 수 등 Micrometer 기반 메트릭 |
| **인프라 알림 설정** | 장애 발생 시 Slack, Email, PagerDuty 등을 통한 실시간 알림 |
| **비즈니스 지표 대시보드** | 실시간 사용자 수, 트랜잭션 수, 판매량 등 도메인 데이터 시각화 |

#### **주요 컴포넌트**

| **구성 요소** | **설명** |
| --- | --- |
| **Dashboard** | 전체 레이아웃 단위의 시각화 보드 |
| **Panel** | 각 메트릭을 시각화하는 단위 컴포넌트 |
| **Query** | 패널 안에 들어가는 쿼리. PromQL, SQL 등 사용 |
| **Data Source** | 데이터를 가져오는 소스 설정 (예: Prometheus, PostgreSQL) |
| **Alert** | 조건에 따라 알림을 보낼 수 있는 기능 |

## 2\. Grafana 설치 및 실행

Grafana는 공식 웹사이트에서 운영체제에 맞는 설치 파일을 제공한다.

-   [https://grafana.com/grafana/download](https://grafana.com/grafana/download)
-   기본 실행 포트: http://localhost:3000
-   초기 로그인 정보:
    -   ID: admin
    -   PW: admin (최초 로그인 시 변경 요구)

**Windows 사용자는** .zip 파일을 내려받은 후, bin/grafana-server.exe를 실행하면 된다.  
**macOS 사용자는** .tar.gz 압축 해제 후, 터미널에서 ./grafana-server 실행이 가능하다.

#### **1\. Grafana 설치 (OS에 맞게 설치)**

[##_Image|kage@BUWfq/btsOobMm7mr/GuxURO4OfWuBUTTWkk4pw1/img.png|CDM|1.3|{"originWidth":2532,"originHeight":1398,"style":"alignCenter"}_##]

#### **2\. Grafana 실행** 

-   압축 해제 후 bin/ 디렉토리로 이동
-   grafana-server.exe 실행
-   Windows SmartScreen이 차단할 경우:
    -   “추가 정보” → “실행”을 클릭하여 허용

[##_Image|kage@bKdSM8/btsOovcJqVC/8vDhNgTRKxBBRGKrNmHOEk/img.png|CDM|1.3|{"originWidth":560,"originHeight":288,"style":"alignCenter"}_##][##_Image|kage@ctJbws/btsOoqbvzAw/xoNjsw6bXY6W5XhTPPL73K/img.png|CDM|1.3|{"originWidth":1156,"originHeight":676,"style":"alignCenter"}_##]

#### **3\. 그라파나 접속**

-   브라우저에서 접속: [http://localhost:3000](http://localhost:3000)
-   기본 로그인 정보:
    -   **Username:** admin
    -   **Password:** admin

[##_Image|kage@RzQ3N/btsOm9n2mQ3/0EZQZKK1U7B1eXn0gKIUuk/img.png|CDM|1.3|{"originWidth":2108,"originHeight":1280,"style":"alignCenter"}_##][##_Image|kage@OAa46/btsOobFEats/6npgXMG7za1FzkRe2TI4vk/img.png|CDM|1.3|{"originWidth":2528,"originHeight":1142,"style":"alignCenter"}_##]

설치 이후 브라우저에서 접속 시, ‘Welcome to Grafana’ 메시지를 통해 성공 여부를 확인할 수 있다.

## 3\. Prometheus와의 연동 설정

Grafana는 자체적으로 데이터를 저장하지 않기 때문에, 외부 데이터 소스를 연결해야 한다. 대표적인 연동 대상인 Prometheus와 연결하는 방법은 다음과 같다.

-   **Grafana 접속 후 좌측 하단 Connections - Data sources - Add data source 버튼 클릭**
-   **Prometheus 선택**
-   **URL 입력: http://localhost:9090 (Prometheus가 로컬에서 실행 중일 경우)**
-   **Save & Test 클릭하여 연결 테스트**

**1\. Grafana 접속 후 좌측 하단 Connections - Data sources - Add data source**

[##_Image|kage@brziuP/btsOn0YEJVp/tei7dKGbA0LxXZLolnwbh0/img.png|CDM|1.3|{"originWidth":2340,"originHeight":1222,"style":"alignCenter"}_##]

#### **2\. Prometheus 선택**

[##_Image|kage@dxeoxS/btsOpe9cWOE/KYA7dN4tnv0Ufb5LkWoBHK/img.png|CDM|1.3|{"originWidth":2402,"originHeight":920,"style":"alignCenter"}_##]

#### **3\. URL 입력: http://localhost:9090 (Prometheus가 로컬에서 실행 중일 경우)**

[##_Image|kage@diABQH/btsOmtm7vd5/ngdiaGqmZ9A3mIgHm42odK/img.png|CDM|1.3|{"originWidth":1900,"originHeight":1198,"style":"alignCenter"}_##]

#### **4\. Save & Test 클릭하여 연결 테스트**

[##_Image|kage@5DNrY/btsOosG5EAw/sitZc2tj3M7eJTG5UDZAQK/img.png|CDM|1.3|{"originWidth":1816,"originHeight":1074,"style":"alignCenter"}_##]

## 4\. 대시보드 및 패널 구성 방법

대시보드는 사용자가 직접 원하는 **메트릭을 시각화할 수 있는 보드**이며, 내부에는 하나 이상의 **패널(Panel)이 존재한다**. 각 패널은 하나의 메트릭을 시각화하는 단위 요소이다.

예를 들어, CPU 사용량을 모니터링하는 경우 다음과 같은 절차를 따른다

#### **대시보드 생성**

1.  왼쪽 메뉴에서 **"Dashboards"** 메뉴 클릭 - **Create dashboard** 선택
2.  오른쪽 상단의 **저장 버튼(디스크 아이콘)** 클릭
3.  **대시보드 이름**: hello dashboard 입력 후 저장

#### **1\. 왼쪽 메뉴에서 "Dashboards" 메뉴 클릭 - Create dashboard 선택**

[##_Image|kage@rVPO2/btsOmKhIAYU/kYqlz0mtz2SzitGyidu23k/img.png|CDM|1.3|{"originWidth":1372,"originHeight":726,"style":"alignCenter"}_##]

#### **2\. 오른쪽 상단의 저장 버튼(디스크 아이콘) 클릭**

[##_Image|kage@cuUKMl/btsOobeBty1/4tnI3WmNNqgNhs1ccHHXo0/img.png|CDM|1.3|{"originWidth":1612,"originHeight":744,"style":"alignCenter"}_##]

#### **3\. 대시보드 이름: hello dashboard 입력 후 저장**

[##_Image|kage@devz5X/btsOmM7ID8a/ekYv46fM9LglKN5mdVvF4k/img.png|CDM|1.3|{"originWidth":1616,"originHeight":464,"style":"alignCenter"}_##][##_Image|kage@c076Fo/btsOoikujsm/T7p7ak86nRfzE7vHXXmrQK/img.png|CDM|1.3|{"originWidth":1894,"originHeight":286,"style":"alignCenter"}_##]

#### **패널(Panels) 추가**

**패널**은 대시보드 내에서 실제 데이터를 표현하는 시각화 단위다. 각각의 메트릭에 대해 독립적인 패널을 구성할 수 있다.

1.  생성된 hello dashboard 클릭후, Add visuialzation 선택
2.  앞서, 설정한 데이터소스(prometheus-1) 선택
3.  상단 우측 **시각화 패널 (timeseries) 선택 -  하단 Code (Promql로 직접 입력) 선택 - Save dashboard**
4.  **메세지 입력 후, Save**
5.  **상단, Back to dashboard 클릭하면 패널이 추가되어 있음**

#### **1\. 생성된 hello dashboard 클릭후, Add visuialzation 선택**

[##_Image|kage@cpvCgt/btsOpcwNXHT/QemMtNKfLr8rmAAjGd0c71/img.png|CDM|1.3|{"originWidth":1700,"originHeight":794,"style":"alignCenter"}_##]

#### **2\. 앞서, 설정한 데이터소스(prometheus-1) 선택**

[##_Image|kage@rNfja/btsOokvL5H8/D1RncXejgfSMXUMZOBaW60/img.png|CDM|1.3|{"originWidth":1904,"originHeight":864,"style":"alignCenter"}_##]

#### **3.상단 우측 시각화 패널 (timeseries) 선택 -  하단 Code (Promql로 직접 입력) 선택 - Save dashboard**

[##_Image|kage@zIzcA/btsOoHKNX3J/nqctKGy4drUePT8A4XKu51/img.png|CDM|1.3|{"originWidth":1618,"originHeight":868,"style":"alignCenter"}_##]

#### **4\. 메세지 입력 후, Save**

[##_Image|kage@bxuG3u/btsOoa7MwC8/a43wRwrQsQe5D5f0k26ZA1/img.png|CDM|1.3|{"originWidth":1622,"originHeight":446,"style":"alignCenter"}_##]

#### **5\. 상단, Back to dashboard 클릭하면 패널이 추가되어 있음**

[##_Image|kage@kyLCl/btsOommNWcB/1kYFqOPA0Xvsyzh8jrqBx0/img.png|CDM|1.3|{"originWidth":1618,"originHeight":542,"style":"alignCenter"}_##][##_Image|kage@b5HZFt/btsOosUBVd7/8lx8dXHITh8q7E5vrboE71/img.png|CDM|1.3|{"originWidth":1622,"originHeight":544,"style":"alignCenter"}_##]

#### **추가로 디스크 사용량 그래프 promql 방식이 아닌 Builder로 추가해보기** 

1.  Add - Visualization
2.  패널 설정
    -   Visualization: Time series
    -   Title: 디스크 사용량
    -   하단 Queries에서 Datasource 설정 (prometheus-1)
    -   Builder 선택 후, 책 모양 버튼 클릭
3.  원하는 메트릭 검색 - Select
4.  Save dashboard

#### **1\. Add - Visualization**

[##_Image|kage@y8x21/btsOoaGKl7o/XJYJT7kvoWdW7t94VHKKo0/img.png|CDM|1.3|{"originWidth":1594,"originHeight":474,"style":"alignCenter"}_##]

#### **2.패널 설정**

-   Visualization: Time series
-   Title: 디스크 사용량
-   하단 Queries에서 Datasource 설정 (prometheus-1)
-   Builder 선택 후, 책 모양 버튼 클릭

[##_Image|kage@d1yLEX/btsOoRTXSv7/SCJ8kysMtMsdW9res4Ny40/img.png|CDM|1.3|{"originWidth":1618,"originHeight":892,"style":"alignCenter"}_##]

#### **3\. 원하는 메트릭 검색 - Select**

[##_Image|kage@3tKTS/btsOmKPuYHY/jtkSRXHUApmy8E4E4GTYy0/img.png|CDM|1.3|{"originWidth":1398,"originHeight":858,"style":"alignCenter"}_##]

#### **4\. Save dashboard**

[##_Image|kage@b74OK3/btsOofnKMag/gDkQ8AlrMHd6uY4yD8UPI0/img.png|CDM|1.3|{"originWidth":1598,"originHeight":834,"style":"alignCenter"}_##]

#### **대시보드에 추가된 패널**

[##_Image|kage@n44x1/btsOoOC8kO5/qx9FN1mJM0RKFd7Rjq8mQk/img.png|CDM|1.3|{"originWidth":1620,"originHeight":530,"style":"alignCenter"}_##]

## 5\. 정리 및 다음 단계

대시보드에는 다음 항목이 시각화된 상태다.

-   **CPU 사용량 (system\_cpu\_usage)**
-   **디스크 사용량 (disk\_total\_bytes)**

이후에는 아래 메트릭들을 추가하여 더욱 풍부한 모니터링 환경을 구성할 수 있다

-   **JVM 메트릭**
-   **시스템 메트릭**
-   **애플리케이션 시작 시간 메트릭**
-   **스프링 MVC 요청 메트릭**
-   **톰캣 메트릭**
-   **데이터 소스(HikariCP 등) 메트릭**
-   **로그 발생 수 메트릭 등**

Grafana는 단순한 시각화 도구를 넘어서, **실시간 모니터링**, **알림 기반 이상 감지**, **운영 데이터 기반 분석**까지 가능하게 해주는 **DevOps 필수 플랫폼**이다. 특히 Prometheus와의 연동을 통해 Spring Boot 애플리케이션의 Micrometer 메트릭을 효과적으로 시각화할 수 있으며, 운영 현장에서 시스템 상태를 직관적으로 파악하고 문제 발생 시 빠르게 대응할 수 있는 기반을 마련할 수 있다.