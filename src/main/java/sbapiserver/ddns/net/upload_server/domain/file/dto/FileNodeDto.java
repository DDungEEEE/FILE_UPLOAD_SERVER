package sbapiserver.ddns.net.upload_server.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileNodeDto {
    private String name;
    private String type; // "FOLDER" or "FILE"
    private Long size; //파일일 때만
    private String lastModified;
}
