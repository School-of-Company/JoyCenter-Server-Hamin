package team.startup.joycenter.domain.auth.exception;

import team.startup.joycenter.global.exception.ErrorCode;
import team.startup.joycenter.global.exception.GlobalException;

public class Oauth2AuthorizationFailedException extends GlobalException {
    public Oauth2AuthorizationFailedException() {
        super(ErrorCode.OAUTH2_AUTHORIZATION_FAILED);
    }
}
