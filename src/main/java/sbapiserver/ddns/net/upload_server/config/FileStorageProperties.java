package sbapiserver.ddns.net.upload_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileStorageProperties {
    @Value("${file.root-dir}")
    private String rootDir;

    public Path getRootPath(){
        return Paths.get(rootDir).toAbsolutePath().normalize();
    }
}
