package io.github.hejcz.domain.lottery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class DtoWishGiver {

    private String recipientName;

    private String recipientSurname;

    private Collection<DtoWishRecipient> recipientWishes;

}
