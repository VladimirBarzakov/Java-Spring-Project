/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.storage;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.exeptions.storage.StorageFileNotFoundException;
import Atia.Shop.service.IMPL.Storage.ItemPictureStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping(WebConstrants.ITEM_PIC_WEB_ROUTE)
public class ItemPictureUploadController {

    private final static String PREFIX_URL = "pics/items";

    private final ItemPictureStorageService storageService;

    @Autowired
    public ItemPictureUploadController(ItemPictureStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        HttpHeaders headers = new HttpHeaders();
        Resource resource
                = storageService.loadAsResource(filename);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        LOGGER.error(exc.getMessage(), exc);
        return ResponseEntity.notFound().build();
    }
}
