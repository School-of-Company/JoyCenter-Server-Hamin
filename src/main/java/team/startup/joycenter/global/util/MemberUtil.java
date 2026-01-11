package team.startup.joycenter.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import team.startup.joycenter.domain.member.entity.Member;
import team.startup.joycenter.domain.member.exception.InvalidMemberPrincipalException;
import team.startup.joycenter.domain.member.exception.NotFoundMemberException;
import team.startup.joycenter.domain.member.repository.MemberRepository;
import team.startup.joycenter.global.auth.MemberDetails;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getCurrentMember(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MemberDetails memberDetails) {
            Long memberId = memberDetails.getMember().getId();

            return memberRepository.findById(memberId)
                    .orElseThrow(NotFoundMemberException::new);
        }

        throw new InvalidMemberPrincipalException();
    }
}
