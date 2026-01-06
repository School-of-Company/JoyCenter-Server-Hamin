package team.startup.joycenter.domain.member.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class NotFoundMemberException extends GlobalException {
    public NotFoundMemberException() {
        super(ErrorCode.NOT_FOUND_MEMBER);
    }
}
