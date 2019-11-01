package io.github.hejcz.web.resources;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class RecipientWishes {
    private String recipient;
    private Collection<Wish> wishes;
}
