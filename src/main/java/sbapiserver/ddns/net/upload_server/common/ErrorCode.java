package sbapiserver.ddns.net.upload_server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    FOLDER_NOT_FOUND(404,"FOL_001", "폴더를 찾을 수 없습니다.");

    private final int status;
    private final String divisionCode;
    private final String reason;
}
