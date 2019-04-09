/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Pictures.Base;

import Atia.Shop.exeptions.base.ReportToUserException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface PictureCreatable<PictureDetailsFile, Entity>{
    
    public PictureDetailsFile savePicture(PictureDetailsFile pictureDetailsFile, MultipartFile file) throws ReportToUserException;
    
    List<PictureDetailsFile> getAllPicturesByEntity(Entity entity);
    
    boolean deleteSinglePicture(Entity entity, Long pictureId);
    
    boolean updateDescriptionSinglePicture(Entity entity, Long pictureId, String description);
    
}
