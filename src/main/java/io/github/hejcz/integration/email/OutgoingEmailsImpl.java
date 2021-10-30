package io.github.hejcz.integration.email;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.github.hejcz.domain.lottery.WishListChange;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

@Service
class OutgoingEmailsImpl implements OutgoingEmails {

    private final Mustache wishesChanged;
    private final Mustache resetPassword;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public OutgoingEmailsImpl(MailProperties mailProperties, JavaMailSender javaMailSender) {
        this.mailProperties = mailProperties;
        MustacheFactory mf = new DefaultMustacheFactory();
        this.wishesChanged = mf.compile("wishes_changed_email.pl.mustache");
        this.resetPassword = mf.compile("password_reset.pl.mustache");
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendWishesUpdate(String giverEmail, WishListChange wishListChange) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = createMessage(mimeMessage);
            fillSender(message);
            message.setTo(giverEmail);
            message.setSubject("Zmiany na liście życzeń wylosowanej osoby");
            message.setText(renderMustache(wishesChanged, wishListChange));
        });
    }

    @Override
    public void sendPasswordRecovery(String giverEmail, String passwordResetUrl) {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper message = createMessage(mimeMessage);
            fillSender(message);
            message.setTo(giverEmail);
            message.setSubject("Reset hasła");
            message.setText(renderMustache(resetPassword, passwordResetUrl));
        });
    }

    private MimeMessageHelper createMessage(MimeMessage mimeMessage) throws MessagingException {
        return new MimeMessageHelper(mimeMessage, true, "UTF-8");
    }

    private void fillSender(MimeMessageHelper message) throws MessagingException, UnsupportedEncodingException {
        message.setFrom(mailProperties.getUsername(), "Loteria świąteczna");
    }

    private String renderMustache(Mustache mustache, Object context) {
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

}
