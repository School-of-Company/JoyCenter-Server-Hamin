package team.startup.joycenter.domain.attachments.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class AttachmentsFileSizeExceededException extends GlobalException {
    public AttachmentsFileSizeExceededException() {
        super(ErrorCode.ATTACHMENTS_FILE_SIZE_EXCEEDED);
    }
}
