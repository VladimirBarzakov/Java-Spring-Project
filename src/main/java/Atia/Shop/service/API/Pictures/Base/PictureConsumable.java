/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Pictures.Base;

import Atia.Shop.exeptions.base.ReportToUserException;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface PictureConsumable<PictureDetailsFile, Entity> {
    
    PictureDetailsFile addPicture(PictureDetailsFile pictureDetailsFile, String consummablePictureId, String sellerMail) throws ReportToUserException;
    
    boolean deletePicture(PictureDetailsFile pictureDetailsFile,  String sellerMail) throws ReportToUserException;
    
    boolean adminDeletePicture(PictureDetailsFile pictureDetailsFile) throws ReportToUserException;
    
    List<PictureDetailsFile> getAllPicturesByEntity(Entity entity);
    
    
}
