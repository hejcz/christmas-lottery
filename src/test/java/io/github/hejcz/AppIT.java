package io.github.hejcz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hejcz.domain.registration.RegistrationFacade;
import io.github.hejcz.integration.email.OutgoingEmails;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application-dev.yml"})
@Testcontainers
public class AppIT {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.5-alpine")
            .withDatabaseName("santa")
            .withUsername("santa")
            .withPassword("example");
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @MockBean
    private OutgoingEmails outgoingEmails;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> String.format("jdbc:postgresql://localhost:%d/santa", postgres.getFirstMappedPort()));
    }

    @Autowired
    private RegistrationFacade registrationFacade;

    @Test
    void generatesMatchesOnceLotteryStarts() throws JsonProcessingException {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange("http://localhost:" + port + "/api/lottery", HttpMethod.DELETE,
                new HttpEntity<>(adminAuthHeaders()), Void.class);
        final ResponseEntity<Boolean> lotteryPerformed = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery/admin", HttpMethod.GET,
                new HttpEntity<>(adminAuthHeaders()), Boolean.class);
        Assertions.assertThat(lotteryPerformed.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(lotteryPerformed.getBody()).isEqualTo(false);

        // run lottery
        final ResponseEntity<Void> startLottery = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery", HttpMethod.PUT,
                new HttpEntity<>(List.of(1, 2, 3, 4), adminAuthHeaders()), Void.class);
        Assertions.assertThat(startLottery.getStatusCodeValue()).isEqualTo(200);

        // check match of user 1
        final ResponseEntity<String> checkBuyingOfUser1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery", HttpMethod.GET,
                new HttpEntity<>(authHeaders("User1:user1")), String.class);
        final JsonNode checkBuyingOfUser1Json = OBJECT_MAPPER.readTree(checkBuyingOfUser1.getBody());
        Assertions.assertThat(checkBuyingOfUser1.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(checkBuyingOfUser1Json.get("firstName").textValue()).isNotEqualTo("Name1");
        Assertions.assertThat(checkBuyingOfUser1Json.get("firstName").textValue()).startsWith("Name");
        Assertions.assertThat(checkBuyingOfUser1Json.get("lastName").textValue()).isNotEqualTo("Surname1");
        Assertions.assertThat(checkBuyingOfUser1Json.get("lastName").textValue()).startsWith("Surname");
        Assertions.assertThat(checkBuyingOfUser1Json.get("locked").booleanValue()).isFalse();
        Assertions.assertThat(checkBuyingOfUser1Json.get("wishes").isArray()).isTrue();
        Assertions.assertThat(checkBuyingOfUser1Json.get("wishes").size()).isEqualTo(0);
    }

    private LinkedMultiValueMap<String, String> adminAuthHeaders() {
        return authHeaders("Admin:admin");
    }

    private LinkedMultiValueMap<String, String> authHeaders(String loginAndPassword) {
        final LinkedMultiValueMap<String, String> authHeaders = new LinkedMultiValueMap<>();
        authHeaders.add("Authorization", adminBasicAUth(loginAndPassword));
        return authHeaders;
    }

    private String adminBasicAUth(String loginAndPassword) {
        return "Basic " + new String(Base64.getEncoder().encode(loginAndPassword.getBytes(StandardCharsets.UTF_8)));
    }
}
