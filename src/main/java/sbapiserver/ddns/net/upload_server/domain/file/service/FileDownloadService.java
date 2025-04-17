package sbapiserver.ddns.net.upload_server.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
@Service
@RequiredArgsConstructor
public class FileDownloadService {

    private final PathValidService pathValidService;

    public Path resolveAndValidate(String relativePath, String filename) throws IOException {
        return pathValidService.resolveRelativePathAndValid(relativePath, filename);
    }

    public Resource loadAsResource(Path filePath) {
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("리소스 로딩 실패: " + filePath.toString(), e);
        }
    }
}
