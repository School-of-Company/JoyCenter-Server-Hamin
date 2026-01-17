package team.startup.joycenter.domain.post.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class NotPostOwnerException extends GlobalException {
    public NotPostOwnerException() {
        super(ErrorCode.NOT_POST_OWNER);
    }
}
