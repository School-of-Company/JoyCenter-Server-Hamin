package team.startup.joycenter.domain.auth.repository;

import org.springframework.data.repository.CrudRepository;
import team.startup.joycenter.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByMemberId(Long memberId);
}
