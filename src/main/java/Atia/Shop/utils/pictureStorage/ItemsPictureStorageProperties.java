/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.utils.pictureStorage;

import Atia.Shop.config.storage.StorageVariables;
import Atia.Shop.utils.pictureStorage.Base.StorageProperties;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class ItemsPictureStorageProperties implements StorageProperties{
    
    private final String location = StorageVariables.STORAGE_ITEMS_LOCATION;
    
    @Override
    public String getLocation() {
        return location;
    }    
}
