package io.github.hejcz.domain.user;

public record DtoUser(Integer id, String login, String name, String surname, String password, String email,
                      SystemRole systemRole) {

    public boolean isAdmin() {
        return systemRole == SystemRole.ADMIN;
    }

    DbUser toDb() {
        return new DbUser(id, login, name, surname, email, password, systemRole);
    }

    public DtoUser withPassword(String newPassword) {
        return new DtoUser(id, login, name, surname, newPassword, email, systemRole);
    }

}

