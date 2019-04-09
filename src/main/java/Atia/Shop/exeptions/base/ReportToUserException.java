/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.exeptions.base;

import org.springframework.stereotype.Component;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Component
public class ReportToUserException extends Exception  {

    /**
     * Creates a new instance of <code>ReportToUserException</code> without
     * detail message.
     */
    public ReportToUserException() {
    }

    /**
     * Constructs an instance of <code>ReportToUserException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReportToUserException(String msg) {
        super(msg);
    }
}
