package lesziy.carol.api.lottery;

import lombok.Value;

import java.util.Map;

/**
 * Na potrzeby Hungarian algorithm każdemu użytkownikowi z puli do losowania
 * należy przypisać jedną z kolejnych liczb naturalnych rozpoczynając od 0.
 * Klasa reprezentuje to mapowanie.
 */
@Value
class OrderedUsers {
    Map<Integer, User> ordinalToUser;
    Map<User, OrderedUser> userToOrderedUser;
}
