package team.startup.joycenter.domain.auth.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class UnauthorizedException extends GlobalException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
