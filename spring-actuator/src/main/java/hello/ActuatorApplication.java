package hello;

import hello.config.OrderConfigV0;
import hello.config.OrderConfigV1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(OrderConfigV1.class)
@SpringBootApplication(scanBasePackages = "hello.controller")
public class ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }

    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        InMemoryHttpExchangeRepository repo = new InMemoryHttpExchangeRepository();
        repo.setCapacity(200); // 최대 저장 요청 수를 200개로 설정
        return repo;
    }
}
