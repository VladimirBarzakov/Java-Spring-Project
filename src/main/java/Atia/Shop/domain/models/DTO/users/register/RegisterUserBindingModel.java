/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.users.register;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class RegisterUserBindingModel {

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Pattern(regexp = ValidProperties.USER_DOMAIN_MAIL_PATTERN, message = ValidationMesseges.INVALID_USER_REG_DOMAIN_EMAIL_MESSAGE)
    private String domainEmail;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Pattern(regexp = ValidProperties.USER_NAME_PATTERN, message = ValidationMesseges.USER_NAME)
    private String name;

    // TODO - Validation after LDAP domain integration
    @NotNull(message = ValidationMesseges.NULL_VALUE)
    private String testPassword;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    private String confirmTestPassword;

    public RegisterUserBindingModel() {
    }

    public String getDomainEmail() {
        return domainEmail;
    }

    public void setDomainEmail(String domainEmail) {
        this.domainEmail = domainEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getTestPassword() {
        return testPassword;
    }

    public void setTestPassword(String testPassword) {
        this.testPassword = testPassword.trim();
    }

    public String getConfirmTestPassword() {
        return confirmTestPassword;
    }

    public void setConfirmTestPassword(String confirmTestPassword) {
        this.confirmTestPassword = confirmTestPassword;
    }

}
