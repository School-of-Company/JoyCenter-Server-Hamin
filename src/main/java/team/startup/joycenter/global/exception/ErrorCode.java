package team.startup.joycenter.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    INVALID_MEMBER_PRINCIPAL(401, "현재 인증된 사용자의 정보가 유효하지 않습니다."),
    UNAUTHORIZED(401, "이메일 또는 비밀번호가 잘못되었습니다."),

    NOT_FOUND_MEMBER(404, "존재하지 않는 사용자입니다."),

    NOT_FOUND_ATTACHMENTS(404, "존재하지 않는 첨부파일입니다."),
    ATTACHMENTS_UPLOAD_FAILED(500, "첨부파일 업로드에 실패했습니다."),
    ATTACHMENTS_DELETE_FAILED(500, "첨부파일 삭제에 실패했습니다."),

    NOT_POST_OWNER(403, "게시글 작성자가 아닙니다."),
    NOT_FOUND_POST(404, "존재하지 않는 게시글입니다.");

    private final int status;
    private final String message;
}
