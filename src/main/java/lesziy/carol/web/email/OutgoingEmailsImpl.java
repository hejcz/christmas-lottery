package lesziy.carol.web.email;

import lesziy.carol.domain.lottery.DtoWishRecipient;
import lesziy.carol.domain.lottery.WishesUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class OutgoingEmailsImpl implements OutgoingEmails {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(String giverEmail, WishesUpdate wishesUpdate) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom("meet.your.santa.app@gmail.com", "Loteria świąteczna");
            message.setTo(giverEmail);
            message.setSubject("Zmiany na liście życzeń wylosowanej osoby");
            message.setText(String.format("%s\n\n%s\n\n%s\n\n%s\n\n%s",
                "Wylosowana przez Ciebie osoba dokonała zmian na liście życzeń",
                "Lista życzeń przed edycją:",
                formatWishes(wishesUpdate.getOldWishes()),
                "Lista życzeń po edycji:",
                formatWishes(wishesUpdate.getNewWishes())));
        });
    }

    private String formatWishes(List<DtoWishRecipient> wishes) {
        return wishes.stream()
            .map(DtoWishRecipient::getText)
            .map(wish -> "- " + wish)
            .collect(Collectors.joining(System.lineSeparator()));
    }

}
