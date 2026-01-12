package team.startup.joycenter.domain.attachments.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class AttachmentsUploadFailedException extends GlobalException {
    public AttachmentsUploadFailedException() {
        super(ErrorCode.ATTACHMENTS_UPLOAD_FAILED);
    }
}
