package io.github.hejcz.web.ui.dashboard;

import io.github.hejcz.domain.user.DtoUser;
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
