package io.github.hejcz.domain.lottery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "forbidden_match")
class ForbiddenMatch {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Integer firstUserId;

    @Column(nullable = false)
    private Integer secondUserId;

    public ForbiddenMatch() {
    }

    public ForbiddenMatch(Long id, Integer firstUserId, Integer secondUserId) {
        this.id = id;
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
    }

    public Integer getFirstUserId() {
        return firstUserId;
    }

    public Integer getSecondUserId() {
        return secondUserId;
    }
}
