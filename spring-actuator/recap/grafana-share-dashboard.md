## Spring Boot 모니터링 (5) - 실무에서 바로 쓰는 Grafana 공유 대시보드 활용

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-5-%EC%8B%A4%EB%AC%B4%EC%97%90%EC%84%9C-%EB%B0%94%EB%A1%9C-%EC%93%B0%EB%8A%94-Grafana-%EA%B3%B5%EC%9C%A0-%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C-%ED%99%9C%EC%9A%A9

## 1\. 공유 대시보드 활용 목적

운영 시스템을 관찰할 수 있는 대시보드를 일일이 수동으로 구성하는 일은 많은 시간과 경험을 필요로 한다. 특히 Spring Boot 애플리케이션을 기반으로 Micrometer 메트릭을 Prometheus에 저장하고 이를 Grafana에서 시각화하는 경우, 각 지표마다 적절한 쿼리와 시각화 방식이 다르기 때문에 처음부터 직접 구성하는 것은 꽤나 까다로운 작업이다.

이러한 부담을 덜어주는 가장 효과적인 방법은 **Grafana의 공유 대시보드(Import Dashboard)** 기능을 활용하는 것이다. 이미 커뮤니티나 기업에서 검증한 대시보드를 가져와 사용하는 방식으로, 설정 시간은 줄이고 시각화 품질은 높일 수 있다.

아래는 Grafana 공유 대시보드 링크다.

[https://grafana.com/grafana/dashboards/](https://grafana.com/grafana/dashboards/) 

 [Grafana dashboards | Grafana Labs

No results found. Please clear one or more filters.

grafana.com](https://grafana.com/grafana/dashboards/)

[##_Image|kage@b6qdqx/btsOsevqFpn/53cP6h2stXK2WThFBP3RZ1/img.png|CDM|1.3|{"originWidth":2532,"originHeight":1248,"style":"alignCenter"}_##]

## 2\. 공유 대시보드 찾기

Grafana는 수많은 사용자들이 제작한 대시보드를 공유하고 있으며, 공식적으로 공유 대시보드 사이트를 운영 중이다. 아래 키워드를 활용하면 Spring Boot, JVM, Micrometer와 관련된 유용한 대시보드를 쉽게 찾을 수 있다.

-   **검색어 예: spring, micrometer, jvm, tomcat**
-   **유용한 예시:**
    -   **Spring Boot System Monitor**
        -   URL: [https://grafana.com/grafana/dashboards/11378](https://grafana.com/grafana/dashboards/11378)
        -   ID: 11378
    -   **JVM Micrometer Dashboard**
        -   URL: [https://grafana.com/grafana/dashboards/4701](https://grafana.com/grafana/dashboards/4701)
        -   ID: 4701

## 3\. Grafana에서 대시보드 가져오기

공유 대시보드는 몇 단계의 클릭만으로 바로 불러올 수 있다. 과정은 다음과 같다

1.  **http://localhost:3000으로 접속하여 Grafana에 로그인**
2.  **상단 우측 → 더하기 버튼 (+) → Import Dashboard 선택**
3.  **공유된 대시보드의 ID(예: 11378)를 입력한 후, Load 버튼 클릭**
4.  **연결할 데이터 소스 선택 (대부분 Prometheus)**
5.  **Import** 버튼을 누르면 완료

[##_Image|kage@2lxa8/btsOsP2Vnfs/UmZAA9CMORiFNSzK9INh30/img.png|CDM|1.3|{"originWidth":1938,"originHeight":1248,"style":"alignCenter"}_##][##_Image|kage@cz75Dj/btsOrW9OjgA/6IISiWOdsPAyiCE9jerMLK/img.png|CDM|1.3|{"originWidth":1884,"originHeight":1060,"style":"alignCenter"}_##][##_Image|kage@BAgVk/btsOsEUQvF2/aHCvRQk2DbSByBsZWrVnfK/img.png|CDM|1.3|{"originWidth":1228,"originHeight":1102,"style":"alignCenter"}_##][##_Image|kage@2D5gr/btsOsj4tHqT/gHX7EdZkn8JAkTwI5XOKbk/img.png|CDM|1.3|{"originWidth":2048,"originHeight":1232,"style":"alignCenter"}_##][##_Image|kage@dD1oKE/btsOrzAizN4/ETT0BLwW5G6mkkB2sobkI0/img.png|CDM|1.3|{"originWidth":2068,"originHeight":1260,"style":"alignCenter"}_##]

## 4\. 패널 수정과 Tomcat 대응

공유 대시보드 중 일부는 Jetty 서버를 기준으로 작성된 항목이 포함되어 있을 수 있다. Spring Boot를 Tomcat 기반으로 운영하고 있는 경우, 이를 적절히 수정하는 것이 필요하다.

예를 들어, 다음과 같이 패널 이름과 쿼리를 변경해주는 것이 좋다

| **항목** | **기존 설정 (Jetty)** | **변경 후 설정** |
| --- | --- | --- |
| **제목** | Jetty Statistics | Tomcat Statistics |
| **설정 스레드 수** | jetty\_threads\_config\_max | tomcat\_threads\_config\_max\_threads |
| **현재 스레드** | jetty\_threads\_current | tomcat\_threads\_current\_threads |
| **사용 중 스레드** | jetty\_threads\_busy | tomcat\_threads\_busy\_threads |
| **불필요한 항목** | jetty\_threads\_idle, jetty\_threads\_jobs | 삭제 가능 |

#### **쿼리 변경 예시**

**Thread Config Max > Edit**

[##_Image|kage@3BwAM/btsOthdNgV0/KaIuYgQ1Gn1Uv6iaIYClM0/img.png|CDM|1.3|{"originWidth":2056,"originHeight":550,"style":"alignCenter"}_##]

**jetty\_threads\_config\_max > tomcat\_threads\_config\_max\_threads**

[##_Image|kage@XppS8/btsOrEnPkQu/iTKhTbUIphrFsxH5pc0E80/img.png|CDM|1.3|{"originWidth":2018,"originHeight":1064,"style":"alignCenter"}_##]

## 5\. 정리

-   Grafana는 **대시보드 템플릿 공유 기능**을 통해 실무에서 자주 쓰이는 모니터링 레이아웃을 빠르게 구성할 수 있다.
-   공유 대시보드는 직접 패널을 구성하지 않아도 되는 이점이 있으며, 필요 시 원하는 메트릭만 수정하거나 교체해서 **맞춤형 대시보드**로 사용할 수 있다.
-   특히 Spring Boot + Micrometer 조합이라면 Prometheus 연동과 함께 이런 템플릿을 적극 활용하는 것이 **운영 자동화 및 시각화 효율성** 면에서 유리하다.