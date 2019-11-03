package io.github.hejcz.web.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipientWishes {
    private String firstName;
    private String lastName;
    private Collection<Wish> wishes;
}
