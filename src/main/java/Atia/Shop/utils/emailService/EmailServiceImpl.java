/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.emailService;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.mailService.MailServiceConfig;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String to, String subject, MailTemplates template, Map<String, String> props) {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            String htmlMsg = template.getTemplate();
            for (Map.Entry<String, String> entry : props.entrySet()) {
                htmlMsg = htmlMsg.replace(entry.getKey(), entry.getValue());
            }
            mimeMessage.setContent(htmlMsg, "text/html");
            helper.setTo(to);
            helper.setSubject("ATIA & TIGER SHOP Notification - " + subject);
            helper.setFrom(MailServiceConfig.SENDER_MAIL);
            emailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
