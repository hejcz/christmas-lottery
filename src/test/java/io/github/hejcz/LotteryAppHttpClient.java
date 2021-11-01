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

public class LotteryAppHttpClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final int port;

    public LotteryAppHttpClient(int port) {
        this.port = port;
    }

    ResponseEntity<Void> deleteLottery() {
        return sendAsAdmin(HttpMethod.DELETE, "/api/lottery", Void.class, null);
    }

    ResponseEntity<Boolean> getLotteryStatus(int groupId) {
        return sendAsAdmin(HttpMethod.GET, "/api/lottery/admin?groupId=" + groupId, Boolean.class, groupId);
    }

    ResponseEntity<Void> startLottery(String payload) {
        return sendAsAdmin(HttpMethod.PUT, "/api/lottery", Void.class, payload);
    }

    ResponseEntity<JsonNode> getMatchOfUser(int testUserSuffix, int groupId) {
        return sendAsUser(testUserSuffix, HttpMethod.GET, "/api/lottery?groupId=" + groupId, JsonNode.class, null);
    }

    ResponseEntity<Void> updateWishlist(int testUserSuffix, String wishesPayload) {
        return sendAsUser(testUserSuffix, HttpMethod.PUT, "/api/ids/current/wish-list", Void.class, wishesPayload);
    }

    ResponseEntity<JsonNode> getWishesOf(int testUserSuffix, int groupId) {
        return sendAsUser(testUserSuffix, HttpMethod.GET, "/api/ids/current/wish-list?groupId=" + groupId,
                JsonNode.class, null);
    }

    public void addGroup(String payload) {
        sendAsAdmin(HttpMethod.POST, "/api/groups", Void.class, payload);
    }

    public ResponseEntity<JsonNode> getGroups() {
        return sendAsAdmin(HttpMethod.GET, "/api/groups", JsonNode.class, null);
    }

    private <T, R> ResponseEntity<R> sendAsAdmin(HttpMethod method, String path, Class<R> returnType, T entity) {
        final ResponseEntity<R> exchange = restTemplate.exchange(getUrl(path), method,
                new HttpEntity<>(entity, adminHeaders()), returnType);
        Assertions.assertThat(exchange.getStatusCodeValue()).isGreaterThanOrEqualTo(200);
        Assertions.assertThat(exchange.getStatusCodeValue()).isLessThan(300);
        return exchange;
    }

    private <T, R> ResponseEntity<R> sendAsUser(int userId, HttpMethod method, String path,
                                                Class<R> returnType, T entity) {
        final ResponseEntity<R> exchange = restTemplate.exchange(getUrl(path), method,
                new HttpEntity<>(entity, headers("User" + userId, "user" + userId)), returnType);
        Assertions.assertThat(exchange.getStatusCodeValue()).isGreaterThanOrEqualTo(200);
        Assertions.assertThat(exchange.getStatusCodeValue()).isLessThan(300);
        return exchange;
    }

    private String getUrl(String path) {
        return "http://localhost:" + port + path;
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

    public <R> ResponseEntity<R> exchange(HttpMethod method, String path,
                                          String login, String password, String payload, Class<R> returnType) {
        return restTemplate.exchange(getUrl(path), method,
                new HttpEntity<>(payload, headers(login, password)), returnType);
    }
}
