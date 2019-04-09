/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Storage;

import java.io.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface StorageService {
    

    void store(MultipartFile file, String filename);
    
    void store(File file, String filename);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
    
    void deleteSingle(String filename);
    
}
