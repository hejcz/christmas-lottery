package io.github.hejcz.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tokens")
class DbPasswordRecoveryToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String token;

    @Column(nullable = false)
    private String email;

    public DbPasswordRecoveryToken() {
    }

    public DbPasswordRecoveryToken(Integer id, String token, String email) {
        this.id = id;
        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }
}
