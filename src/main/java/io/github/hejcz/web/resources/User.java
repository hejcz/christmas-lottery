package io.github.hejcz.web.resources;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    Integer id;
    String firstName;
    String lastName;
}
