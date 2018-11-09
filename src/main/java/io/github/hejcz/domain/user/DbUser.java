package io.github.hejcz.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class DbUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;
    @Column(nullable = false)
    private String login;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private SystemRole systemRole;

    DtoUser toDto() {
        return new DtoUser(id, login, name, surname, password, email, systemRole);
    }

    public String formatName() {
        return String.format("%s %s", getName(), getSurname());
    }
}
