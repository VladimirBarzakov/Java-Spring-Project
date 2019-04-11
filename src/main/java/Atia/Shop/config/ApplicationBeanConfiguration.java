/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.config;

import Atia.Shop.config.mailService.MailServiceConfig;
import Atia.Shop.utils.pictureStorage.AuctionPictureStorageProperties;
import Atia.Shop.utils.pictureStorage.ItemsPictureStorageProperties;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.Properties;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Configuration
public class ApplicationBeanConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator;
    }

    @Bean
    public ItemsPictureStorageProperties itemsImageStorageProperties() {
        ItemsPictureStorageProperties storageProperties = new ItemsPictureStorageProperties();
        return storageProperties;
    }

    @Bean
    public AuctionPictureStorageProperties auctionsImageStorageProperties() {
        AuctionPictureStorageProperties storageProperties = new AuctionPictureStorageProperties();
        return storageProperties;
    }

    @Bean
    public InputValidator inputValidator() {
        return new InputValidator();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("tsrv01.atia.com");
        mailSender.setPort(587);

        mailSender.setUsername(MailServiceConfig.SENDER_MAIL);
        mailSender.setPassword(MailServiceConfig.SENDER_MAIL_PASS);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.trust", "tsrv01.atia.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }

}
