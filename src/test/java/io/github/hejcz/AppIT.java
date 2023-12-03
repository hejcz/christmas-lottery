package io.github.hejcz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.WishListChange;
import io.github.hejcz.integration.email.OutgoingEmails;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @LocalServerPort
    private int port;

    @MockBean
    private OutgoingEmails outgoingEmails;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> String.format("jdbc:postgresql://localhost:%d/santa", postgres.getFirstMappedPort()));
    }

    @MockBean
    private CsrfTokenRepository csrfTokenRepository;

    private LotteryAppHttpClient httpClient;

    @BeforeEach
    void beforeEach() {
        httpClient = new LotteryAppHttpClient(port);
        Mockito.when(csrfTokenRepository.loadToken(Mockito.any()))
                .thenReturn(new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "csrftokenvalue"));
        Mockito.when(csrfTokenRepository.generateToken(Mockito.any()))
                .thenReturn(new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "csrftokenvalue"));
        Mockito.doNothing().when(csrfTokenRepository).saveToken(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void simpleLotteryFlow() throws JsonProcessingException {
        // delete lottery
        httpClient.deleteLottery();

        // check there is no lottery
        final ResponseEntity<Boolean> lotteryStatusResponse = httpClient.getLotteryStatus();
        Assertions.assertThat(lotteryStatusResponse.getBody()).isEqualTo(false);

        // start lottery
        httpClient.startLottery(List.of(1, 2, 3, 4));

        // check match of user 1
        final ResponseEntity<JsonNode> user1MatchResponse = httpClient.getMatchOfUser(1);
        Assertions.assertThat(user1MatchResponse.getBody()).isNotNull();
        final String firstName = user1MatchResponse.getBody().get("firstName").textValue();
        Assertions.assertThat(firstName).isNotEqualTo("Name1");
        Assertions.assertThat(firstName).startsWith("Name");
        Assertions.assertThat(user1MatchResponse.getBody().get("lastName").textValue()).isNotEqualTo("Surname1");
        Assertions.assertThat(user1MatchResponse.getBody().get("lastName").textValue()).startsWith("Surname");
        Assertions.assertThat(user1MatchResponse.getBody().get("locked").booleanValue()).isFalse();
        Assertions.assertThat(user1MatchResponse.getBody().get("wishes").isArray()).isTrue();
        Assertions.assertThat(user1MatchResponse.getBody().get("wishes").size()).isEqualTo(0);

        // add some wishes to matched user wishlist
        final int matchingUserNumber = Integer.parseInt(firstName.substring(firstName.length() - 1));
        final String wishesPayload = """
                [
                    {"title": "A", "url": "https://google.com", "power": 1},
                    {"title": "B", "power": 2}
                ]
                """;
        httpClient.updateWishlist(matchingUserNumber, wishesPayload);
        Mockito.verify(outgoingEmails).sendWishesUpdate("rubin94+01@`gmail.com",
                new WishListChange(List.of(), List.of(
                        new DtoWishRecipient(null, "B", null, 2),
                        new DtoWishRecipient(null, "A", "https://google.com", 1))));
        Mockito.reset(outgoingEmails);

        // get self wishes as matched user
        final ResponseEntity<JsonNode> matchingUserWishesResponse = httpClient.getWishesOf(matchingUserNumber);
        Assertions.assertThat(matchingUserWishesResponse.getBody()).isNotNull();
        Assertions.assertThat(matchingUserWishesResponse.getBody().get("locked").asBoolean()).isFalse();
        Assertions.assertThat(matchingUserWishesResponse.getBody().get("wishes").isArray()).isTrue();
        assertWish(matchingUserWishesResponse.getBody().get("wishes").get(0), "A", "https://google.com", 1);
        assertWish(matchingUserWishesResponse.getBody().get("wishes").get(1), "B", null, 2);

        // recheck wishes of matched user as user1. We check that added wishes are now returned.
        final ResponseEntity<JsonNode> user1MatchResponse2 = httpClient.getMatchOfUser(1);
        Assertions.assertThat(user1MatchResponse2.getBody()).isNotNull();
        Assertions.assertThat(user1MatchResponse2.getBody().get("wishes").isArray()).isTrue();
        Assertions.assertThat(user1MatchResponse2.getBody().get("wishes").size()).isEqualTo(2);
        assertWish(user1MatchResponse2.getBody().get("wishes").get(0), "A", "https://google.com", 1);
        assertWish(user1MatchResponse2.getBody().get("wishes").get(1), "B", null, 2);

        // update wishes
        final String updatedWishesPayload = """
                [
                    {"title": "B", "power": 2},
                    {"title": "C", "power": 1}
                ]
                """;
        httpClient.updateWishlist(matchingUserNumber, updatedWishesPayload);
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
}
