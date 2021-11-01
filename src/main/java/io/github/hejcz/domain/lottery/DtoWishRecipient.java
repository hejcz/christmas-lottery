package io.github.hejcz.domain.lottery;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public record DtoWishRecipient(Integer id, String text, String url, Integer power) {

    DbWish toDb(DbMatch match) {
        return new DbWish(id, Timestamp.valueOf(LocalDateTime.now()), text, url, power, match);
    }

    // TODO we use Sets.difference so we don't want to rely on id (why?) but it's kinda hacky.
    // we can wrap this object in another one.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DtoWishRecipient that = (DtoWishRecipient) o;
        return Objects.equals(text, that.text) && Objects.equals(url, that.url) && Objects.equals(power, that.power);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, url, power);
    }
}
