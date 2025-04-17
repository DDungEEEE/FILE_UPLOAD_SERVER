package sbapiserver.ddns.net.upload_server.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileBrowseResponse {
    private boolean success;
    private String message;
    private String currentPath;
    private List<FolderDto> folders;
    private List<FileDto> files;
}