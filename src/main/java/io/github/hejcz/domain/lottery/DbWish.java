package io.github.hejcz.domain.lottery;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "wishes")
class DbWish {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;

    @CreationTimestamp
    private Timestamp creationDate;

    @Column(nullable = false)
    private String text;

    @Column
    private String url;

    @Column(nullable = false)
    private Integer power;

    @ManyToOne(fetch = FetchType.EAGER)
    private DbMatch match;

    DtoWishRecipient toDto() {
        return new DtoWishRecipient(id, text, url, power);
    }

    public DbWish() {
    }

    public DbWish(Integer id, Timestamp creationDate, String text, String url, Integer power, DbMatch match) {
        this.id = id;
        this.creationDate = creationDate;
        this.text = text;
        this.url = url;
        this.power = power;
        this.match = match;
    }

    public String getText() {
        return text;
    }
}
