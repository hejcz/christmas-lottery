package lesziy.carol.web.ui.dashboard;

import lesziy.carol.domain.user.DtoUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Musi być publiczna żeby BeanUtils miało do niej dostęp
public class UserForLottery {
    private Integer id;
    private String formattedName;
    private boolean consideredInLottery;

    static UserForLottery from(DtoUser dtoUser) {
        return new UserForLottery(
            dtoUser.id(),
            dtoUser.formatName(),
            true
        );
    }
}
