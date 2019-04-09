/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.exeptions.base;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class InvalidRoleException extends RuntimeException {

    /**
     * Creates a new instance of <code>InvalidRoleException</code> without
     * detail message.
     */
    public InvalidRoleException() {
    }

    /**
     * Constructs an instance of <code>InvalidRoleException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidRoleException(String msg) {
        super(msg);
    }
    
    public InvalidRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
