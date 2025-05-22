package hello;

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
        InMemoryHttpExchangeRepository repo = new InMemoryHttpExchangeRepository();
        repo.setCapacity(200); // 최대 저장 요청 수를 200개로 설정
        return repo;
    }
}
