/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.emailService;

import java.util.Map;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
interface EmailService {
    
    public void sendSimpleMessage(String to, String subject, MailTemplates template, Map<String, String> props);
    
}
