package lesziy.carol.domain.lottery;

import lesziy.carol.domain.user.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    @Column(nullable = false)
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    private DbUser recipient;

    DtoWishRecipient toDto() {
        return new DtoWishRecipient(id, text);
    }
}
