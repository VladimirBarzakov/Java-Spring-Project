/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.storage;

import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.exeptions.storage.StorageFileNotFoundException;
import Atia.Shop.service.IMPL.Storage.AuctionPictureStorageService;
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
@RequestMapping(WebConstrants.AUCTION_PIC_WEB_ROUTE)
public class AuctionPictureUploadController {

    private final static String PREFIX_URL = "pics/auctions";

    private final AuctionPictureStorageService storageService;

    @Autowired
    public AuctionPictureUploadController(AuctionPictureStorageService storageService) {
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
        return ResponseEntity.notFound().build();
    }
}
