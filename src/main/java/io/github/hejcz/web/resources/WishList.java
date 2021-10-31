package io.github.hejcz.web.resources;

import java.util.Collection;

public record WishList(Collection<Wish> wishes, boolean locked) {
}
