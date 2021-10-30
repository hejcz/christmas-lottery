package io.github.hejcz.domain.lottery;

import java.util.Map;

/**
 * Na potrzeby algorytmu węgierskiego każdemu użytkownikowi z puli do losowania
 * należy przypisać jedną z kolejnych liczb naturalnych rozpoczynając od 0.
 * Klasa reprezentuje to mapowanie.
 */
record OrderedUsers(Map<Integer, UserId> ordinalToUser,
                    Map<Integer, OrderedUser> userToOrderedUser) {
}
