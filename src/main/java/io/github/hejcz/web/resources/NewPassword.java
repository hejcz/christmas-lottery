package io.github.hejcz.web.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.hejcz.domain.lottery.DtoWishRecipient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewPassword {
    private String token;
    private String newPassword;
}
