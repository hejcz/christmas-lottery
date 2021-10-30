package io.github.hejcz.web.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecipientWishes(@JsonProperty("firstName") String firstName,
                              @JsonProperty("lastName") String lastName,
                              @JsonProperty("locked") boolean locked,
                              @JsonProperty("wishes") Collection<Wish> wishes) {
}
