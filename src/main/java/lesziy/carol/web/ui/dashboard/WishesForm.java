package lesziy.carol.web.ui.dashboard;

import lesziy.carol.domain.lottery.DtoWishRecipient;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
class WishesForm {

    private String name;

    WishesForm(Collection<DtoWishRecipient> dtoWishRecipients) {
        name = "Default";
    }
}
