package team.startup.joycenter.domain.post.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class NotFoundPostException extends GlobalException {
    public NotFoundPostException() {
        super(ErrorCode.NOT_FOUND_POST);
    }
}
