package io.github.hejcz.web.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.hejcz.domain.lottery.DtoWishRecipient;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Wish(Integer id, String title, String url, Integer power) {

    public DtoWishRecipient toOldDto() {
        return new DtoWishRecipient(id, title, url, power);
    }
}
