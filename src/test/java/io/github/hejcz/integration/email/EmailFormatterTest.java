package io.github.hejcz.integration.email;

import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.WishListChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class EmailFormatterTest {

    @Test
    void shouldRenderCorrectWishListForNewList() {
        String tested = EmailFormatter.formatWishlistChange(new WishListChange(List.of(), List.of(
                        new DtoWishRecipient(0, "szampon", "http://sklepik.com/szampn/1243", 1),
                        new DtoWishRecipient(0, "fajka", "http://szlugi.com/fajki/221", 2))),
                "https://lottery.com");

        Assertions.assertEquals("""
                Nowa lista życzeń:

                - szampon (http://sklepik.com/szampn/1243)
                - fajka (http://szlugi.com/fajki/221)
                
                Przejdź do loterii: https://lottery.com
                                """, tested);
    }

    @Test
    void shouldRenderCorrectWishListForNullList() {
        String tested = EmailFormatter.formatWishlistChange(new WishListChange(null, List.of(
                        new DtoWishRecipient(0, "szampon", "http://sklepik.com/szampn/1243", 1),
                        new DtoWishRecipient(0, "fajka", "http://szlugi.com/fajki/221", 2))),
                "https://lottery.com");

        Assertions.assertEquals("""
                Nowa lista życzeń:

                - szampon (http://sklepik.com/szampn/1243)
                - fajka (http://szlugi.com/fajki/221)

                Przejdź do loterii: https://lottery.com
                                """, tested);
    }

    @Test
    void shouldRenderCorrectWishListForUpdatedList() {
        String tested = EmailFormatter.formatWishlistChange(new WishListChange(
                        List.of(
                                new DtoWishRecipient(0, "szamponetka", "http://sklepik.com/szamponetka/222", 1),
                                new DtoWishRecipient(0, "fajka", "http://szlugi.com/fajki/221", 2)),
                        List.of(
                                new DtoWishRecipient(0, "szampon", "http://sklepik.com/szampn/1243", 1),
                                new DtoWishRecipient(0, "fajka", "http://szlugi.com/fajki/221", 2))),
                "https://lottery2.com");

        Assertions.assertEquals("""
                Poprzednia lista życzeń:

                - szamponetka (http://sklepik.com/szamponetka/222)
                - fajka (http://szlugi.com/fajki/221)
                                
                Nowa lista życzeń:

                - szampon (http://sklepik.com/szampn/1243)
                - fajka (http://szlugi.com/fajki/221)
                
                Przejdź do loterii: https://lottery2.com
                                """, tested);
    }

}