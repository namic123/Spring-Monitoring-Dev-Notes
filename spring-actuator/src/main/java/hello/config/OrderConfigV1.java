package hello.config;

import hello.service.OrderService;
import hello.service.OrderServiceV1;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfigV1 {
    @Bean
    OrderService orderService(MeterRegistry registry) {
        return new OrderServiceV1(registry);
    }
}