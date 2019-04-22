package io.github.hejcz.domain.lottery;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "forbidden_match")
class ForbiddenMatch {

    @Id
    @Column(nullable = false)
    Long id;

    @Column(nullable = false)
    Integer firstUserId;

    @Column(nullable = false)
    Integer secondUserId;

}
