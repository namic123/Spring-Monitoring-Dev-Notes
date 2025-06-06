## Spring Boot 모니터링 (6) - Spring Boot 실무 장애 대응: Grafana 메트릭 기반 문제 추적 사례

블로그 : https://pjs-world.tistory.com/entry/Spring-Boot-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-6-Spring-Boot-%EC%8B%A4%EB%AC%B4-%EC%9E%A5%EC%95%A0-%EB%8C%80%EC%9D%91-Grafana-%EB%A9%94%ED%8A%B8%EB%A6%AD-%EA%B8%B0%EB%B0%98-%EB%AC%B8%EC%A0%9C-%EC%B6%94%EC%A0%81-%EC%82%AC%EB%A1%80

## 1\. 개요

시스템이나 애플리케이션에서 장애 혹은 성능 저하가 발생하였을 때, 전통적인 방식은 로그 분석에 의존하는 것이 일반적이었다. 그러나 로그만으로는 근본적인 원인 파악이 어렵거나 늦어지는 경우가 많다.  
이때 Grafana를 활용하면, 실시간 메트릭을 기반으로 시스템 자원의 상태 변화를 직관적으로 관찰할 수 있어 문제를 조기에 탐지하고 대응할 수 있는 강력한 수단이 된다.

## 2\. CPU 사용량 초과 사례

다음은 CPU 부하가 급격히 상승하는 상황을 유도하는 예제 코드이다

```
@GetMapping("/cpu")
public String cpu() {
    log.info("cpu");
    long value = 0;
    for (long i = 0; i < 100000000000L; i++) {
        value++;
    }
    return "ok value=" + value;
}
```

해당 API(/cpu)를 호출하면 단일 코어가 100% 가까이 점유되며, system\_cpu\_usage와 process\_cpu\_usage 메트릭이 급격히 상승하는 현상을 Grafana에서 확인할 수 있다.

이러한 부하 테스트는 성능 튜닝 시 병목 지점을 파악하거나 자원 한계를 미리 확인하는 데 유용하게 활용된다.

**Postman 테스트**

[##_Image|kage@9xUwx/btsOsuku41B/Xg7soteAeb8Lx4kN15ei80/img.png|CDM|1.3|{"originWidth":1282,"originHeight":752,"style":"alignCenter"}_##][##_Image|kage@43GCw/btsOsurfN2M/VGdwkDtO9UXTEQjkpClmkk/img.png|CDM|1.3|{"originWidth":2028,"originHeight":822,"style":"alignCenter"}_##]

## 3\. JVM 메모리 초과 사례

다음 예제는 의도적으로 Heap 메모리를 지속적으로 소비하는 방식이다

```
private List<String> list = new ArrayList<>();

@GetMapping("/jvm")
public String jvm() {
    log.info("jvm");
    for (int i = 0; i < 1_000_000; i++) {
        list.add("hello jvm!" + i);
    }
    return "ok";
}
```

해당 API(/jvm)를 여러 차례 호출하면 **jvm\_memory\_used\_bytes** 메트릭이 점진적으로 증가하는 모습을 확인할 수 있으며, 이는 메모리 릭(memory leak) 또는 GC 설정의 한계를 사전에 진단하는 데 중요한 지표가 된다. 심할 경우 **OutOfMemoryError**가 발생할 수 있으며, 실시간 모니터링을 통해 이를 사전에 방지할 수 있다.

**여러 번 요청하고 JVM 메모리 사용량 확인 테스트**

[##_Image|kage@dBucGW/btsOqT6T7nB/aybmbY9RA7FT5244GRs0Xk/img.png|CDM|1.3|{"originWidth":1276,"originHeight":910,"style":"alignCenter"}_##][##_Image|kage@w7eNY/btsOsSrRQt9/Pe8aWCM1CChYDiQAA9ofh1/img.png|CDM|1.3|{"originWidth":1974,"originHeight":956,"style":"alignCenter"}_##]

## 4\. 커넥션 풀 고갈 사례

다음은 JDBC 커넥션을 반납하지 않는 예제이다

```
    @Autowired
    DataSource dataSource;

    @GetMapping("/jdbc")
    public String jdbc() throws SQLException {
        log.info("jdbc");
        Connection conn = dataSource.getConnection(); // close 안함!
        log.info("connection info={}", conn);
        return "ok";
    }
```

이 API(/jdbc)를 반복적으로 호출하면 커넥션이 반환되지 않음으로 인해 **hikaricp\_active\_connections**, **hikaricp\_pending\_connections** 지표가 급격히 증가하게 된다. 결국 풀(pool)이 고갈되면 다음과 같은 예외가 발생한다

**여러 번 요청**

[##_Image|kage@ogam5/btsOtftBqa6/xBaAzKsfXtrIEvzT3qp5g1/img.png|CDM|1.3|{"originWidth":1268,"originHeight":742,"style":"alignCenter"}_##][##_Image|kage@cbzmbS/btsOtn5ZPFn/H5LGjFOeKwgrVkYajt4YBK/img.png|CDM|1.3|{"originWidth":1786,"originHeight":834,"style":"alignCenter"}_##]

**총 Connection 사이즈, Connect timeout 횟수, 현재 활성화된 Connection 확인** 

[##_Image|kage@TjHT8/btsOssAa6zA/7uscCtRs3XbTF7eBfnKY0K/img.png|CDM|1.3|{"originWidth":2120,"originHeight":774,"style":"alignCenter"}_##]

이러한 상황은 실시간 모니터링이 없으면 운영 중 매우 치명적인 병목을 유발할 수 있다. Grafana를 통해 해당 메트릭을 경고 조건으로 설정하면 조기 대응이 가능하다.

## 5\. 에러 로그 급증 사례

아래 코드는 단순한 에러 로그를 무한하게 쌓는 예시이다

```
@GetMapping("/error-log")
public String errorLog() {
    log.error("error log");
    return "error";
}
```

해당 API(/error-log)를 다수 호출하면 logback\_events\_total{level="error"} 메트릭이 빠르게 상승하게 되며, 이는 장애 조짐이나 예외 핸들링 미비 상태를 사전에 감지할 수 있는 신호가 된다.

[##_Image|kage@bGN2BB/btsOsOQwlvL/gXkNcZZdl5gSA06AvcdILk/img.png|CDM|1.3|{"originWidth":1266,"originHeight":742,"style":"alignCenter"}_##][##_Image|kage@beqcuI/btsOqEvnB5G/8AOLZPMSt1JOX8xw5teAw0/img.png|CDM|1.3|{"originWidth":2104,"originHeight":776,"style":"alignCenter"}_##]

## 6\. 요약 및 실무 적용

Grafana를 활용한 메트릭 기반 문제 탐지는 단순한 시각화를 넘어 **운영 현장의 이상 징후를 실시간으로 조기에 파악**할 수 있는 체계를 구축하는 데 중대한 역할을 한다. 아래 표는 본문에서 다룬 예제와 해당 메트릭의 대응 관계를 정리한 것이다

| **문제 유형** | **원인** | **대표 메트릭** | **대응 포인트** |
| --- | --- | --- | --- |
| **CPU 부하** | 무한 루프 | cpu\_usage | 병목 테스트, 자원 제한 점검 |
| **JVM 메모리 초과** | List 누적 | jvm\_memory\_\* | GC 튜닝, 메모리 릭 분석 |
| **커넥션 풀 고갈** | 커넥션 미반납 | hikaricp\_\* | try-with-resources 사용 권장 |
| **에러 로그 폭증** | 반복 에러 발생 | logback\_events\_total | 예외 처리 로직 점검 |