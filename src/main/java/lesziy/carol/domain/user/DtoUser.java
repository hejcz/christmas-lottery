package lesziy.carol.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class DtoUser {
    private Integer id;
    private String login;
    private String name;
    private String surname;
    private String password;
    private String email;
    private SystemRole systemRole;

    public boolean isAdmin() {
        return systemRole == SystemRole.ADMIN;
    }

    DbUser toDb() {
        return new DbUser(
            id,
            login,
            name,
            surname,
            email,
            password,
            systemRole
        );
    }

    public String formatName() {
        return String.format("%s %s", name, surname);
    }
}

