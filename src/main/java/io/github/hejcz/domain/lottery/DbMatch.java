package io.github.hejcz.domain.lottery;

import io.github.hejcz.domain.user.DbUser;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "matches")
class DbMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;

    private Timestamp creationDate;

    @ManyToOne
    private DbUser giver;

    @ManyToOne
    private DbUser recipient;

    @Column(nullable = false)
    private boolean locked;

    public DbMatch() {
    }

    public DbMatch(Integer id, Timestamp creationDate, DbUser giver, DbUser recipient, boolean locked) {
        this.id = id;
        this.creationDate = creationDate;
        this.giver = giver;
        this.recipient = recipient;
        this.locked = locked;
    }

    Match asMatch() {
        return new Match(new UserId(giver.getId()), new UserId(recipient.getId()));
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public DbUser getGiver() {
        return giver;
    }

    public DbUser getRecipient() {
        return recipient;
    }

    public boolean isLocked() {
        return locked;
    }
}
