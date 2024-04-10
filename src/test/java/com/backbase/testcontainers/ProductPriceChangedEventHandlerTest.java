package com.backbase.testcontainers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.backbase.testcontainers.domain.Product;
import com.backbase.testcontainers.repository.ProductRepository;
import com.backbase.testcontainerssamples.event.spec.v1.ProductPriceChangedEvent;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@TestPropertySource(
    properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.datasource.url=jdbc:tc:mysql:8.0.32:///db",
    }
)
@Testcontainers
@ActiveProfiles("it")
@Slf4j
public class ProductPriceChangedEventHandlerTest {
    @Container
    static final KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.1")
    );

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private EventEmitter<ProductPriceChangedEvent> productPriceChangedEventEventEmitter;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Product product = new Product(null, "P100", "Product One", BigDecimal.TEN);
        productRepository.save(product);
    }

    @Test
    void shouldHandleProductPriceChangedEvent() {
        ProductPriceChangedEvent event = new ProductPriceChangedEvent();
        event.withPrice("14.50")
            .withCode("P100");

        productPriceChangedEventEventEmitter.sendMessage(event);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(10, SECONDS)
            .untilAsserted(() -> {
                Optional<Product> optionalProduct = productRepository.findByCode("P100");

                assertThat(optionalProduct).isPresent();
                log.info("Product {}", optionalProduct.get());
                assertThat(optionalProduct.get().getCode()).isEqualTo("P100");
                assertThat(optionalProduct.get().getPrice())
                    .isEqualTo(new BigDecimal("14.50"));
            });
    }
}