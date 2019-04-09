/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.Storage;

import Atia.Shop.service.IMPL.Base.BaseStorageService;
import Atia.Shop.utils.pictureStorage.AuctionPictureStorageProperties;
import Atia.Shop.utils.valdiation.InputValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Service
public class AuctionPictureStorageService extends BaseStorageService{
    
    @Autowired
    public AuctionPictureStorageService(AuctionPictureStorageProperties properties, InputValidator inputValidator) {
        super(properties, inputValidator);
    }
    
}
