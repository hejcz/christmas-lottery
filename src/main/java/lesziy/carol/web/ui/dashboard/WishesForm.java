package lesziy.carol.web.ui.dashboard;

import lesziy.carol.domain.lottery.DtoWishRecipient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
class WishesForm {
    @NotNull
    public DtoWishRecipient[] wishes;

    WishesForm(Collection<DtoWishRecipient> wishes) {
        this.wishes = wishes.toArray(new DtoWishRecipient[wishes.size()]);
    }
}
