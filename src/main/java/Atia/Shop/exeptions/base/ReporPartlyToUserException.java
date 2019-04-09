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
public class ReporPartlyToUserException extends RuntimeException  {

    /**
     * Creates a new instance of <code>ReportToSystemException</code> without
     * detail message.
     */
    public ReporPartlyToUserException() {
    }

    /**
     * Constructs an instance of <code>ReportToSystemException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReporPartlyToUserException(String msg) {
        super(msg);
    }

    public ReporPartlyToUserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
