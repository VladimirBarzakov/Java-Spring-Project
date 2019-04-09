/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.items.editGallery;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import javax.validation.ValidationException;
import javax.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class CreateItemPictureBindingModel {
    
    @Size(min = ValidProperties.PICTURE_DESCRIPTION_MIN_LENGHT, max = ValidProperties.PICTURE_DESCRIPTION_MAX_LENGHT, message = ValidationMesseges.PICTURE_DESCRIPTION)
    private String description;
    
    MultipartFile file;

    public CreateItemPictureBindingModel() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        if(file.getSize()==0){
            throw new ValidationException(ValidationMesseges.PICTURE_NO_FILE_MESSAGE);
        }
        this.file = file;
    }
}
