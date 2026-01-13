package team.startup.joycenter.domain.attachments.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class AttachmentsDeleteFailedException extends GlobalException {
    public AttachmentsDeleteFailedException() {
        super(ErrorCode.ATTACHMENTS_DELETE_FAILED);
    }
}
