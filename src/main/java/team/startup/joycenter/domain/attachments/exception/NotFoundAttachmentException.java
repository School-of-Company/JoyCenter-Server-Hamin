package team.startup.joycenter.domain.attachments.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class NotFoundAttachmentException extends GlobalException {
    public NotFoundAttachmentException() {
        super(ErrorCode.NOT_FOUND_ATTACHMENTS);
    }
}
