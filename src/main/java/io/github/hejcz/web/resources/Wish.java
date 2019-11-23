package io.github.hejcz.web.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.hejcz.domain.lottery.DtoWishRecipient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wish {
    private Integer id;
    private String title;
    private String url;
    private Integer power;

    public DtoWishRecipient toOldDto() {
        return new DtoWishRecipient(id, title, url, power);
    }
}
