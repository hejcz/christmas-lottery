package io.github.hejcz.web.resources;

import java.util.List;

public record WishlistUpdateDto(int groupId, List<Wish> wishes) {
}
