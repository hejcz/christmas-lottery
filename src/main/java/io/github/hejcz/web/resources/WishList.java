package io.github.hejcz.web.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishList {
    Collection<Wish> wishes;
    boolean locked;
}
