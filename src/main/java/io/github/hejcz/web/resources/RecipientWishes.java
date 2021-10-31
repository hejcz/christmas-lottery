package io.github.hejcz.web.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipientWishes(String firstName, String lastName, boolean locked, Collection<Wish> wishes) {
}
