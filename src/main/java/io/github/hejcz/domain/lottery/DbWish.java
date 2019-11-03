package io.github.hejcz.domain.lottery;

import io.github.hejcz.domain.user.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Data
@Entity
@Table(name = "wishes")
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = false)
    private boolean locked;

    @ManyToOne(fetch = FetchType.EAGER)
    private DbUser recipient;

    DtoWishRecipient toDto() {
        return new DtoWishRecipient(id, text, url == null ? "" : url, power, locked);
    }

}
