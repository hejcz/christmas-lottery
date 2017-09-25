package lesziy.carol.web.ui.dashboard;

import lesziy.carol.domain.user.DtoUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
class LotteryForm {
    private List<UserForLottery> usersForLottery;

    LotteryForm(Collection<DtoUser> users) {
        usersForLottery = users.stream()
            .map(UserForLottery::from)
            .collect(Collectors.toList());
    }
}
