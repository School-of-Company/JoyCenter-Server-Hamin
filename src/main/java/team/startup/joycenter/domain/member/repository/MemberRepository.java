package team.startup.joycenter.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.startup.joycenter.domain.member.entity.Member;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(String findByProvider, String ProviderId);
}
