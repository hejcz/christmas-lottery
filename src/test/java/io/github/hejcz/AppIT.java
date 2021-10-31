package io.github.hejcz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.WishListChange;
import io.github.hejcz.domain.registration.RegistrationFacade;
import io.github.hejcz.integration.email.OutgoingEmails;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
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

    @MockBean
    private CsrfTokenRepository csrfTokenRepository;

    @BeforeEach
    void beforeEach() {
        Mockito.when(csrfTokenRepository.loadToken(Mockito.any()))
                .thenReturn(new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "csrftokenvalue"));
        Mockito.when(csrfTokenRepository.generateToken(Mockito.any()))
                .thenReturn(new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "csrftokenvalue"));
        Mockito.doNothing().when(csrfTokenRepository).saveToken(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void simpleLotteryFlow() throws JsonProcessingException {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange("http://localhost:" + port + "/api/lottery", HttpMethod.DELETE,
                new HttpEntity<>(adminHeaders()), Void.class);
        final ResponseEntity<Boolean> lotteryPerformed = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery/admin", HttpMethod.GET,
                new HttpEntity<>(adminHeaders()), Boolean.class);
        Assertions.assertThat(lotteryPerformed.getStatusCodeValue()).isEqualTo(200);
        Assertions.assertThat(lotteryPerformed.getBody()).isEqualTo(false);

        // run lottery
        final ResponseEntity<Void> startLottery = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery", HttpMethod.PUT,
                new HttpEntity<>(List.of(1, 2, 3, 4), adminHeaders()), Void.class);
        Assertions.assertThat(startLottery.getStatusCodeValue()).isEqualTo(200);

        // check match of user 1
        final ResponseEntity<String> checkBuyingOfUser1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery", HttpMethod.GET,
                new HttpEntity<>(headers("User1", "user1")), String.class);
        final JsonNode checkBuyingOfUser1Json = OBJECT_MAPPER.readTree(checkBuyingOfUser1.getBody());
        Assertions.assertThat(checkBuyingOfUser1.getStatusCodeValue()).isEqualTo(200);
        final String firstName = checkBuyingOfUser1Json.get("firstName").textValue();
        Assertions.assertThat(firstName).isNotEqualTo("Name1");
        Assertions.assertThat(firstName).startsWith("Name");
        Assertions.assertThat(checkBuyingOfUser1Json.get("lastName").textValue()).isNotEqualTo("Surname1");
        Assertions.assertThat(checkBuyingOfUser1Json.get("lastName").textValue()).startsWith("Surname");
        Assertions.assertThat(checkBuyingOfUser1Json.get("locked").booleanValue()).isFalse();
        Assertions.assertThat(checkBuyingOfUser1Json.get("wishes").isArray()).isTrue();
        Assertions.assertThat(checkBuyingOfUser1Json.get("wishes").size()).isEqualTo(0);

        // add some wishes
        final String matchingUserNumber = firstName.substring(firstName.length() - 1);
        final String wishesPayload = """
                [
                    {"title": "A", "url": "https://google.com", "power": 1},
                    {"title": "B", "power": 2}
                ]
                """;
        final ResponseEntity<Void> addedWishesOfMatchingUser = restTemplate.exchange(
                "http://localhost:" + port + "/api/users/current/wish-list", HttpMethod.PUT,
                new HttpEntity<>(wishesPayload, headers("User" + matchingUserNumber, "user" + matchingUserNumber)),
                Void.class);
        Assertions.assertThat(addedWishesOfMatchingUser.getStatusCodeValue()).isEqualTo(200);
        Mockito.verify(outgoingEmails).sendWishesUpdate("rubin94+01@`gmail.com",
                new WishListChange(List.of(), List.of(
                        new DtoWishRecipient(null, "B", null, 2),
                        new DtoWishRecipient(null, "A", "https://google.com", 1))));
        Mockito.reset(outgoingEmails);

        final ResponseEntity<String> getWishesOfMatchingUserByMatchingUser = restTemplate.exchange(
                "http://localhost:" + port + "/api/users/current/wish-list", HttpMethod.GET,
                new HttpEntity<>(headers("User" + matchingUserNumber, "user" + matchingUserNumber)),
                String.class);
        Assertions.assertThat(getWishesOfMatchingUserByMatchingUser.getStatusCodeValue()).isEqualTo(200);
        final JsonNode matchingUserWishes = OBJECT_MAPPER.readTree(getWishesOfMatchingUserByMatchingUser.getBody());
        Assertions.assertThat(matchingUserWishes.get("locked").asBoolean()).isFalse();
        Assertions.assertThat(matchingUserWishes.get("wishes").isArray()).isTrue();
        assertWish(matchingUserWishes.get("wishes").get(0), "A", "https://google.com", 1);
        assertWish(matchingUserWishes.get("wishes").get(1), "B", null, 2);

        // recheck match of user 1. In particular, we check whether wishes are returned.
        final ResponseEntity<String> recheckBuyingOfUser1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/lottery", HttpMethod.GET,
                new HttpEntity<>(headers("User1", "user1")), String.class);
        JsonNode recheckBuyingOfUser1Json = OBJECT_MAPPER.readTree(recheckBuyingOfUser1.getBody());
        Assertions.assertThat(recheckBuyingOfUser1Json.get("wishes").isArray()).isTrue();
        Assertions.assertThat(recheckBuyingOfUser1Json.get("wishes").size()).isEqualTo(2);
        assertWish(recheckBuyingOfUser1Json.get("wishes").get(0), "A", "https://google.com", 1);
        assertWish(recheckBuyingOfUser1Json.get("wishes").get(1), "B", null, 2);

        // update wishes
        final String updatedWishesPayload = """
                [
                    {"title": "B", "power": 2},
                    {"title": "C", "power": 1}
                ]
                """;
        final ResponseEntity<Void> updateWishesOfMatchingUser = restTemplate.exchange(
                "http://localhost:" + port + "/api/users/current/wish-list", HttpMethod.PUT,
                new HttpEntity<>(updatedWishesPayload, headers("User" + matchingUserNumber, "user" + matchingUserNumber)),
                Void.class);
        Assertions.assertThat(updateWishesOfMatchingUser.getStatusCodeValue()).isEqualTo(200);
        Mockito.verify(outgoingEmails).sendWishesUpdate("rubin94+01@`gmail.com",
                new WishListChange(
                        List.of(
                                new DtoWishRecipient(55, "B", null, 2),
                                new DtoWishRecipient(56, "A", "https://google.com", 1)),
                        List.of(
                                new DtoWishRecipient(null, "B", null, 2),
                                new DtoWishRecipient(null, "C", null, 1))));
    }

    private void assertWish(JsonNode firstWish, String expectedTitle, String expectedUrl, int expectedPower) {
        Assertions.assertThat(firstWish.get("id").isNull()).isFalse();
        Assertions.assertThat(firstWish.get("title").asText()).isEqualTo(expectedTitle);
        if (expectedUrl == null) {
            Assertions.assertThat(firstWish.get("url").isNull()).isTrue();
        } else {
            Assertions.assertThat(firstWish.get("url").asText()).isEqualTo(expectedUrl);
        }
        Assertions.assertThat(firstWish.get("power").asInt()).isEqualTo(expectedPower);
    }

    private LinkedMultiValueMap<String, String> adminHeaders() {
        return headers("Admin", "admin");
    }

    private LinkedMultiValueMap<String, String> headers(String login, String password) {
        final LinkedMultiValueMap<String, String> authHeaders = new LinkedMultiValueMap<>();
        authHeaders.add("Authorization", encodeCredentials(login, password));
        authHeaders.add("X-Forwarded-Proto", "https");
        authHeaders.add("Content-type", "application/json");
        authHeaders.add("X-XSRF-TOKEN", "csrftokenvalue");
        return authHeaders;
    }

    private String encodeCredentials(String login, String password) {
        return "Basic " + new String(Base64.getEncoder().encode((login + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }
}
