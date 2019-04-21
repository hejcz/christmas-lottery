package io.github.hejcz.integration.email;

import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.WishesUpdate;
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
    public void sendWishesUpdate(String giverEmail, WishesUpdate wishesUpdate) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom("meet.your.santa.app@gmail.com", "Loteria świąteczna");
            message.setTo(giverEmail);
            message.setSubject("Zmiany na liście życzeń wylosowanej osoby");
            message.setText(String.format("%s\n\n%s\n\n%s\n\n%s\n\n%s",
                "Wylosowana przez Ciebie osoba dokonała zmian na liście życzeń",
                "Poprzednia lista życzeń:",
                formatWishes(wishesUpdate.getOldWishes()),
                "Aktualna lista życzeń:",
                formatWishes(wishesUpdate.getNewWishes())));
        });
    }

    @Override
    public void sendPasswordRecovery(String giverEmail, String passwordResetUrl) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom("meet.your.santa.app@gmail.com", "Loteria świąteczna");
            message.setTo(giverEmail);
            message.setSubject("Reset hasła");
            message.setText(String.format("%s\n\n%s\n\n%s",
                "Została uruchomiona procedura zmiany hasła",
                "Aby zresetować hasło kliknij w poniższy link:",
                passwordResetUrl));
        });
    }

    private String formatWishes(List<DtoWishRecipient> wishes) {
        return wishes.stream()
            .map(DtoWishRecipient::getText)
            .map(wish -> "- " + wish)
            .collect(Collectors.joining(System.lineSeparator()));
    }

}