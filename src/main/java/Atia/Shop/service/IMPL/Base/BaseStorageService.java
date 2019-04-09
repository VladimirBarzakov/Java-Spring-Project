/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.Base;

import Atia.Shop.exeptions.storage.StorageException;
import Atia.Shop.exeptions.storage.StorageFileNotFoundException;
import Atia.Shop.service.API.Storage.StorageService;
import Atia.Shop.utils.pictureStorage.Base.StorageProperties;
import Atia.Shop.utils.valdiation.InputValidator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public abstract class BaseStorageService implements StorageService {

    private final Path rootLocation;
    private final InputValidator inputValidator;

    public BaseStorageService(StorageProperties properties, InputValidator inputValidator) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.inputValidator=inputValidator;
    }

    protected void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file, String filename) {
        filename = StringUtils.cleanPath(filename);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException("Cannot store file with relative path outside current directory "+ filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void store(File file, String filename) {
        if (file.length()==0) {
            throw new StorageException("Failed to store empty file " + filename);
        }
        if (filename.contains("..")) {
            // This is a security check
            throw new StorageException("Cannot store file with relative path outside current directory "+ filename);
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            Files.copy(inputStream, this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        this.inputValidator.validateString(filename);
        if (filename.contains("..")) {
            // This is a security check
            throw new StorageException("Cannot load file with relative path outside current directory "+ filename);
        }
        filename = StringUtils.cleanPath(filename);
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = this.load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void deleteSingle(String filename) {
        try {
            Path path = this.load(filename);
            Files.delete(path);
        } catch (IOException ex) {
            throw new StorageFileNotFoundException("Could not delete file: " + filename);
        }
    }
}
