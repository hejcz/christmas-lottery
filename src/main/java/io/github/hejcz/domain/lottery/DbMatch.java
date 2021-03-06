package io.github.hejcz.domain.lottery;

import io.github.hejcz.domain.user.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "matches")
@NoArgsConstructor
@AllArgsConstructor
class DbMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;

    @CreationTimestamp
    private Timestamp creationDate;

    @ManyToOne
    private DbUser giver;

    @ManyToOne
    private DbUser recipient;

    @Column(nullable = false)
    private boolean locked;

    DbMatch(DbUser giver, DbUser recipient) {
        this.giver = giver;
        this.recipient = recipient;
        this.locked = false;
    }

    Match asMatch() {
        return new Match(User.with(giver.getId()), User.with(recipient.getId()));
    }

}
