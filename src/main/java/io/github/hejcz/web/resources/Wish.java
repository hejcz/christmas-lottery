package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.DtoWishRecipient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Wish {
    private Integer id;
    private String text;
    private String url;
    private Integer power;

    public DtoWishRecipient toOldDto() {
        return new DtoWishRecipient(id, text, url, power);
    }
}
