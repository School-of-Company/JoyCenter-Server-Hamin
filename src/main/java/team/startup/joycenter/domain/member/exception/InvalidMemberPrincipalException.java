package team.startup.joycenter.domain.member.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class InvalidMemberPrincipalException extends GlobalException {
    public InvalidMemberPrincipalException() {
        super(ErrorCode.INVALID_MEMBER_PRINCIPAL);
    }
}
