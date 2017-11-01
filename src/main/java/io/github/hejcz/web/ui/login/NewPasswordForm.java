package io.github.hejcz.web.ui.login;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class NewPasswordForm {
    private String password;
    private String repeatedPassword;
}
