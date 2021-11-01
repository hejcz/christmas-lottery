package io.github.hejcz;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class LotteryAppHttpClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final int port;

    public LotteryAppHttpClient(int port) {
        this.port = port;
    }

    ResponseEntity<Void> deleteLottery() {
        return sendAsAdmin(HttpMethod.DELETE, "/api/lottery", Void.class, null);
    }

    ResponseEntity<Boolean> getLotteryStatus() {
        return sendAsAdmin(HttpMethod.GET, "/api/lottery/admin", Boolean.class, null);
    }

    ResponseEntity<Void> startLottery(List<Integer> userIds) {
        return sendAsAdmin(HttpMethod.PUT, "/api/lottery", Void.class, userIds);
    }

    ResponseEntity<JsonNode> getMatchOfUser(int testUserSuffix) {
        return sendAsUser(testUserSuffix, HttpMethod.GET, "/api/lottery", JsonNode.class, null);
    }

    ResponseEntity<Void> updateWishlist(int testUserSuffix, String wishesPayload) {
        return sendAsUser(testUserSuffix, HttpMethod.PUT, "/api/users/current/wish-list", Void.class, wishesPayload);
    }

    ResponseEntity<JsonNode> getWishesOf(int testUserSuffix) {
        return sendAsUser(testUserSuffix, HttpMethod.GET, "/api/users/current/wish-list", JsonNode.class, null);
    }

    private <T, R> ResponseEntity<R> sendAsAdmin(HttpMethod method, String path, Class<R> returnType, T entity) {
        final ResponseEntity<R> exchange = restTemplate.exchange("http://localhost:" + port + path, method,
                new HttpEntity<>(entity, adminHeaders()), returnType);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
        return exchange;
    }

    private <T, R> ResponseEntity<R> sendAsUser(int userId, HttpMethod method, String path,
                                                 Class<R> returnType, T entity) {
        final ResponseEntity<R> exchange = restTemplate.exchange("http://localhost:" + port + path, method,
                new HttpEntity<>(entity, headers("User" + userId, "user" + userId)), returnType);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
        return exchange;
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
