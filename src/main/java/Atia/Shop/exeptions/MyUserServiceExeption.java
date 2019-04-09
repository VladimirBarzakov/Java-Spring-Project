/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.exeptions;

import Atia.Shop.exeptions.base.ReportToUserException;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class MyUserServiceExeption extends ReportToUserException {

    /**
     * Creates a new instance of <code>MyDBaseUniqueValidationExeption</code>
     * without detail message.
     */
    public MyUserServiceExeption() {
    }

    /**
     * Constructs an instance of <code>MyDBaseUniqueValidationExeption</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MyUserServiceExeption(String msg) {
        super(msg);
    }
}
