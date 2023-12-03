package io.github.hejcz.integration.email;

import io.github.hejcz.domain.lottery.WishListChange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
class OutgoingEmailsImpl implements OutgoingEmails {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    private final String websiteAddress;

    public OutgoingEmailsImpl(MailProperties mailProperties, JavaMailSender javaMailSender,
            @Value("${santa.url}") String websiteAddress) {
        this.mailProperties = mailProperties;
        this.javaMailSender = javaMailSender;
        this.websiteAddress = websiteAddress;
    }

    @Override
    public void sendWishesUpdate(String giverEmail, WishListChange wishListChange) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = createMessage(mimeMessage);
            fillSender(message);
            message.setTo(giverEmail);
            message.setSubject("Zmiany na liście życzeń wylosowanej osoby");
            message.setText(EmailFormatter.formatWishlistChange(wishListChange, websiteAddress));
        });
    }

    @Override
    public void sendPasswordRecovery(String giverEmail, String passwordResetUrl) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = createMessage(mimeMessage);
            fillSender(message);
            message.setTo(giverEmail);
            message.setSubject("Reset hasła");
            message.setText(EmailFormatter.formatPasswordReset(passwordResetUrl));
        });
    }

    private MimeMessageHelper createMessage(MimeMessage mimeMessage) throws MessagingException {
        return new MimeMessageHelper(mimeMessage, true, "UTF-8");
    }

    private void fillSender(MimeMessageHelper message) throws MessagingException, UnsupportedEncodingException {
        message.setFrom(mailProperties.getUsername(), "Loteria świąteczna");
    }

}
