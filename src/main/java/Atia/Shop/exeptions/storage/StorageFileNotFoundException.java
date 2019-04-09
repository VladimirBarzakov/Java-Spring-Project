/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.exeptions.storage;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class StorageFileNotFoundException extends StorageException {
    
    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
