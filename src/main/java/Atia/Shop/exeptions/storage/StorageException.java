/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.exeptions.storage;

import Atia.Shop.exeptions.base.ReporPartlyToUserException;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class StorageException extends ReporPartlyToUserException{
    
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
