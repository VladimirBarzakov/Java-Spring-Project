/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers;

import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.exeptions.base.InvalidRoleException;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import java.sql.SQLException;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import static Atia.Shop.ShopApplication.LOGGER;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final String ERROR_PAGE_VIEW_ADRESS = "oops.html";
    private final String ERROR_DATABASE_PAGE_VIEW_ADRESS = "database-error.html";
    private final String ERROR_MAJOR_PAGE_VIEW_ADRESS = "error.html";

    private final String PARTLY_ERROR_FORMAT = ">>>ERRORHANDLE<<< User with mail \"%s\" couse an error - \"%s\", at URL - \"%s\"";

    @ExceptionHandler({ReporPartlyToUserException.class})
    public ModelAndView handleShowableException(Exception ex, Principal principal, HttpServletRequest request) {
        ModelAndView model = new ModelAndView(ERROR_PAGE_VIEW_ADRESS);
        LOGGER.warn(String.format(PARTLY_ERROR_FORMAT, principal.getName(), ex.getMessage(), this.getFullURL(request)));
        model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE, ex.getMessage());
        return model;
    }

    @ExceptionHandler({SQLException.class, ValidationException.class})
    public ModelAndView handleSQLException(Exception ex, Principal principal, HttpServletRequest request) {
        ModelAndView model = new ModelAndView(ERROR_DATABASE_PAGE_VIEW_ADRESS);
        LOGGER.error(String.format(PARTLY_ERROR_FORMAT, principal.getName(), ex.getMessage(), this.getFullURL(request)), ex);
        return model;
    }

    @ExceptionHandler({InvalidRoleException.class})
    public ModelAndView handleInvalidRoleException(Exception ex, Principal principal, HttpServletRequest request) {
        ModelAndView model = new ModelAndView(ERROR_DATABASE_PAGE_VIEW_ADRESS);
        LOGGER.error(String.format(PARTLY_ERROR_FORMAT, principal.getName(), ex.getMessage(), this.getFullURL(request)), ex);
        return model;
    }

    @ExceptionHandler({Throwable.class})
    public ModelAndView handleGeneralException(Throwable e, Principal principal, HttpServletRequest request) {
        ModelAndView model = new ModelAndView(ERROR_MAJOR_PAGE_VIEW_ADRESS);
        LOGGER.error(String.format(PARTLY_ERROR_FORMAT, principal.getName(), e.getMessage(), this.getFullURL(request)), e);

        Throwable throwable = e;
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return model;
    }

    private String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
}
